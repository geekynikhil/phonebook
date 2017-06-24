package com.shree.phonebook;

import static com.shree.phonebook.JsonUtil.toJson;
import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.notFound;
import static spark.Spark.path;
import static spark.Spark.staticFileLocation;

public class Main
{
    public static void main(String[] args)
    {
        staticFileLocation("/public");
        notFound((req, res) ->
        {
            res.type("application/json");
            return "{\"message\":\"Resource Not Found\"}";
        });


        // Initialize dependent services
        GroupService groupService = new GroupService();
        ContactService contactService = new ContactService(groupService);

        ContactController contactController = new ContactController(contactService);
        GroupController groupController = new GroupController(groupService);

        path("/api", () ->
        {
            contactController.mount("/contacts");
            groupController.mount("/groups");
        });

        after((req, res) ->
        {
            res.type("application/json");
        });

        exception(IllegalArgumentException.class, (e, req, res) ->
        {
            res.status(400);
            res.body(toJson(new ResponseError(e)));
        });
    }
}

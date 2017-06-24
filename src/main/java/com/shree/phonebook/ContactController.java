package com.shree.phonebook;

import java.util.Arrays;

import spark.Spark;

import static spark.Spark.exception;
import static spark.Spark.path;


public class ContactController
{
    private ContactService contactService;

    public ContactController(final ContactService contactService)
    {
        this.contactService = contactService;
    }

    public void mount(String resourcePath)
    {
        path(resourcePath, () ->
        {
            Spark.post("", (req, res) ->
            {
                String[] groupIDs = req.queryParamsValues("groupId[]");
                if (groupIDs == null)
                {
                    groupIDs = new String[0];
                }

                return contactService.createUser(
                        req.queryParams("name"),
                        req.queryParams("email"),
                        req.queryParams("phone"),
                        Arrays
                                .stream(groupIDs)
                                .mapToInt(Integer::parseInt).toArray()
                );
            }, JsonUtil.json());

            Spark.get("", (req, res) -> contactService.getAllUsers(), JsonUtil.json());

            Spark.get("/:id", (req, res) ->
            {
                int id = Integer.parseInt(req.params(":id"));
                Contact contact = contactService.getUser(id);
                if (contact != null)
                {
                    return contact;
                }
                res.status(400);
                return new ResponseError("No user with id '%s' found", String.valueOf(id));
            }, JsonUtil.json());

            Spark.put("/:id", (req, res) ->
            {
                String[] groupIDs = req.queryParamsValues("groupId[]");
                if (groupIDs == null)
                {
                    groupIDs = new String[0];
                }
                return contactService.updateUser(
                        Integer.parseInt(req.params(":id")),
                        req.queryParams("name"),
                        req.queryParams("email"),
                        req.queryParams("phone"),
                        Arrays
                                .stream(groupIDs)
                                .mapToInt(Integer::parseInt).toArray()
                );
            }, JsonUtil.json());

            Spark.delete("/:id", (req, res) ->
            {
                int id = Integer.parseInt(req.params(":id"));
                Contact contact = contactService.getUser(id);
                if (contact != null)
                {
                    contactService.deleteUser(id);
                    return "";
                }
                else
                {
                    res.status(400);
                    return new ResponseError("No user with id '%s' found", String.valueOf(id));
                }
            }, JsonUtil.json());
        });


        exception(IllegalArgumentException.class, (e, req, res) ->
        {
            res.status(400);
            res.body(JsonUtil.toJson(new ResponseError(e)));
        });
    }
}

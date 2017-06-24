package com.shree.phonebook;

import static com.shree.phonebook.JsonUtil.json;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;
import static spark.Spark.put;


public class GroupController
{
    private GroupService groupService;

    public GroupController(final GroupService groupService)
    {
        this.groupService = groupService;
    }

    public void mount(String resourceString)
    {
        path(resourceString, () ->
        {
            get("", (req, res) -> groupService.getAllGroups(), json());

            post("", (req, res) ->
            {
                return groupService.createGroup(
                        req.queryParams("name")
                );
            }, json());

            get("/:id", (req, res) ->
            {
                int id = Integer.parseInt(req.params(":id"));
                Group group = groupService.getGroup(id);
                if (group != null)
                {
                    return group;
                }
                res.status(400);
                return new ResponseError("No group with id '%s' found", String.valueOf(id));
            }, json());

            put("/:id", (req, res) -> groupService.updateGroup(
                    Integer.parseInt(req.params(":id")),
                    req.queryParams("name")
            ), json());

            delete("/:id", (req, res) ->
            {
                int id = Integer.parseInt(req.params(":id"));
                Group group = groupService.getGroup(id);
                if (group != null)
                {
                    groupService.deleteGroup(id);
                    return "";
                }
                else
                {
                    res.status(400);
                    return new ResponseError("No group with id '%s' found", String.valueOf(id));
                }
            }, json());
        });
    }
}

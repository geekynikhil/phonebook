package com.shree.phonebook;

import com.shree.phonebook.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupService
{
    public List<Group> getAllGroups()
    {
        try
        {
            String selectGroups = "SELECT * FROM GROUPS;";
            Statement statement = DB.sharedInstance.connection.createStatement();
            return populateFromResults(statement.executeQuery(selectGroups));
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
            return new ArrayList<Group>();
        }
    }

    public Group getGroup(int id)
    {
        try
        {
            Connection connection = DB.sharedInstance.connection;
            String query = "SELECT * FROM GROUPS WHERE ID=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next())
            {
                Group group = new Group(rs.getString("name"));
                group.setId(id);
                return group;
            }
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Group> getGroups(int[] ids)
    {

        try
        {
            String selectGroups = "SELECT * FROM GROUPS WHERE ID IN "
                    + Arrays.toString(ids).replace("[", "(").replace("]", ")")
                    + ";";
            Connection connection = DB.sharedInstance.connection;
            PreparedStatement statement = connection.prepareStatement(selectGroups);
            return populateFromResults(statement.executeQuery());
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
            return new ArrayList<Group>();
        }
    }

    public List<Group> getGroupsForUser(int userID)
    {
        try
        {
            String selectGroups = "SELECT ID, NAME FROM GROUPS JOIN CONTACTS_GROUPS CG ON CG.GROUP_ID = GROUPS.ID AND CG.CONTACT_ID=?;";
            PreparedStatement st1 = DB.sharedInstance.connection.prepareStatement(selectGroups);
            st1.setInt(1, userID);
            return populateFromResults(st1.executeQuery());
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
            return new ArrayList<Group>();
        }
    }

    private List<Group> populateFromResults(ResultSet resultSet) throws Exception
    {
        List<Group> groups = new ArrayList<>();
        while (resultSet.next())
        {
            Group group = new Group(resultSet.getString("name"));
            group.setId(resultSet.getInt("id"));
            groups.add(group);
        }
        return groups;
    }

    public Group createGroup(String name)
    {

        failIfInvalid(name);
        Group group = new Group(name);
        try
        {
            group.save();
        } catch (SQLException ex)
        {
            System.out.println(ex.getMessage());
        }

        return group;
    }

    public Group updateGroup(int id, String name)
    {
        Group group = getGroup(id);
        if (group == null)
        {
            throw new IllegalArgumentException("No group with id '" + id + "' found");
        }
        failIfInvalid(name);
        group.setName(name);
        try
        {
            group.save();
        } catch (Exception ex)
        {
            throw new IllegalArgumentException("Could not update the group.");
        }

        return group;
    }

    public void deleteGroup(int id)
    {
        try
        {
            Connection connection = DB.sharedInstance.connection;
            String query = "DELETE FROM GROUPS WHERE ID=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception e)
        {
            throw new IllegalArgumentException("No group with id '" + id + "'found");
        }
    }

    private void failIfInvalid(String name)
    {
        if (name == null || name.isEmpty())
        {
            throw new IllegalArgumentException("Sorry, Group Name cannot be empty");
        }
    }
}
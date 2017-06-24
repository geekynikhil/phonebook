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

public class ContactService
{
    private GroupService groupService;

    public ContactService(GroupService groupService)
    {
        this.groupService = groupService;
    }

    public List<Contact> getAllUsers()
    {
        ArrayList<Contact> contacts = new ArrayList<>();
        try
        {
            Connection connection = DB.sharedInstance.connection;
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM CONTACTS;";
            ResultSet rs = statement.executeQuery(query);

            while (rs.next())
            {
                Contact contact = new Contact(rs.getString("name"), rs.getString("email"), rs.getString("phone"));
                contact.setId(rs.getInt("id"));
                contacts.add(contact);
            }

            for (Contact contact : contacts)
            {
                contact.setGroups(groupService.getGroupsForUser(contact.getId()));
            }

        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return contacts;
    }

    public Contact getUser(int id)
    {
        try
        {
            Connection connection = DB.sharedInstance.connection;
            String query = "SELECT * FROM CONTACTS WHERE ID=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next())
            {
                Contact contact = new Contact(rs.getString("name"), rs.getString("email"), rs.getString("phone"));
                contact.setId(rs.getInt("id"));
                contact.setGroups(groupService.getGroupsForUser(contact.getId()));
                return contact;
            }
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Contact createUser(String name, String email, String phone, int[] groupId)
    {
        failIfInvalid(name, email, phone, groupId);
        List<Group> groups = new ArrayList<Group>();
        groups = groupService.getGroups(groupId);

        if (groups == null)
        {
            throw new IllegalArgumentException("No group found");
        }
        Contact contact = new Contact(name, email, phone);
        contact.setGroups(groups);
        try
        {
            contact.save();
        } catch (SQLException s)
        {
            System.out.println(s);
        }

        return contact;
    }

    public Contact updateUser(int id, String name, String email, String phone, int[] groupId)
    {
        Contact contact = getUser(id);
        if (contact == null)
        {
            throw new IllegalArgumentException("No user with id '" + id + "' found");
        }
        List<Group> groups = groupService.getGroups(groupId);
        if (groups == null)
        {
            throw new IllegalArgumentException("No groups with id '" + Arrays.toString(groupId) + "' found");
        }
        failIfInvalid(name, email, phone, groupId);
        contact.setName(name);
        contact.setEmail(email);
        contact.setPhone(phone);
        contact.setGroups(groups);
        try
        {
            contact.save();
        } catch (Exception ex)
        {
            throw new IllegalArgumentException("Could not update the user.");
        }
        return contact;
    }

    public void deleteUser(int id)
    {
        try
        {
            Connection connection = DB.sharedInstance.connection;
            String query = "DELETE FROM CONTACTS WHERE ID=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception e)
        {
            throw new IllegalArgumentException("No user with id '" + id + "'found");
        }
    }

    private void failIfInvalid(String name, String email, String phone, int[] groupId)
    {
        System.out.println("Name = " + name);
        System.out.println("email = " + email);
        System.out.println("phone = " + phone);
        System.out.println("groupId = " + Arrays.toString(groupId));
        if (name == null || name.isEmpty())
        {
            throw new IllegalArgumentException("Sorry, Name cannot be empty");
        }
        if (email == null || email.isEmpty())
        {
            throw new IllegalArgumentException("Sorry, Email cannot be empty");
        }

        if (phone == null || phone.isEmpty())
        {
            throw new IllegalArgumentException("Sorry, Phone cannot be empty");
        }

        if (groupId == null || groupId.length == 0)
        {
            throw new IllegalArgumentException("Sorry, At least one group should be selected.");
        }
    }
}

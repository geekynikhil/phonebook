package com.shree.phonebook;

import com.shree.phonebook.db.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Contact
{

    private int id;
    private String name;
    private String email;
    private String phone;
    private List<Group> groups = new ArrayList<>();

    public Contact(String name, String email, String phone)
    {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public List<Group> getGroups()
    {
        return groups;
    }

    public void setGroups(List<Group> groups)
    {
        this.groups = groups;
    }

    public void save() throws SQLException
    {

        try
        {
            PreparedStatement updateStatement;
            boolean isUpdate = id > 0;
            if (isUpdate)
            {
                String insertContact = "UPDATE CONTACTS SET NAME=?, EMAIL=?, PHONE=? WHERE ID=?;";
                updateStatement = DB.sharedInstance.connection.prepareStatement(insertContact);
                updateStatement.setInt(4, id);
            }
            else
            {
                String insertContact = "INSERT INTO CONTACTS(NAME, EMAIL, PHONE) VALUES(?,?,?)" + ";";
                updateStatement = DB.sharedInstance.connection.prepareStatement(insertContact, Statement.RETURN_GENERATED_KEYS);
            }

            updateStatement.setString(1, name);
            updateStatement.setString(2, email);
            updateStatement.setString(3, phone);
            updateStatement.execute();

            if (isUpdate)
            {
                String deleteOldGroups = "DELETE FROM CONTACTS_GROUPS WHERE CONTACT_ID = ?;";
                PreparedStatement deleteStatement = DB.sharedInstance.connection.prepareStatement(deleteOldGroups);
                deleteStatement.setInt(1, id);
                deleteStatement.execute();
            }
            else
            {
                ResultSet rs = updateStatement.getGeneratedKeys();
                if (rs.next())
                {
                    id = rs.getInt(1);
                    System.out.println("set id of inserted recoded " + id);
                }
                updateStatement.close();
            }

            String insertContactGroup = "INSERT INTO CONTACTS_GROUPS VALUES(?,?);";
            PreparedStatement insertGroupStatement = DB.sharedInstance.connection.prepareStatement(insertContactGroup);
            for (Group group : groups)
            {
                insertGroupStatement.setInt(1, id);
                insertGroupStatement.setInt(2, group.getId());
                insertGroupStatement.addBatch();
            }
            insertGroupStatement.executeBatch();
        } catch (Exception ex)
        {
            throw new IllegalArgumentException("Sorry, There was a problem saving this user.");
        }
    }
}

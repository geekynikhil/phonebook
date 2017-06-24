package com.shree.phonebook;

import com.shree.phonebook.db.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Group
{

    private int id;
    private String name;

    public Group(String name)
    {

        this.name = name;
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

    // Example of saving user
    public void save() throws SQLException
    {

        try
        {
            PreparedStatement updateStatement;
            boolean isUpdate = id > 0;
            if (isUpdate)
            {
                String insertGroup = "UPDATE GROUPS SET NAME=? WHERE ID=?;";
                updateStatement = DB.sharedInstance.connection.prepareStatement(insertGroup);
                updateStatement.setInt(2, id);
            }
            else
            {
                String insertGroup = "INSERT INTO GROUPS(NAME) VALUES(?)" + ";";
                updateStatement = DB.sharedInstance.connection.prepareStatement(insertGroup);
            }

            updateStatement.setString(1, name);
            updateStatement.execute();

            ResultSet rs = updateStatement.getGeneratedKeys();
            if (rs.next())
            {
                id = rs.getInt(1);
                System.out.println("set id of inserted recoded " + id);
            }
            updateStatement.close();
        } catch (SQLException ex)
        {
            System.out.println(ex.getMessage());
            throw new IllegalStateException("Sorry, there was a problem saving group information.");
        }
    }

}
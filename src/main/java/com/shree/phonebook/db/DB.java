package com.shree.phonebook.db;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;

public class DB
{
    public Connection connection;
    public static DB sharedInstance = new DB();

    private DB()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            connection = DriverManager.getConnection(
                    "jdbc:sqlite:db/database.db",
                    config.toProperties()
            );
        } catch (Exception ex)
        {
        }
    }
}

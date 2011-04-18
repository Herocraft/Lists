package com.herocraftonline.dthielke.lists.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class SQLHandler {
    
    private static final String SQL_CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS ?";

    protected String database;
    protected String driver;
    protected String dbURL;
    protected String username;
    protected String password;
    protected Connection db;

    public SQLHandler(String database, String driver, String dbURL, String username, String password) {
        this.driver = driver;
        this.dbURL = dbURL;
        this.username = username;
        this.password = password;
        connect();
    }

    public void connect() {
        try {
            if (db == null || db.isClosed()) {
                Class.forName(driver);
                Properties connectionProperties = new Properties();
                connectionProperties.put("user", username);
                connectionProperties.put("password", password);
                connectionProperties.put("autoReconnect", "true");
                db = DriverManager.getConnection(dbURL, connectionProperties);
                
                PreparedStatement ps = db.prepareStatement(SQL_CREATE_DATABASE);
                ps.setString(1, database);
                ps.executeUpdate();
            }
        } catch (SQLException e) {

        } catch (ClassNotFoundException e) {

        }
    }

    public void disconnect() {
        try {
            if (db != null && !db.isClosed()) {
                db.close();
            }
        } catch (SQLException e) {

        }
    }
}

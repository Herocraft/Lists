package com.herocraftonline.dthielke.lists.io;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.herocraftonline.dthielke.lists.PrivilegedList;
import com.herocraftonline.dthielke.lists.PrivilegedList.PrivilegeLevel;

public class ListsSQLHandler extends SQLHandler {

    private static final String MYSQL_CREATE_LISTS_TABLE = "CREATE TABLE IF NOT EXISTS privileged_lists (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(50) UNIQUE NOT NULL, restricted TINYINT(1) NOT NULL, PRIMARY KEY (id))";
    private static final String MYSQL_CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS privileged_users (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(16) NOT NULL, level TINYINT(1) NOT NULL, list_id INT NOT NULL, PRIMARY KEY (id), FOREIGN KEY (list_id) REFERENCES privileged_lists(id))";
    private static final String SQLITE_CREATE_LISTS_TABLE = "CREATE TABLE IF NOT EXISTS privileged_lists (id INTEGER PRIMARY KEY, name VARCHAR(50) UNIQUE NOT NULL, restricted TINYINT(1) NOT NULL)";
    private static final String SQLITE_CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS privileged_users (id INTEGER PRIMARY KEY, name VARCHAR(16) NOT NULL, level TINYINT(1) NOT NULL, list_id INT NOT NULL, FOREIGN KEY (list_id) REFERENCES privileged_lists(id))";
    private static final String SQL_SELECT_LIST = "SELECT id, restricted FROM privileged_lists WHERE name = ?";
    private static final String SQL_SELECT_LISTS = "SELECT * FROM privileged_lists";
    private static final String SQL_SELECT_USER = "SELECT id, level FROM privileged_users WHERE name = ? AND list_id = ?";
    private static final String SQL_SELECT_USERS = "SELECT id, name, level FROM privileged_users WHERE list_id = ?";
    private static final String SQL_INSERT_LIST = "INSERT INTO privileged_lists (name, restricted) VALUES (?, ?)";
    private static final String SQL_INSERT_USER = "INSERT INTO privileged_users (name, level, list_id) VALUES (?, ?, ?)";
    private static final String SQL_DELETE_LIST = "DELETE FROM privileged_lists WHERE id = ?";
    private static final String SQL_DELETE_USER = "DELETE FROM privileged_users WHERE id = ?";
    private static final String SQL_DELETE_USERS = "DELETE FROM privileged_users WHERE list_id = ?";
    private static final String SQL_UPDATE_LIST = "UPDATE privileged_lists SET restricted = ? WHERE id = ?";
    private static final String SQL_UPDATE_USER = "UPDATE privileged_users SET level = ? WHERE name = ? AND list_id = ?";

    private PreparedStatement stmtSelectList;
    private PreparedStatement stmtSelectLists;
    private PreparedStatement stmtSelectUser;
    private PreparedStatement stmtSelectUsers;
    private PreparedStatement stmtInsertList;
    private PreparedStatement stmtInsertUser;
    private PreparedStatement stmtDeleteList;
    private PreparedStatement stmtDeleteUser;
    private PreparedStatement stmtDeleteUsers;
    private PreparedStatement stmtUpdateList;
    private PreparedStatement stmtUpdateUser;
    
    public ListsSQLHandler(String database, String driver, String dbURL, String username, String password) {
        super(database, driver, dbURL, username, password);
    }
    
    protected void prepareStatements() {
        try {
            stmtSelectList = db.prepareStatement(SQL_SELECT_LIST);
            stmtSelectLists = db.prepareStatement(SQL_SELECT_LISTS);
            stmtSelectUser = db.prepareStatement(SQL_SELECT_USER);
            stmtSelectUsers = db.prepareStatement(SQL_SELECT_USERS);
            stmtInsertList = db.prepareStatement(SQL_INSERT_LIST, Statement.RETURN_GENERATED_KEYS);
            stmtInsertUser = db.prepareStatement(SQL_INSERT_USER);
            stmtDeleteList = db.prepareStatement(SQL_DELETE_LIST);
            stmtDeleteUser = db.prepareStatement(SQL_DELETE_USER);
            stmtDeleteUsers = db.prepareStatement(SQL_DELETE_USERS);
            stmtUpdateList = db.prepareStatement(SQL_UPDATE_LIST);
            stmtUpdateUser = db.prepareStatement(SQL_UPDATE_USER);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setupDatabase() {
        try {
            connect();
            Statement st = db.createStatement();
            if (driver.contains("sqlite")) {
                st.executeUpdate(SQLITE_CREATE_LISTS_TABLE);
                st.executeUpdate(SQLITE_CREATE_USERS_TABLE);
            } else {
                st.executeUpdate(MYSQL_CREATE_LISTS_TABLE);
                st.executeUpdate(MYSQL_CREATE_USERS_TABLE);
            }
            st.close();
            
            prepareStatements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, PrivilegedList> loadLists() {
        HashMap<String, PrivilegedList> map = new HashMap<String, PrivilegedList>();

        try {
            connect();
            ResultSet listsResult = stmtSelectLists.executeQuery();
            while (listsResult.next()) {
                // create the list
                int id = listsResult.getInt("id");
                String name = listsResult.getString("name");
                boolean restricted = listsResult.getByte("restricted") == 1 ? true : false;
                PrivilegedList list = new PrivilegedList(name, restricted);

                // add users to the list
                stmtSelectUsers.setInt(1, id);
                ResultSet usersResult = stmtSelectUsers.executeQuery();
                while (usersResult.next()) {
                    String user = usersResult.getString("name");
                    PrivilegeLevel level = PrivilegeLevel.values()[usersResult.getByte("level")];
                    list.put(user.toLowerCase(), level);
                }

                // add the list to the map
                map.put(name.toLowerCase(), list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public void saveList(PrivilegedList list) {
        try {
            connect();
            stmtSelectList.setString(1, list.getName());
            ResultSet listResult = stmtSelectList.executeQuery();

            // does the list exist?
            if (!listResult.next()) {
                // if not, add the list
                addList(list);
            } else {
                // if so, update the list
                updateList(list, listResult);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteList(String name) {
        try {
            connect();

            stmtSelectList.setString(1, name);
            ResultSet listResult = stmtSelectList.executeQuery();
            // does the list exist?
            if (listResult.next()) {
                // if so, delete it
                int listId = listResult.getInt("id");
                stmtDeleteList.setInt(1, listId);
                stmtDeleteList.executeUpdate();

                // delete users associated with the list
                stmtDeleteUsers.setInt(1, listId);
                stmtDeleteUsers.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addList(PrivilegedList list) {
        try {
            stmtInsertList.setString(1, list.getName());
            stmtInsertList.setByte(2, (byte) (list.isRestricted() ? 1 : 0));
            stmtInsertList.executeUpdate();

            ResultSet keyResult = stmtInsertList.getGeneratedKeys();
            keyResult.next();
            int listId = keyResult.getInt(1);

            // add the users
            Map<String, PrivilegeLevel> users = list.getUsers();
            for (String user : users.keySet()) {
                stmtInsertUser.setString(1, user);
                stmtInsertUser.setByte(2, (byte) users.get(user).ordinal());
                stmtInsertUser.setInt(3, listId);
                stmtInsertUser.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateList(PrivilegedList list, ResultSet listResult) {
        try {
            int listId = listResult.getInt("id");

            // update the restricted property
            boolean restricted = listResult.getByte("restricted") == 1 ? true : false;
            if (restricted != list.isRestricted()) {
                stmtUpdateList.setByte(1,  (byte) (!restricted ? 1 : 0));
                stmtUpdateList.setInt(2, listId);
                stmtUpdateList.executeUpdate();
            }

            // update the users
            Map<String, PrivilegeLevel> users = list.getUsers();
            for (String user : users.keySet()) {
                stmtSelectUser.setString(1, user);
                stmtSelectUser.setInt(2, listId);
                ResultSet userResult = stmtSelectUser.executeQuery();
                // does the user exist?
                if (!userResult.next()) {
                    // if not, add the user
                    stmtInsertUser.setString(1, user);
                    stmtInsertUser.setByte(2, (byte) users.get(user).ordinal());
                    stmtInsertUser.setInt(3, listId);
                    stmtInsertUser.executeUpdate();
                } else {
                    // if so, update the user
                    PrivilegeLevel level = PrivilegeLevel.values()[userResult.getByte("level")];
                    if (level != users.get(user)) {
                        stmtUpdateUser.setByte(1, (byte) users.get(user).ordinal());
                        stmtUpdateUser.setString(2, user);
                        stmtUpdateUser.setInt(3, listId);
                        stmtUpdateUser.executeUpdate();
                    }
                }
            }

            // check for any deleted users
            stmtSelectUsers.setInt(1, listId);
            ResultSet usersResult = stmtSelectUsers.executeQuery();
            while (usersResult.next()) {
                // is the user in our list?
                if (!users.containsKey(usersResult.getString("name"))) {
                    // if not, delete it
                    stmtDeleteUser.setInt(1, usersResult.getInt("id"));
                    stmtDeleteUser.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

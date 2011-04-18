package com.herocraftonline.dthielke.lists.io;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.herocraftonline.dthielke.lists.PrivilegedList;
import com.herocraftonline.dthielke.lists.PrivilegedList.Level;

public class ListsSQLHandler extends SQLHandler {

    private static final String MYSQL_CREATE_LISTS_TABLE = "CREATE TABLE IF NOT EXISTS privileged_lists (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(50) UNIQUE NOT NULL, restricted TINYINT(1) NOT NULL, PRIMARY KEY (id))";
    private static final String MYSQL_CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS privileged_users (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(16) NOT NULL, level TINYINT(1) NOT NULL, list_id INT NOT NULL, PRIMARY KEY (id), FOREIGN KEY (list_id) REFERENCES privileged_lists(id))";
    private static final String SQLITE_CREATE_LISTS_TABLE = "CREATE TABLE IF NOT EXISTS privileged_lists (id INTEGER PRIMARY KEY, name VARCHAR(50) UNIQUE NOT NULL, restricted TINYINT(1) NOT NULL)";
    private static final String SQLITE_CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS privileged_users (id INTEGER PRIMARY KEY, name VARCHAR(16) NOT NULL, level TINYINT(1) NOT NULL, list_id INT NOT NULL, FOREIGN KEY (list_id) REFERENCES privileged_lists(id))";
    private static final String SQL_SELECT_LIST = "SELECT id, restricted FROM privileged_lists WHERE name = ?";
    private static final String SQL_SELECT_LISTS = "SELECT * FROM privileged_lists";
    private static final String SQL_SELECT_USERS = "SELECT id, name, level FROM privileged_users WHERE list_id = ?";
    private static final String SQL_SELECT_USER = "SELECT id, level FROM privileged_users WHERE name = ? AND list_id = ?";
    private static final String SQL_INSERT_LIST = "INSERT INTO privileged_lists (name, restricted) VALUES (?, ?)";
    private static final String SQL_INSERT_USER = "INSERT INTO privileged_users (name, level, list_id) VALUES (?, ?, ?)";
    private static final String SQL_DELETE_LIST = "DELETE FROM privileged_lists WHERE id = ?";
    private static final String SQL_DELETE_USERS = "DELETE FROM privileged_users WHERE list_id = ?";
    private static final String SQL_DELETE_USER = "DELETE FROM privileged_users WHERE id = ?";
    private static final String SQL_UPDATE_LIST = "UPDATE privilege_lists SET restricted = ? WHERE id = ?";
    private static final String SQL_UPDATE_USER = "UPDATE privileged_users SET level = ? WHERE name = ? AND list_id = ?";

    public ListsSQLHandler(String database, String driver, String dbURL, String username, String password) {
        super(database, driver, dbURL, username, password);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, PrivilegedList> loadLists() {
        HashMap<String, PrivilegedList> map = new HashMap<String, PrivilegedList>();

        try {
            connect();
            PreparedStatement usersQuery = db.prepareStatement(SQL_SELECT_USERS);
            Statement listsQuery = db.createStatement();
            ResultSet listsResult = listsQuery.executeQuery(SQL_SELECT_LISTS);
            while (listsResult.next()) {
                // create the list
                int id = listsResult.getInt("id");
                String name = listsResult.getString("name");
                boolean restricted = listsResult.getByte("restricted") == 1 ? true : false;
                PrivilegedList list = new PrivilegedList(name, restricted);

                // add users to the list
                usersQuery.setInt(1, id);
                ResultSet usersResult = usersQuery.executeQuery();
                while (usersResult.next()) {
                    String user = usersResult.getString("name");
                    Level level = Level.values()[usersResult.getByte("level")];
                    list.put(user, level);
                }

                // add the list to the map
                map.put(name, list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public void saveList(PrivilegedList list) {
        try {
            connect();
            PreparedStatement listQuery = db.prepareStatement(SQL_SELECT_LIST);
            listQuery.setString(1, list.getName());
            ResultSet listResult = listQuery.executeQuery();

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
            PreparedStatement listQuery = db.prepareStatement(SQL_SELECT_LIST);

            listQuery.setString(1, name);
            ResultSet listResult = listQuery.executeQuery();
            // does the list exist?
            if (listResult.next()) {
                // if so, delete it
                int listId = listResult.getInt("id");
                PreparedStatement listDelete = db.prepareStatement(SQL_DELETE_LIST);
                listDelete.setInt(1, listId);
                listDelete.executeUpdate();

                // delete users associated with the list
                PreparedStatement usersDelete = db.prepareStatement(SQL_DELETE_USERS);
                usersDelete.setInt(1, listId);
                usersDelete.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addList(PrivilegedList list) {
        try {
            PreparedStatement listInsert = db.prepareStatement(SQL_INSERT_LIST, Statement.RETURN_GENERATED_KEYS);
            listInsert.setString(1, list.getName());
            listInsert.setByte(2, (byte) (list.isRestricted() ? 1 : 0));
            listInsert.executeUpdate();

            ResultSet keyResult = listInsert.getGeneratedKeys();
            keyResult.next();
            int listId = keyResult.getInt(1);

            // add the users
            PreparedStatement userInsert = db.prepareStatement(SQL_INSERT_USER);
            Map<String, Level> users = list.getUsers();
            for (String user : users.keySet()) {
                userInsert.setString(1, user);
                userInsert.setByte(2, (byte) users.get(user).ordinal());
                userInsert.setInt(3, listId);
                userInsert.executeUpdate();
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
                PreparedStatement listUpdate = db.prepareStatement(SQL_UPDATE_LIST);
                listUpdate.setByte(1,  (byte) (!restricted ? 1 : 0));
                listUpdate.setInt(2, listId);
                listUpdate.executeUpdate();
            }

            // update the users
            PreparedStatement userQuery = db.prepareStatement(SQL_SELECT_USER);
            PreparedStatement userInsert = db.prepareStatement(SQL_INSERT_USER);
            PreparedStatement userUpdate = db.prepareStatement(SQL_UPDATE_USER);
            Map<String, Level> users = list.getUsers();
            for (String user : users.keySet()) {
                userQuery.setString(1, user);
                userQuery.setInt(2, listId);
                ResultSet userResult = userQuery.executeQuery();
                // does the user exist?
                if (!userResult.next()) {
                    // if not, add the user
                    userInsert.setString(1, user);
                    userInsert.setByte(2, (byte) users.get(user).ordinal());
                    userInsert.setInt(3, listId);
                    userInsert.executeUpdate();
                } else {
                    // if so, update the user
                    Level level = Level.values()[userResult.getByte("level")];
                    if (level != users.get(user)) {
                        userUpdate.setByte(1, (byte) users.get(user).ordinal());
                        userUpdate.setString(2, user);
                        userUpdate.setInt(3, listId);
                        userUpdate.executeUpdate();
                    }
                }
            }

            // check for any deleted users
            PreparedStatement usersQuery = db.prepareStatement(SQL_SELECT_USERS);
            usersQuery.setInt(1, listId);
            ResultSet usersResult = usersQuery.executeQuery();
            while (usersResult.next()) {
                // is the user in our list?
                if (!users.containsKey(usersResult.getString("name"))) {
                    // if not, delete it
                    PreparedStatement userDelete = db.prepareStatement(SQL_DELETE_USER);
                    userDelete.setInt(1, usersResult.getInt("id"));
                    userDelete.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

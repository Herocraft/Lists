package com.herocraftonline.dthielke.lists;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/***
 * Stores a list of players that can be created by users and passed between plugins.
 * Each member of a list has one of four <code>PrivilegeLevel</code>s. Each level inherits the privileges of the previous level.</br></br>
 * Privilege Levels:</br>
 * <ol>
 *     <li><code>NONE</code> (n) - No privileges. If the list is restricted, such a user cannot see the list.</li>
 *     <li><code>VIEWER</code> (v) - Basic viewing privileges, even if the list is restricted.</li>
 *     <li><code>MODIFIER</code> (m) - The user can add, modify and remove players from the list. Can promote players up to VIEWER.</li>
 *     <li><code>OWNER</code> (o) - The user can delete the list. Can promote players up to MODIFIER.</li>
 * </ol>
 */
public class PrivilegedList {

    /***
     * Enumeration of the four available privilege levels for members of a list.
     */
    public enum PrivilegeLevel {
        NONE("n"),
        VIEWER("v"),
        MODIFIER("m"),
        OWNER("o");

        private static final HashMap<String, PrivilegeLevel> mapping = new HashMap<String, PrivilegeLevel>();
        public final String abbreviation;

        static {
            for (PrivilegeLevel level : EnumSet.allOf(PrivilegeLevel.class)) {
                mapping.put(level.abbreviation, level);
            }
        }

        private PrivilegeLevel(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        public static PrivilegeLevel parse(String name) {
            PrivilegeLevel mappedLevel = mapping.get(name);
            if (mappedLevel == null) {
                for (PrivilegeLevel level : EnumSet.allOf(PrivilegeLevel.class)) {
                    System.out.println(name);
                    System.out.println(level.name());
                    if (name.equalsIgnoreCase(level.name())) {
                        return level;
                    }
                }
                return null;
            } else {
                return mappedLevel;
            }
        }

        public boolean clears(PrivilegeLevel reference) {
            return this.ordinal() >= reference.ordinal();
        }
    }

    private String name;
    private boolean restricted;
    private Map<String, PrivilegeLevel> users = new HashMap<String, PrivilegeLevel>();

    /***
     * Creates a new list with a specified name. By default, a list is unrestricted, so anyone can view its members.
     * 
     * @param name the name of the list
     */
    public PrivilegedList(String name) {
        this.name = name;
        this.restricted = false;
    }

    /***
     * Creates a new list with a specified name and restriction status.
     * 
     * @param name the name of the list
     * @param restricted if true, only members of the list with a privilege <code>Level</code> of <code>VIEWER</code> or above can view the list; otherwise, anyone can view the list.
     */
    public PrivilegedList(String name, boolean restricted) {
        this.name = name;
        this.restricted = restricted;
    }

    public String toString() {
        String map = "[";
        for (Entry<String, PrivilegeLevel> entry : users.entrySet()) {
            map += entry.getKey() + ":" + entry.getValue().abbreviation + ", ";
        }
        if (users.size() != 0) {
            map = map.substring(0, map.length() - 2) + "]";
        } else {
            map = map + "]";
        }
        return "{" + name + ", " + restricted + ", " + map + "}";
    }

    public boolean contains(String name) {
        return users.containsKey(name.toLowerCase());
    }

    public void put(String name, PrivilegeLevel level) {
        users.put(name.toLowerCase(), level);
    }

    public PrivilegeLevel get(String name) {
        if (contains(name)) {
            return users.get(name.toLowerCase());
        } else {
            return null;
        }
    }

    public Map<String, PrivilegeLevel> getUsers() {
        return users;
    }

    public void setUsers(Map<String, PrivilegeLevel> users) {
        this.users = users;
    }

    public void remove(String name) {
        users.remove(name.toLowerCase());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public boolean isRestricted() {
        return restricted;
    }

}

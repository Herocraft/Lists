package com.herocraftonline.dthielke.lists;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PrivilegedList {

    /***
     * Enumeration of the four available privilege levels for members of a list.
     */
    public enum Level {
        NONE("n"),
        VIEWER("v"),
        MODIFIER("m"),
        OWNER("o");

        private static final HashMap<String, Level> mapping = new HashMap<String, Level>();
        public final String abbreviation;

        static {
            for (Level level : EnumSet.allOf(Level.class)) {
                mapping.put(level.abbreviation, level);
            }
        }

        private Level(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        public static Level parse(String name) {
            Level mappedLevel = mapping.get(name);
            if (mappedLevel == null) {
                for (Level level : EnumSet.allOf(Level.class)) {
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

        public boolean clears(Level reference) {
            return this.ordinal() >= reference.ordinal();
        }
    }

    private String name;
    private boolean restricted;
    private Map<String, Level> users = new HashMap<String, Level>();

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
        for (Entry<String, Level> entry : users.entrySet()) {
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

    public void put(String name, Level level) {
        users.put(name.toLowerCase(), level);
    }

    public Level get(String name) {
        if (contains(name)) {
            return users.get(name.toLowerCase());
        } else {
            return null;
        }
    }

    public Map<String, Level> getUsers() {
        return users;
    }

    public void setUsers(Map<String, Level> users) {
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

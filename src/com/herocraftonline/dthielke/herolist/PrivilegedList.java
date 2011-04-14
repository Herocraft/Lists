package com.herocraftonline.dthielke.herolist;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.avaje.ebean.annotation.EnumValue;

public class PrivilegedList {

    public enum Level {
        @EnumValue("n")
        NONE("n"),

        @EnumValue("v")
        VIEWER("v"),

        @EnumValue("m")
        MODIFIER("m"),

        @EnumValue("o")
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
    private Map<String, Level> players = new HashMap<String, Level>();

    public PrivilegedList(String name) {
        this.name = name;
        this.restricted = false;
    }

    public PrivilegedList(String name, boolean restricted) {
        this.name = name;
        this.restricted = restricted;
    }

    public String toString() {
        String map = "[";
        for (Entry<String, Level> entry : players.entrySet()) {
            map += entry.getKey() + ":" + entry.getValue().abbreviation + ", ";
        }
        if (players.size() != 0) {
            map = map.substring(0, map.length() - 2) + "]";
        }
        return "{" + name + ", " + restricted + ", " + map + "}";
    }

    public boolean contains(String name) {
        return players.containsKey(name.toLowerCase());
    }

    public void put(String name, Level level) {
        players.put(name.toLowerCase(), level);
    }

    public Level get(String name) {
        if (contains(name)) {
            return players.get(name.toLowerCase());
        } else {
            return null;
        }
    }

    public Map<String, Level> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, Level> players) {
        this.players = players;
    }

    public Set<String> getPlayerSet() {
        return players.keySet();
    }

    public void remove(String name) {
        players.remove(name.toLowerCase());
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

package com.herocraftonline.dthielke.herolist;

import java.util.EnumSet;
import java.util.HashMap;

import com.avaje.ebean.annotation.EnumValue;

public enum PrivilegeLevel {
    @EnumValue("n")
    NONE("n"),
    
    @EnumValue("v")
    VIEWER("v"),
    
    @EnumValue("m")
    MODIFIER("m"),
    
    @EnumValue("o")
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
package com.herocraftonline.dthielke.herolist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.validation.NotEmpty;

@Entity
public class PrivilegedList {

    protected static HeroList plugin;

    @Id
    private int id;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="list")
    private Set<PrivilegedPlayer> players;

    @NotEmpty
    private String name;

    private boolean restricted = false;

    public PrivilegedList() {}

    public PrivilegedList(String name) {
        this.players = new HashSet<PrivilegedPlayer>();
        this.name = name;
    }
    
    public void save() {
        
    }

    public boolean containsPlayer(String name) {
        name = name.toLowerCase();
        for (PrivilegedPlayer player : players) {
            if (player.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void putPlayer(String name, PrivilegeLevel privilege) {
        name = name.toLowerCase();
        PrivilegedPlayer player = new PrivilegedPlayer(name, privilege);
        // player.setList(this);
        players.remove(player);
        players.add(player);
    }

    public PrivilegeLevel getPrivilegeLevel(String name) {
        name = name.toLowerCase();
        for (PrivilegedPlayer player : players) {
            if (player.getName().equals(name)) {
                return player.getPrivilege();
            }
        }
        return null;
    }

    public boolean removePlayer(String name) {
        PrivilegedPlayer player = new PrivilegedPlayer(name, PrivilegeLevel.NONE);
        // player.setList(this);
        return players.remove(player);
    }

    public String[] getPlayerList() {
        List<String> names = new ArrayList<String>();
        for (PrivilegedPlayer player : players) {
            names.add(player.getName());
        }
        return names.toArray(new String[0]);
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<PrivilegedPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(Set<PrivilegedPlayer> players) {
        this.players = players;
    }

}

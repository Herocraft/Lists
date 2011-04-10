package com.herocraftonline.dthielke.herolist;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

@Entity
public class PrivilegedPlayer {

    @Id
    private int id;

    @NotEmpty
    private String name;

    @NotNull
    private PrivilegeLevel privilege;

    @ManyToOne(cascade=CascadeType.ALL)
    private PrivilegedList list;

    public PrivilegedPlayer() {}

    public PrivilegedPlayer(String name, PrivilegeLevel privilege) {
        this.name = name;
        this.privilege = privilege;
    }

    public boolean equals(Object o) {
        if (o instanceof PrivilegedPlayer) {
            if (name.equalsIgnoreCase(((PrivilegedPlayer) o).getName())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PrivilegeLevel getPrivilege() {
        return privilege;
    }

    public void setPrivilege(PrivilegeLevel privilege) {
        this.privilege = privilege;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setList(PrivilegedList list) {
        this.list = list;
    }

    public PrivilegedList getList() {
        return list;
    }

}

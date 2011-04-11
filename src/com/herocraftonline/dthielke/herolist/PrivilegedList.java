package com.herocraftonline.dthielke.herolist;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.herocraftonline.dthielke.herolist.Privilege.Level;

@Entity
@Table(name = "privileged_lists")
public class PrivilegedList {

	@Id
	private int id;
	private String name;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "list")
	@MapKey(name = "playerName")
	private Map<String, Privilege> players = new HashMap<String, Privilege>();
	private boolean restricted = false;

	public PrivilegedList() {
	}

	public PrivilegedList(String name) {
		this.name = name;
	}

	public boolean contains(String name) {
		return players.containsKey(name.toLowerCase());
	}

	public void put(String name, Level level) {
		players.put(name.toLowerCase(), new Privilege(level, this, name.toLowerCase()));
	}

	public Level get(String name) {
		if (contains(name)) {
			return players.get(name.toLowerCase()).getLevel();
		} else {
			return null;
		}
	}

	public Map<String, Privilege> getPlayers() {
		return players;
	}

	public void setPlayers(Map<String, Privilege> players) {
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

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}

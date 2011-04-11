package com.herocraftonline.dthielke.herolist;

import java.util.EnumSet;
import java.util.HashMap;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.annotation.EnumValue;

@Entity
@Table(name = "privileges")
public class Privilege {

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

	@Id
	private int id;
	private Level level;
	private String playerName;
	@ManyToOne(cascade = CascadeType.ALL, targetEntity = PrivilegedList.class)
	private PrivilegedList list;

	public Privilege() {
	}

	public Privilege(Level level, PrivilegedList list, String playerName) {
		this.level = level;
		this.list = list;
		this.playerName = playerName;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Level getLevel() {
		return level;
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

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return playerName;
	}

}

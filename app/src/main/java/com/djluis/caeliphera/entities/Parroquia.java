package com.djluis.caeliphera.entities;

import androidx.annotation.NonNull;

public class Parroquia {
	private int    id;
	private String name;

	public int getId () {
		return id;
	}

	public void setId (int id) {
		this.id = id;
	}

	public String getName () {
		return name;
	}

	public void setName (String name) {
		this.name = name;
	}

	@NonNull
	public String toString () {
		return id + "-" + name;
	}
}
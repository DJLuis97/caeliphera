package com.djluis.caeliphera.entities;

public class PersonUser extends Person {
	protected String name, email;
	protected int person_user_id;

	public String getName () {
		return name;
	}

	public void setName (String name) {
		this.name = name;
	}

	public String getEmail () {
		return email;
	}

	public void setEmail (String email) {
		this.email = email;
	}

	public int getPersonUserId () {
		return person_user_id;
	}

	public void setPersonUserId (int person_user_id) {
		this.person_user_id = person_user_id;
	}

	public String toString () {
		return name;
	}
}

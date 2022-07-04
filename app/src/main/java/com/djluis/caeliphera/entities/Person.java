package com.djluis.caeliphera.entities;

import java.util.Date;
public class Person {
	protected Date   birth;
	protected String ci, first_name, last_name;
	protected int person_id;

	public Date getBirth () {
		return birth;
	}

	public void setBirth (Date birth) {
		this.birth = birth;
	}

	public String getCi () {
		return ci;
	}

	public void setCi (String ci) {
		this.ci = ci;
	}

	public String getFirst_name () {
		return first_name;
	}

	public void setFirst_name (String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name () {
		return last_name;
	}

	public void setLastName (String last_name) {
		this.last_name = last_name;
	}

	public int getPersonId () {
		return person_id;
	}

	public void setPersonId (int person_id) {
		this.person_id = person_id;
	}

	public void setFullName (String full_name) {
		String[] parts = full_name.split(" ");
		first_name = parts[0];
		last_name = parts[1];
	}

	public String getFullName () {
		return first_name + " " + last_name;
	}

	public String toString () {
		return getFullName();
	}
}

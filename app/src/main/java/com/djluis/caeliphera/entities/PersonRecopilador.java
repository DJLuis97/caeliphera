package com.djluis.caeliphera.entities;

public class PersonRecopilador extends Person {
	protected int    id_recopilador;
	protected String latitude, longitude;
	protected Parroquia belong_to_parroquia;
	protected Person    belongs_to_encargado;

	public int getIdRecopilador () {
		return id_recopilador;
	}

	public void setIdRecopilador (int id_encargado) {
		this.id_recopilador = id_encargado;
	}

	public String getLatitude () {
		return latitude;
	}

	public void setLatitude (String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude () {
		return longitude;
	}

	public void setLongitude (String longitude) {
		this.longitude = longitude;
	}

	public Parroquia getParroquia () {
		return belong_to_parroquia;
	}

	public void setParroquia (Parroquia parroquia) {
		this.belong_to_parroquia = parroquia;
	}

	public Person getEncargado () {
		return belongs_to_encargado;
	}

	public void setEncargado (Person encargado) {
		this.belongs_to_encargado = encargado;
	}
}
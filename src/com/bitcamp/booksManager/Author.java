package com.bitcamp.booksManager;

import java.sql.SQLException;

public class Author {
	private int id;
	private String name;
	private String surname;
	
	/**
	 * @param name
	 * @param surname
	 * @throws SQLException 
	 */
	public Author(int id, String name, String surname) {
		this.id = id;
		this.name = name;
		this.surname = surname;
	}
	
	/**
	 * @param name
	 * @param surname
	 * @throws SQLException 
	 */
	public Author(String name, String surname) {
		this(-1, name, surname);
	}
	
	public Author(Author other) {
		this(other.id, other.name, other.surname);
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getFullName() {
		return name + " " + surname;
	}
	
	@Override
	public String toString() {
		return "id = " + id + ", nome = " + name + ", cognome = " + surname; 
	}
	
	
}

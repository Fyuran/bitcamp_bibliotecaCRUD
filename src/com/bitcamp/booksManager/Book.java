package com.bitcamp.booksManager;

public class Book {
	private String title;
	private int year;
	private Author author;
	private int id;
	
	/**
	 * @param title
	 * @param year
	 * @param author
	 */
	public Book(int id, String title, int year, Author author) {
		this.id = id;
		this.title = title;
		this.year = year;
		this.author = author;
	}

	public Book(String title, int year, Author author) {
		this(-1, title, year, author);
	}
	
	public Book(Book other) {
		this(other.id, other.title, other.year, other.author);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "id = " + id + ", titolo = " + title + ", anno pubblicazione = " + year + ", autore:[" + author + "]";
	}
	
	
}

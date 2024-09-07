package com.bitcamp.booksManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseConnection {
	private final String url;
	private Connection connection = null;
	
	/**
	 * @param url
	 * @param username
	 * @param password
	 * @throws SQLException 
	 */
	public DatabaseConnection(String url, String username, String password) throws SQLException {
		this.url = url;
		
		PreparedStatement stat = null;
		connection = DriverManager.getConnection(url, username, password);
		
		String schemaQuery = "CREATE SCHEMA IF NOT EXISTS books_manager";
		stat = connection.prepareStatement(schemaQuery);
		stat.execute();
		
		//Author(int id, String name, String surname)
		String authorsTQuery = "CREATE TABLE IF NOT EXISTS books_manager.authors("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "name VARCHAR(100) NOT NULL, "
				+ "surname VARCHAR(100) NOT NULL)";
		
		stat = connection.prepareStatement(authorsTQuery);
		stat.execute();
		
		//Book(int id, String title, int year, Author author)
		String booksTQuery = "CREATE TABLE IF NOT EXISTS books_manager.books("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "title VARCHAR(50) NOT NULL, "
				+ "year INT NOT NULL, "
				+ "author_id INT NOT NULL, "
				+ "FOREIGN KEY(author_id) REFERENCES books_manager.authors(id))";
		stat = connection.prepareStatement(booksTQuery);
		stat.execute();
	}
	
	
	public String getUrl() {
		return url;
	}
	
	public boolean isValid(int timeout) {
		try {
			return connection.isValid(timeout);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public void insertBook(Book book) {
		String query = "INSERT INTO books_manager.books(title, year, author_id) VALUES (?, ?, ?)";
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				
			stat.setString(1, book.getTitle());
			stat.setInt(2, book.getYear());
			stat.setInt(3, book.getAuthor().getId());
			
			stat.executeUpdate();
			
			ResultSet generatedKeys = stat.getGeneratedKeys();
			if(generatedKeys.next()) {
				book.setId(generatedKeys.getInt(1));				
			}
            
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stat.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int deleteBook(int id) {
		String query = "DELETE FROM books_manager.books WHERE id = ?";
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement(query);
			
			stat.setInt(1, id);
			
            return stat.executeUpdate();

		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stat.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public int deleteBook(Book book) {
		return deleteBook(book.getId());
	}
	
	public int updateBook(int id, Book book) {
		String query = "UPDATE books_manager.books SET title = ?, year = ?, author_id = ? WHERE id = ?";
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement(query);
		
			stat.setString(1, book.getTitle());
			stat.setInt(2, book.getYear());
			stat.setInt(3, book.getAuthor().getId());
			stat.setInt(4, id);
				
            return stat.executeUpdate();

		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stat.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public int updateBook(Book book) {
		return updateBook(book.getId(), book);
	}
	
	public Book getBook(int id) {
		Book book = null;
		
		String query = "SELECT * FROM books_manager.books WHERE id = ?";
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement(query);
			
			stat.setInt(1, id);
			
			
			ResultSet rs = stat.executeQuery();
			if(rs.next()) {
				Author author = getAuthor(rs.getInt(4));		
				book = new Book(rs.getInt(1), rs.getString(2), rs.getInt(3), author); //(int id, String title, int year, Author author)
			}

		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stat.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return book;
	}
	
	public List<Book> getBooks() {
		List<Book> books = new ArrayList<>();
		PreparedStatement stat = null;
		try {
			String query = "SELECT * FROM books_manager.books";
			stat = connection.prepareStatement(query);
			
			ResultSet rs = stat.executeQuery();
			while(rs.next()) {
				Book book = getBook(rs.getInt(1));
				if(book == null) throw new SQLException("Could not find referenced book id: " + rs.getInt(1));
				books.add(book);
			}			
		}catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stat.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return Collections.unmodifiableList(books);
	}
	
	public int insertAuthor(Author author) {
		String query = "INSERT INTO books_manager.authors(name, surname) VALUES (?, ?)";
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			
			stat.setString(1, author.getName());
			stat.setString(2, author.getSurname());
			
			stat.executeUpdate();
			
			ResultSet generatedKeys = stat.getGeneratedKeys();
			if(generatedKeys.next()) {
				author.setId(generatedKeys.getInt(1));				
			}
            
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stat.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	public Author getAuthor(int id) {
		Author author = null;
		PreparedStatement stat = null;
		
		try {
			String query = "SELECT * FROM books_manager.authors WHERE id = ?";
			stat = connection.prepareStatement(query);	
			stat.setInt(1, id);
			
			ResultSet rs = stat.executeQuery();
			if(rs.next()) {
				author = new Author(rs.getInt(1), rs.getString(2), rs.getString(3)); //(int id, String name, String surname)	
				
			}

		}catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stat.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return author;		
	}
	
	public int deleteAuthor(int id) {
		String query = "DELETE FROM books_manager.authors WHERE id = ?";
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement(query);
			
			stat.setInt(1, id);
			
            return stat.executeUpdate();

		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stat.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public int deleteAuthor(Author author) {
		return deleteAuthor(author.getId());
	}
	
	public int updateAuthor(int id, Author author) {
		String query = "UPDATE books_manager.authors SET name = ?, surname = ? WHERE id = ?";
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement(query);
		
			stat.setString(1, author.getName());
			stat.setString(2, author.getSurname());
			stat.setInt(3, id);
				
            return stat.executeUpdate();

		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stat.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public int updateAuthor(Author author) {
		return updateAuthor(author.getId(), author);
	}
	
	public List<Author> getAuthors() {
		List<Author> authors = new ArrayList<>();
		PreparedStatement stat = null;
		try {
			String query = "SELECT * FROM books_manager.authors";
			stat = connection.prepareStatement(query);
			
			ResultSet rs = stat.executeQuery();
			while(rs.next()) {
				Author book = getAuthor(rs.getInt(1));
				if(book == null) throw new SQLException("Could not find referenced author id: " + rs.getInt(1));
				authors.add(book);
			}			
		}catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stat.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return Collections.unmodifiableList(authors);
	}
	
	public boolean close() {
		try {
			connection.close();
			return connection.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}

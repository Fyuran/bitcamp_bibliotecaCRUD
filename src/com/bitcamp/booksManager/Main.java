package com.bitcamp.booksManager;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


import com.bitcamp.consoleUI.*;

public class Main {	
	private static DatabaseConnection db = null;
	
	private static List<Author> authors;
	private static List<Book> books;
	
	public static void main(String[] args) {
		while(db == null) {
			String url = Input.askLine(Colors.toColor("Inserire URL database (esempio: jdbc:mysql://localhost:3306/)", Colors.YELLOW));
			String user = Input.askLine(Colors.toColor("Inserire USERNAME database", Colors.YELLOW));
			String password = Input.askLine(Colors.toColor("Inserire PASSWORD database", Colors.YELLOW));
			
			try {
				db = new DatabaseConnection(url, user, password);
				if(db.isValid(60)) {
					System.out.println(Colors.toColor("Connessione con il database avvenuta con successo", Colors.GREEN));
				} else {
					System.out.println(Colors.toColor("Connessione con il database fallita o timeout", Colors.RED));
				}
			} catch(SQLException e) {
				System.out.println(Colors.toColor("Connessione con il database fallita o timeout", Colors.RED));
				System.out.println(e);
			}

		}

		ConsoleUI menu = new ConsoleUI("Book store Manager 2024 - Bitcamp rev 0.5");
		menu.addCmd("Gestione Libri", () -> manageItems(true));
		menu.addCmd("Gestione Autori", () -> manageItems(false));
		menu.showCmds();
	}
	
	private static void refreshLists() {
		authors = db.getAuthors();
		books = db.getBooks();
	}
	
	private static void manageItems(boolean isBook) {
		
		String ITEM_NAME;
		if(isBook)
			ITEM_NAME = "Libri";
		else
			ITEM_NAME = "Autori";
		
		ConsoleUI menu = new ConsoleUI("Menu Modifica " + ITEM_NAME);
			
		menu.addCmd("Aggiungi", () -> addItem(isBook));
		menu.addCmd("Rimuovi", () -> removeItem(isBook));
		menu.addCmd("Aggiorna", () -> updateItem(isBook));
		menu.addCmd("Stampa dati", () -> printItem(isBook));
		menu.addCmd("Stampa lista", () -> printItems(isBook));
		menu.addCmd("Ricerca Avanzate", () -> searchItem(isBook));
		menu.showCmds();
		
		
	}
	
	private static void addItem(boolean isBook) {
		refreshLists();
		
		if(isBook) {
			//(String title, int year, Author author)
			if(authors.isEmpty()) {
				System.out.println(Colors.toColor("Nessun autore presente nel database", Colors.RED));
				return;
			}
			String title = Input.askLine(Colors.toColor("Inserire titolo libro", Colors.YELLOW));
			int year = Input.askInt(Colors.toColor("Inserire anno libro", Colors.YELLOW));
			
			boolean isValidAuthor = false;
			while(!isValidAuthor) {
				try {
					printItems(false);
					int choice = Input.askInt(Colors.toColor("Scegliere indice autore", Colors.YELLOW));
					Book book = new Book(title, year, authors.get(choice-1));
					db.insertBook(book);
					isValidAuthor = true;
				}catch(IndexOutOfBoundsException e) {
					System.out.println(Colors.toColor("*Indice non valido*", Colors.RED));
				}				
			}
			
		} else {
			String name = Input.askLine(Colors.toColor("Inserire nome autore", Colors.YELLOW));
			String surname = Input.askLine(Colors.toColor("Inserire cognome autore", Colors.YELLOW));
						
			Author author = new Author(name, surname);
			db.insertAuthor(author);
		}
		
		System.out.println(Colors.toColor("Oggetto inserito nel database", Colors.GREEN));
	}
	
	private static <T> void removeItem(boolean isBook) {
		refreshLists();
		
		if(isBook) {
			if(books.isEmpty()) {
				System.out.println(Colors.toColor("Nessun libro presente nel database", Colors.RED));
				return;
			}
			printItems(true);
			
			boolean isValidIndex = false;
			while(!isValidIndex) {
				try {
					int choice = Input.askInt(Colors.toColor("Scegliere indice libro", Colors.YELLOW));
					db.deleteBook(books.get(choice-1));
					isValidIndex = true;
				}catch(IndexOutOfBoundsException e) {
					System.out.println(Colors.toColor("*Indice non valido*", Colors.RED));
				}				
			}
			
		} else {
			if(authors.isEmpty()) {
				System.out.println(Colors.toColor("Nessun autore presente nel database", Colors.RED));
				return;
			}
			printItems(false);
			
			boolean isValidIndex = false;
			while(!isValidIndex) {
				try {
					int choice = Input.askInt(Colors.toColor("Scegliere indice autore", Colors.YELLOW));
					db.deleteAuthor(authors.get(choice-1));
					isValidIndex = true;
				}catch(IndexOutOfBoundsException e) {
					System.out.println(Colors.toColor("*Indice non valido*", Colors.RED));
				}				
			}
			
		}
		
		System.out.println(Colors.toColor("Oggetto rimosso dal database", Colors.GREEN));
	}
	
	private static void updateItem(boolean isBook) {
		refreshLists();
		
		if(isBook) {		
			Book oldBook = null;
			
			boolean isValidBook = false;
			while(!isValidBook) {
				try {
					printItems(true);
					int choice = Input.askInt(Colors.toColor("Scegliere indice libro da modificare", Colors.YELLOW));
					oldBook = books.get(choice - 1);
					isValidBook = true;
				}catch(IndexOutOfBoundsException e) {
					System.out.println(Colors.toColor("*Indice non valido*", Colors.RED));
				}				
			}
			
			String title = Input.askLine(Colors.toColor("Inserire nuovo titolo libro", Colors.YELLOW));
			int year = Input.askInt(Colors.toColor("Inserire nuovo anno libro", Colors.YELLOW));
			oldBook.setTitle(title);
			oldBook.setYear(year);
			
			boolean isValidAuthor = false;
			while(!isValidAuthor) {
				try {
					printItems(false);
					int choice = Input.askInt(Colors.toColor("Scegliere nuovo indice autore", Colors.YELLOW));
					oldBook.setAuthor(authors.get(choice - 1));
					db.updateBook(oldBook);
					isValidAuthor = true;
				}catch(IndexOutOfBoundsException e) {
					System.out.println(Colors.toColor("*Indice non valido*", Colors.RED));
				}				
			}
		} else {
			Author oldAuthor = null;
			
			boolean isValidAuthor = false;
			while(!isValidAuthor) {
				try {
					printItems(false);
					int choice = Input.askInt(Colors.toColor("Scegliere indice autore da modificare", Colors.YELLOW));
					oldAuthor = authors.get(choice - 1);
					
					String name = Input.askLine(Colors.toColor("Inserire nuovo nome autore", Colors.YELLOW));
					String surname = Input.askLine(Colors.toColor("Inserire nuovo cognome autore", Colors.YELLOW));
					oldAuthor.setName(name);
					oldAuthor.setSurname(surname);
					
					db.updateAuthor(oldAuthor);
					
					isValidAuthor = true;
				}catch(IndexOutOfBoundsException e) {
					System.out.println(Colors.toColor("*Indice non valido*", Colors.RED));
				}				
			}
		}
		
		System.out.println(Colors.toColor("Oggetto aggiornato nel database", Colors.GREEN));
	}
	
	private static void printItem(boolean isBook) {
		refreshLists();
		
		if(isBook) {
			if(books.isEmpty()) {
				System.out.println(Colors.toColor("Nessun libro presente nel database", Colors.RED));
				return;
			}
			int id = Input.askInt(Colors.toColor("Inserire ID del libro", Colors.YELLOW));
			for(Book book : books) {
				if(book.getId() == id) {
					System.out.println(book);
					return;
				}
			}
			
			System.out.println(Colors.toColor("*Libro con ID scelto, non trovato*", Colors.RED));
		} else {
			if(authors.isEmpty()) {
				System.out.println(Colors.toColor("Nessun autore presente nel database", Colors.RED));
				return;
			}
			int id = Input.askInt(Colors.toColor("Inserire ID dell'autore", Colors.YELLOW));
			for(Author author : authors) {
				if(author.getId() == id) {
					System.out.println(author);
					return;
				}
			}
			
			System.out.println(Colors.toColor("*Autore con ID scelto, non trovato*", Colors.RED));
		}
		
	}
	
	private static void printItems(boolean isBook) {
		refreshLists();
		
		if(isBook) {
			if(books.isEmpty()) {
				System.out.println(Colors.toColor("Nessun libro presente nel database", Colors.RED));
				return;
			}
			for(int i = 0; i < books.size(); i++)
				System.out.println("\t" + (i+1) + ". " + books.get(i));
		} else {
			if(authors.isEmpty()) {
				System.out.println(Colors.toColor("Nessun autore presente nel database", Colors.RED));
				return;
			}
			for(int i = 0; i < authors.size(); i++)
				System.out.println("\t" + (i+1) + ". " + authors.get(i));
		}
	}

	private static void searchItem(boolean isBook) {
		refreshLists();
		
		ConsoleUI submenu = new ConsoleUI("Ricerca Avanzata");
		if(isBook) {
			submenu.addCmd("Cerca per titolo", () -> {
						
				String term = Input.askLine(Colors.toColor("Inserire titolo", Colors.YELLOW));
				List<Book> found = searchBooksByTitle(term);
				
				if(!found.isEmpty()) {
					System.out.println(Colors.toColor("Libro/i trovati:", Colors.GREEN));
					found.forEach(b -> System.out.println("\t" + b));
					return;
				} else {
					System.out.println(Colors.toColor("Nessun libro trovato", Colors.RED));
				}
				
			});
			submenu.addCmd("Cerca per autore", () -> {
				String term = Input.askLine(Colors.toColor("Inserire nome e/o cognome autore", Colors.YELLOW));
				List<Book> found = searchBooksByAuthor(term);
				
				if(!found.isEmpty()) {
					System.out.println(Colors.toColor("Libro/i trovati:", Colors.GREEN));
					found.forEach(b -> System.out.println("\t" + b));
					return;
				} else {
					System.out.println(Colors.toColor("Nessun libro trovato", Colors.RED));
				}
			});
			submenu.addCmd("Cerca per intervallo anni", ()->{
				int interval1 = Input.askInt(Colors.toColor("Inserire inizio intervallo", Colors.YELLOW));
				int interval2 = -1;
				while(true) {
					interval2 = Input.askInt(Colors.toColor("Inserire fine intervallo", Colors.YELLOW));
					if(interval2 >= interval1) break;
					else System.out.println(Colors.toColor("Intervallo finale non valido", Colors.RED));
				}
				int[] interval = {interval1, interval2};
				
				List<Book> found = searchBooksByYear(interval);
				
				if(!found.isEmpty()) {
					System.out.println(Colors.toColor("Libro/i trovati:", Colors.GREEN));
					found.forEach(b -> System.out.println("\t" + b));
					return;
				} else {
					System.out.println(Colors.toColor("Nessun libro trovato", Colors.RED));
				}
			});
		} else {
			submenu.addCmd("Cerca per nome e/o cognome", () -> {
				String term = Input.askLine(Colors.toColor("Inserire nome e/o cognome autore", Colors.YELLOW));
				List<Author> found = searchAuthorsByFullname(term);
				
				if(!found.isEmpty()) {
					System.out.println(Colors.toColor("Autore/i trovati:", Colors.GREEN));
					found.forEach(b -> System.out.println("\t" + b));
					return;
				} else {
					System.out.println(Colors.toColor("Nessun autore trovato", Colors.RED));
				}
			});
		}
		
		
		submenu.showCmds();

	}
	
	//splits into elements the looked-up element then splits the searched term too, providing then a list with matches
	private static List<Book> searchBooksByTitle(String term) {
		List<Book> found = books.stream().filter(b -> 
			Arrays.stream(b.getTitle().split(" "))
			.anyMatch(s -> 
				Arrays.stream(term.split(" "))
				.anyMatch(token -> token.equalsIgnoreCase(s)))
		).toList();
			
		return found;
	}

	//splits into elements the looked-up element then splits the searched term too, providing then a list with matches
	private static List<Book> searchBooksByYear(int[] interval) {
		List<Book> found = books.stream().filter(b -> {
			int year = b.getYear();
			return year >= interval[0] && year <= interval[1]; //if year is between interval (inclusive)
		}).toList();
			
		return found;
	}

	
	//splits into elements the looked-up element then splits the searched term too, providing then a list with matches
	private static List<Book> searchBooksByAuthor(String term) {
		List<Book> found = books.stream().filter(b -> 
			Arrays.stream(b.getAuthor().getFullName().split(" "))
			.anyMatch(s -> 
				Arrays.stream(term.split(" "))
				.anyMatch(token -> token.equalsIgnoreCase(s)))
		).toList();
		
		return found;
	}
	
	//splits into elements the looked-up element then splits the searched term too, providing then a list with matches
	private static List<Author> searchAuthorsByFullname(String term) {
		List<Author> found = authors.stream().filter(b -> 
			Arrays.stream(b.getFullName().split(" "))
			.anyMatch(s -> 
				Arrays.stream(term.split(" "))
				.anyMatch(token -> token.equalsIgnoreCase(s)))
		).toList();
		
		return found;
	}
}

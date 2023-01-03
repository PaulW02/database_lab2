package com.example.database_lab2.model;

import org.bson.types.ObjectId;

import java.sql.Date;
import java.util.List;

/**
 * This interface declares methods for querying a Books database.
 * Different implementations of this interface handles the connection and
 * queries to a specific DBMS and database, for example a MySQL or a MongoDB
 * database.
 *
 * NB! The methods in the implementation must catch the SQL/MongoDBExceptions thrown
 * by the underlying driver, wrap in a BooksDbException and then re-throw the latter
 * exception. This way the interface is the same for both implementations, because the
 * exception type in the method signatures is the same. More info in BooksDbException.java.
 * 
 * @author anderslm@kth.se
 */
public interface BooksDbInterface {
    
    /**
     * Connect to the database.
     */
    public void connect();
    
    public void disconnect();
    
    public List<Book> searchBooksByTitle(String title);

    public List<Book> searchBooksByISBN(String searchISBN);

    List<Book> searchBooksByAuthor(String searchAuthor);

    List<Book> searchBooksByGenre(String searchGenre);

    List<Book> searchBooksByStars(String searchStars);

    List<Genre> getGenresByBookId(int bookId);

    boolean removeBook(String isbn);

    Book addBook(String title, String isbn, Date published, String authorName, String genre);

    void addGenreToBook(String isbn, String genre);

    void updateTitleBook(String newTitle, String isbn);

    void addAuthorToBook(String isbn, String authorName);

    int getBookIdByISBN(String isbn);

    Author addAuthor(String authorName);

    Author getAuthorByName(String authorName);

    List<Author> getAuthorsByBookId(int bookId);

    User loginUser(String username, String password);

    boolean registerUser(String name, String username, String password);

    List<Book> getAllBooks();

    List<Author> getAllAuthors();

    void reviewBook(String isbn, String username, double rating, String reviewText);

    void addReviewToBook(ObjectId id, String isbn);

    List<Book> getBooksNotReviewed(String username);

    List<Review> getReviewsByBookId(int bookId);

    User getUserByUsername(String username);

    Book getBookByISBN(String isbn);

    // TODO: Add abstract methods for all inserts, deletes and queries
    // mentioned in the instructions for the assignement.
}

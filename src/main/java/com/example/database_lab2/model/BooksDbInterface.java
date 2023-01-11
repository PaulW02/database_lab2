package com.example.database_lab2.model;

import org.bson.Document;
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
    public void connect() throws BooksDbException;
    
    public void disconnect() throws BooksDbException;
    
    public List<Book> searchBooksByTitle(String title) throws BooksDbException;

    public List<Book> searchBooksByISBN(String searchISBN) throws BooksDbException;

    List<Book> searchBooksByAuthor(String searchAuthor) throws BooksDbException;

    List<Book> searchBooksByGenre(String searchGenre) throws BooksDbException;

    List<Book> searchBooksByStars(String searchStars) throws BooksDbException;

    List<Genre> getGenresByISBN(String isbn) throws BooksDbException;

    void removeBook(String isbn) throws BooksDbException;

    Book addBook(String title, String isbn, Date published, String authorName, String genre) throws BooksDbException;

    Genre addGenre(String genre) throws BooksDbException;

    void addGenreToBook(String isbn, String genre) throws BooksDbException;

    void updateTitleBook(String newTitle, String isbn) throws BooksDbException;

    void addAuthorToBook(String isbn, String authorName) throws BooksDbException;

    Author addAuthor(String authorName) throws BooksDbException;

    List<Author> getAuthorsByISBN(String isbn) throws BooksDbException;

    User loginUser(String username, String password) throws BooksDbException;

    boolean registerUser(String name, String username, String password) throws BooksDbException;

    List<Book> getAllBooks() throws BooksDbException;

    List<Author> getAllAuthors() throws BooksDbException;

    void reviewBook(String isbn, String username, double rating, String reviewText) throws BooksDbException;

    void addReviewToBook(Document document, String isbn) throws BooksDbException;

    List<Book> getBooksNotReviewed(String username) throws BooksDbException;

    List<Review> getReviewsByISBN(String isbn) throws BooksDbException;

    Book getBookByISBN(String isbn) throws BooksDbException;

    // TODO: Add abstract methods for all inserts, deletes and queries
    // mentioned in the instructions for the assignement.
}

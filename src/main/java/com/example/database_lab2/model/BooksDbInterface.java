package com.example.database_lab2.model;

import com.mongodb.client.MongoDatabase;

import java.sql.Date;
import java.sql.SQLException;
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
     *
     * @return database on successful connection.
     */
    public MongoDatabase connect();
    
    public void disconnect() throws BooksDbException, SQLException;
    
    public List<Book> searchBooksByTitle(String title) throws BooksDbException, SQLException, ClassNotFoundException;

    public List<Book> searchBooksByISBN(String searchISBN) throws BooksDbException;

    List<Book> searchBooksByAuthor(String searchAuthor) throws BooksDbException;

    List<Book> searchBooksByGenre(String searchGenre) throws BooksDbException;

    List<Book> searchBooksByStars(String searchStars) throws BooksDbException;

    List<Genre> getGenresByBookId(int bookId)
            throws BooksDbException;

    boolean removeBook(int bookId) throws BooksDbException;

    Book addBook(String title, String isbn, Date published, String authorName, String genre)
            throws BooksDbException;

    void addGenreToBook(String isbn, String genre) throws BooksDbException;

    void updateTitleBook(String newTitle, int bookId) throws BooksDbException;

    void addAuthorToBook(String isbn, String authorName) throws BooksDbException, SQLException;

    int getBookIdByISBN(String isbn)
            throws BooksDbException;

    Author addAuthor(String authorName) throws BooksDbException;

    Author getAuthorByName(String authorName) throws BooksDbException;

    List<Author> getAuthorsByBookId(int bookId) throws BooksDbException;

    User loginUser(String username, String password) throws BooksDbException;

    boolean registerUser(String name, String username, String password) throws BooksDbException;

    List<Book> getAllBooks() throws BooksDbException;

    List<Author> getAllAuthors() throws BooksDbException;

    void reviewBook(int bookId, int userId, double rating, String reviewText) throws BooksDbException;

    List<Book> getBooksNotReviewed(int userId) throws BooksDbException;

    Book getBookById(int bookId) throws BooksDbException;

    List<Review> getReviewsByBookId(int bookId) throws BooksDbException;

    User getUserById(int userId) throws BooksDbException;

    // TODO: Add abstract methods for all inserts, deletes and queries
    // mentioned in the instructions for the assignement.
}

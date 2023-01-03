/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.database_lab2.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

/**
 * A mock implementation of the BooksDBInterface interface to demonstrate how to
 * use it together with the user interface.
 * <p>
 * Your implementation must access a real database.
 *
 * @author anderslm@kth.se
 */
public class BooksDbImpl implements BooksDbInterface {
    private static final String DB_NAME = "booksdb";
    private Connection con;

    public BooksDbImpl() {}

    @Override
    public MongoDatabase connect() {
        ConnectionString connectionString = new ConnectionString("mongodb://PaulW02:Adda2002%21@ac-edsmrsu-shard-00-00.4jdrzx1.mongodb.net:27017,ac-edsmrsu-shard-00-01.4jdrzx1.mongodb.net:27017,ac-edsmrsu-shard-00-02.4jdrzx1.mongodb.net:27017/?ssl=true&replicaSet=atlas-7w5ul6-shard-0&authSource=admin&retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);
        return database;
    }

    @Override
    public void disconnect() throws BooksDbException {
        try {
            this.con.close();
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the connection", e);
        }
    }

    /**
     * Takes a string parameter with the searched title and returns all books containing the given string.
     * @param searchTitle takes a string parameter with the searched title
     * @return ArrayList with all books that have a title containing the given string.
     * */
    @Override
    public List<Book> searchBooksByTitle(String searchTitle)
            throws BooksDbException {
        ResultSet rs = null;
        try {
            List<Book> result = new ArrayList<>();
            searchTitle = searchTitle.toLowerCase();
            String sql = "SELECT * FROM book WHERE title LIKE ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setString(1, "%"+searchTitle+"%");
            rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(new Book(rs.getInt("book_id"), rs.getString("isbn"), rs.getString("title"), rs.getDate("published")));
            }
            return result;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * Takes a string parameter with the searched ISBN and returns all books containing the given string.
     * @param searchISBN takes a string parameter with the searched ISBN
     * @return ArrayList with all books that have a ISBN containing the given string.
     * */
    @Override
    public List<Book> searchBooksByISBN(String searchISBN)
            throws BooksDbException {
        ResultSet rs = null;
        try {
            List<Book> result = new ArrayList<>();
            String sql = "SELECT * FROM book WHERE isbn LIKE ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setString(1, "%"+searchISBN+"%");
            rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(new Book(rs.getInt("book_id"), rs.getString("isbn"), rs.getString("title"), rs.getDate("published")));
            }
            return result;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * Takes a string parameter with the searched author and returns all books containing the given string.
     * @param searchAuthor takes a string parameter with the searched author
     * @return ArrayList with all books that have an author's name containing the given string.
     * */
    @Override
    public List<Book> searchBooksByAuthor(String searchAuthor)
            throws BooksDbException {
        ResultSet rs = null;
        try{
            List<Book> result = new ArrayList<>();
            searchAuthor = searchAuthor.toLowerCase();
            String sql = "SELECT b.book_id, b.isbn, b.title, b.published FROM book b INNER JOIN book_author ba ON b.book_id = ba.book_id INNER JOIN author a ON ba.author_id = a.author_id WHERE a.name LIKE ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setString(1, "%"+searchAuthor+"%");
            rs = stmt.executeQuery();

            while (rs.next()){
                result.add(new Book(rs.getInt("book_id"), rs.getString("isbn"), rs.getString("title"), rs.getDate("published")));
            }
            return result;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * Takes a string parameter with the searched genre and returns all books containing the given string.
     * @param searchGenre takes a string parameter with the searched genre
     * @return ArrayList with all books that have a genre same as the one searched.
     * */
    @Override
    public List<Book> searchBooksByGenre(String searchGenre)
            throws BooksDbException {
        ResultSet rs = null;
        try {
            List<Book> result = new ArrayList<>();
            searchGenre = searchGenre.toLowerCase();
            String sql = "SELECT b.book_id, b.isbn, b.title, b.published FROM book b INNER JOIN book_genre bg ON b.book_id = bg.book_id INNER JOIN genre g ON bg.genre_id = g.genre_id WHERE g.genre_name LIKE ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setString(1, "%"+searchGenre+"%");
            rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(new Book(rs.getInt("book_id"), rs.getString("isbn"), rs.getString("title"), rs.getDate("published")));
            }
            return result;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * Takes a string parameter with the searched stars and returns all books containing the given string.
     * @param searchStars takes a string parameter with the searched stars
     * @return ArrayList with all books that have the same amount of stars as given.
     * */
    @Override
    public List<Book> searchBooksByStars(String searchStars)
            throws BooksDbException {
        ResultSet rs = null;
        try {
            List<Book> result = new ArrayList<>();
            String sql = "SELECT b.book_id, b.isbn, b.title, b.published FROM book b, review r WHERE b.book_id = r.book_id AND r.stars = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, Integer.valueOf(searchStars));
            rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(new Book(rs.getInt("book_id"), rs.getString("isbn"), rs.getString("title"), rs.getDate("published")));
            }
            return result;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * This method is used to take a new given title to a book and replace it with the title of an existing book.
     * @param newTitle takes a string with a given title.
     * @param bookId takes an int with an id to specify a book.
     * */
    @Override
    public void updateTitleBook(String newTitle, int bookId)
            throws BooksDbException{
        try{
            String sql = "UPDATE book SET title = ? WHERE book_id = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setString(1,newTitle);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    /**
     * This method is used to add a new book to the database by applying all the needed attributes for a book to exist.
     * @param title a string parameter with a given title.
     * @param isbn a string parameter with a given isbn.
     * @param published takes a given date as a parameter.
     * @param authorName a string parameter with the author's name.
     * @param genre a string parameter with the chosen genre.
     * @return returns a book with all attributes for a book in the Book class.
     * */
    @Override
    public Book addBook(String title, String isbn, Date published, String authorName, String genre) {
        MongoDatabase database = connect();
        MongoCollection<Document> authorCollection = database.getCollection("author");
        MongoCollection<Document> genreCollection = database.getCollection("genre");
        ObjectId authorId = null;
        ObjectId genreId = null;
        FindIterable findAuthor = authorCollection.find(eq("authorName", authorName));
        FindIterable findGenre = genreCollection.find(eq("genreName", genre));

        for (MongoCursor<Document> cursor = findAuthor.iterator(); cursor.hasNext();) {
            Document authorDoc = cursor.next();
            authorId = authorDoc.getObjectId("_id");
        }

        for (MongoCursor<Document> cursor = findGenre.iterator(); cursor.hasNext();) {
            Document genreDoc = cursor.next();
            genreId = genreDoc.getObjectId("_id");
        }

        if (authorId == null){
            Document newAuthor = new Document("authorName", authorName);
            authorCollection.insertOne(newAuthor);
            authorId = newAuthor.getObjectId("_id");
            System.out.println(authorId);
        }

        if (genreId == null){
            Document newGenre = new Document("genreName", genre);
            genreCollection.insertOne(newGenre);
            genreId = newGenre.getObjectId("_id");
            System.out.println(genreId);
        }

        Document document = new Document("title", title) //Subdokument
                        .append("isbn", isbn)
                        .append("published", published)
                        .append("author", Arrays.asList(authorId))
                        .append("genre", Arrays.asList(genreId));
        MongoCollection<Document> collection = database.getCollection("book");
        collection.insertOne(document);
        ObjectId id = document.getObjectId("_id");
        return new Book(document.getString("isbn"), document.getString("title"), (Date) document.getDate("published"));
    }

    /**
     * This method adds a chosen genre to a book.
     * @param isbn a string parameter with a given isbn.
     * @param genre a string parameter with a chosen genre.
     * */
    @Override
    public void addGenreToBook(String isbn, String genre) throws BooksDbException {
        try {
            String sql = "SELECT * FROM book_genre WHERE book_id = ? and genre_id = ?";
            PreparedStatement stmt;
            stmt = this.con.prepareStatement(sql);
            int bookId = getBookIdByISBN(isbn);
            int genreId = getGenreIdByGenreName(genre);
            stmt.setInt(1, bookId);
            stmt.setInt(2, genreId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                sql = "INSERT INTO book_genre (book_id, genre_id) VALUES (?,?)";
                stmt = this.con.prepareStatement(sql);
                stmt.setInt(1, bookId);
                stmt.setInt(2, genreId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    /**
     * This method gets the id for a specific genre.
     * @param genre a string parameter with a chosen genre.
     * @return returns the id for the specific genre.
     * */
    private int getGenreIdByGenreName(String genre)
            throws BooksDbException {
        ResultSet rs = null;
        try {
            String sql = "SELECT genre_id FROM genre WHERE genre_name = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setString(1, genre);
            rs = stmt.executeQuery();
            int genreId = 0;
            while (rs.next()){
                genreId = rs.getInt("genre_id");
            }
            return genreId;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * This method adds an author to a specific book.
     * @param isbn a string parameter with a given isbn.
     * @param authorName a string parameter with a given name.
     * */
    @Override
    public void addAuthorToBook(String isbn, String authorName) throws BooksDbException {
        try {
            String sql = "SELECT * FROM book_author WHERE book_id = ? and author_id = ?";
            PreparedStatement stmt;
            stmt = this.con.prepareStatement(sql);
            int bookId = getBookIdByISBN(isbn);
            int authorId = getAuthorByName(authorName).getAuthorId();
            stmt.setInt(1, bookId);
            stmt.setInt(2, authorId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                sql = "INSERT INTO book_author (book_id, author_id) VALUES (?,?)";
                stmt = this.con.prepareStatement(sql);
                stmt.setInt(1, bookId);
                stmt.setInt(2, authorId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    /**
     * this method gets an id for a book with help from the isbn.
     * @param isbn a string parameter with a given isbn.
     * @return returns an int with the id of a specific book.
     * */
    @Override
    public int getBookIdByISBN(String isbn)
            throws BooksDbException {
        ResultSet rs = null;
        try {
            String sql = "SELECT book_id FROM book WHERE isbn = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setString(1, isbn);
            rs = stmt.executeQuery();
            int bookId = 0;
            while (rs.next()){
                bookId = rs.getInt("book_id");
            }
            return bookId;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * this method adds an author to the database.
     *
     * @param authorName a string parameter with a given name.
     * @return the newly created author or if
     */
    @Override
    public Author addAuthor(String authorName)
            throws BooksDbException {
        try {
            if (getAuthorByName(authorName).getAuthorId() == 0) {
                String sql = "INSERT INTO author (name) VALUES (?)";
                PreparedStatement stmt = this.con.prepareStatement(sql);
                stmt.setString(1, authorName);
                stmt.executeUpdate();
            }
            return getAuthorByName(authorName);
        } catch (SQLException e) {
            throw new BooksDbException("This author already exists", e);
        }
    }

    /**
     * This method returns an author by searching for the name.
     * @param authorName a string parameter with a name.
     * @return returns an author with its id and name.
     * */
    @Override
    public Author getAuthorByName(String authorName) throws BooksDbException{
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM author WHERE name = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setString(1, authorName);
            rs = stmt.executeQuery();
            int authorId = 0;
            String name = "";
            while (rs.next()){
                authorId = rs.getInt("author_id");
                name = rs.getString("name");
            }
            return new Author(authorId, name);
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * This book returns a book with a given id.
     * @param bookId an int with an id for a specific book.
     * @return returns a book with its id, title, isbn, and published date.
     * */
    @Override
    public Book getBookById(int bookId) throws BooksDbException{
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM book WHERE book_id = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            rs = stmt.executeQuery();
            String isbn = "";
            String title = "";
            Date published = null;
            while (rs.next()){
                bookId = rs.getInt("book_id");
                isbn = rs.getString("isbn");
                title = rs.getString("title");
                published = rs.getDate("published");
            }
            return new Book(bookId,isbn,title,published);
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * This book returns a list of authors connected with a specific bookid.
     * @param bookId an int with an id for a book.
     * @return a list of authors with the given bookid.
     * */
    @Override
    public List<Author> getAuthorsByBookId(int bookId)
            throws BooksDbException {
        ResultSet rs = null;
        try {
            List<Author> result = new ArrayList<>();
            String sql = "SELECT a.author_id, a.name FROM book b, author a, book_author ba WHERE b.book_id = ba.book_id AND ba.author_id = a.author_id AND b.book_id = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(new Author(rs.getInt("author_id"), rs.getString("name")));
            }
            return result;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * This book returns a list of genres connected with a specific bookid.
     * @param bookId an int with an id for a book.
     * @return a list of genres with the given bookid.
     * */
    @Override
    public List<Genre> getGenresByBookId(int bookId) throws BooksDbException {
        ResultSet rs = null;
        try {
            List<Genre> result = new ArrayList<>();
            String sql = "SELECT g.genre_name FROM book b, genre g, book_genre bg WHERE b.book_id = bg.book_id AND bg.genre_id = g.genre_id AND b.book_id = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(Genre.valueOf(rs.getString("genre_name")));
            }
            return result;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * @param bookId an int with an id for a book.
     * @return a list of all the reviews on the book with the specified bookId
     */
    @Override
    public List<Review> getReviewsByBookId(int bookId)
            throws BooksDbException {
        ResultSet rs = null;
        try {
            List<Review> result = new ArrayList<>();
            String sql = "SELECT * FROM review WHERE book_id = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(new Review(rs.getInt("book_id"), rs.getInt("user_id"), rs.getInt("stars"), rs.getDate("review_date"), rs.getString("review_text")));
            }
            return result;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * This method removes a book from the database by its id.
     * @param bookId an int with an id for a book.
     * @return true if the book has been removed.
     * */
    @Override
    public boolean removeBook(int bookId) throws BooksDbException {
        try {
            con.setAutoCommit(false);
            removeBookFromAuthor(bookId);
            removeBookFromGenre(bookId);
            removeBookReviews(bookId);
            String sql = "DELETE FROM book WHERE book_id = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
            con.commit();
            return true;
        } catch (SQLException e) {
            if (con != null){
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the SQL statement", e);
            }
        }
    }

    /**
     * @param bookId an int with an id for a book.
     * Removes all reviews on the specified book.
     */
    private void removeBookReviews(int bookId) throws BooksDbException{
        try {
            String sql = "DELETE FROM review WHERE book_id = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    /**
     * This method removes a book from an author.
     * @param bookId an int with an id for a book.
     * */
    private void removeBookFromAuthor(int bookId) throws BooksDbException{
        try {
            String sql = "DELETE FROM book_author WHERE book_id = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    /**
     * This method removes a book from a genre.
     * @param bookId an int with an id for a book.
     * */
    private void removeBookFromGenre(int bookId) throws BooksDbException{
        try {
            String sql = "DELETE FROM book_genre WHERE book_id = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }


    /**
     * This method allows logging in to an existing user if the password is equal to the encrypted one.
     * @param username a string parameter with a username.
     * @param password a string parameter with a password.
     * @return returns a user where password equals the encrypted one.
     * */
    @Override
    public User loginUser(String username, String password)
            throws BooksDbException {
        ResultSet rs = null;
        try {
            User user = null;
            String sql = "SELECT * FROM user WHERE username = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            while (rs.next()) {
                user = new User(rs.getInt("user_id"), rs.getString("name"), rs.getString("username"), rs.getString("password"));
            }
            return user.getPassword().equals(encryptPassword(password)) ? user : null;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * This method is used to register a new user to the database, by entering name, username and a password.
     * @param name a string parameter with a name.
     * @param username a string parameter with a username.
     * @param password a string password with a password.
     * @return returns true if register is succeeded, and false if not.
     * */
    @Override
    public boolean registerUser(String name, String username, String password)
            throws BooksDbException {
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM user WHERE username = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (!rs.next()){
                sql = "INSERT INTO user (name, username, password) VALUES (?, ?, ?)";
                stmt = this.con.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, username);
                stmt.setString(3, encryptPassword(password));
                stmt.executeUpdate();
                return true;
            }else{
                return false;
            }
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    /**
     * This method gets a list of all books in the database.
     * @return returns a list of books.
     * */
    @Override
    public List<Book> getAllBooks() {
        MongoDatabase database = connect();
        List<Book> books = new ArrayList<>();
        MongoCollection<Document> booksCollection = database.getCollection("books");
        FindIterable<Document> bookDocs = booksCollection.find();

        for (Document book: bookDocs) {
            books.add(new Book(book.getString("isbn"), book.getString("title"), (Date) book.getDate("published")));
        }
        return books;
    }

    /**
     * This method gets a list of all authors in the database.
     * @return returns a list of authors.
     * */
    @Override
    public List<Author> getAllAuthors() throws BooksDbException {
        ResultSet rs = null;
        try {
            List<Author> authors = new ArrayList<>();
            String sql = "SELECT * FROM author";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()){
                authors.add(new Author(rs.getInt("author_id"), rs.getString("name")));
            }
            return authors;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * This method is to make a review for a book with a rating a review-text.
     * @param bookId an int parameter with an id for a book.
     * @param userId an int parameter with an id for a user.
     * @param rating a double parameter with the rating for a book.
     * @param reviewText a String parameter with a review text.
     * */
    @Override
    public void reviewBook(int bookId, int userId, double rating, String reviewText) throws BooksDbException {
        try {
            String sql = "INSERT INTO review (book_id, user_id, stars, review_text, review_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            stmt.setInt(2, userId);
            stmt.setDouble(3, rating);
            stmt.setString(4, reviewText);
            stmt.setDate(5, Date.valueOf(LocalDate.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    /**
     * This method returns all the books that have yet not been reviewed by a user.
     * @param userId an int parameter with an id for a user.
     * @return returns a list of books.
     * */
    @Override
    public List<Book> getBooksNotReviewed(int userId) throws BooksDbException {
        ResultSet rs = null;
        try {
            List<Book> books = new ArrayList<>();
            String sql = "SELECT * FROM book WHERE book_id NOT IN (SELECT b.book_id FROM book b, review r WHERE r.book_id = b.book_id AND r.user_id = ?)";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            while (rs.next()){
                books.add(new Book(rs.getInt("book_id"), rs.getString("title"), rs.getString("isbn"), rs.getDate("published")));
            }
            return books;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * This method returns a user by their id.
     * @param userId an int parameter with a user id.
     * @return returns a user.
     * */
    @Override
    public User getUserById(int userId) throws BooksDbException{
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM user WHERE user_id = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            String name = "";
            String username = "";
            String password = "";
            while (rs.next()){
                name = rs.getString("name");
                username = rs.getString("username");
                password = rs.getString("password");
            }
            return new User(userId, name, username, password);
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new BooksDbException("There is something wrong with the connection", e);
            }
        }
    }

    /**
     * This method takes a string parameter with a password and returns it encrypted.
     * @return returns a string of an encrypted password.
     * */
    private String encryptPassword(String password){
        String encryptedpassword = null;
        try
        {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(password.getBytes());
            byte[] bytes = m.digest();
            StringBuilder s = new StringBuilder();
            for(int i=0; i< bytes.length; i++)
            {
                s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            encryptedpassword = s.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return encryptedpassword;
    }
}

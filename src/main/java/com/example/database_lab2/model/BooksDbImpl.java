/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.database_lab2.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.set;

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
    private MongoDatabase database;

    private Connection con;

    public BooksDbImpl() {}

    @Override
    public void connect() {
        ConnectionString connectionString = new ConnectionString("mongodb://PaulW02:Adda2002%21@ac-edsmrsu-shard-00-00.4jdrzx1.mongodb.net:27017,ac-edsmrsu-shard-00-01.4jdrzx1.mongodb.net:27017,ac-edsmrsu-shard-00-02.4jdrzx1.mongodb.net:27017/?ssl=true&replicaSet=atlas-7w5ul6-shard-0&authSource=admin&retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase(DB_NAME);
    }

    @Override
    public void disconnect() {
        try {
            this.con.close();
        } catch (SQLException e) {

        }
    }

    /**
     * Takes a string parameter with the searched title and returns all books containing the given string.
     * @param searchTitle takes a string parameter with the searched title
     * @return ArrayList with all books that have a title containing the given string.
     * */
    @Override
    public List<Book> searchBooksByTitle(String searchTitle)
    {
        MongoCollection<Document> bookCollection = database.getCollection("book");

        FindIterable<Document> find = bookCollection.find(regex("title", java.util.regex.Pattern.compile(searchTitle)));

        List<Book> books = new ArrayList<>();
        for (MongoCursor<Document> cursor = find.iterator(); cursor.hasNext();) {
            Document bookDoc = cursor.next();
            books.add(new Book(
                    bookDoc.getString("isbn"),
                    bookDoc.getString("title"),
                    (Date) bookDoc.getDate("published")
            ));
        }
        return books;
        /*MongoCollection<Document> bookCollection = database.getCollection("book");

        List<Book> result = new ArrayList<>();
        searchTitle = searchTitle.toLowerCase();

        BasicDBObject query = new BasicDBObject();
        query.put("title", new BasicDBObject("$regex", java.util.regex.Pattern.compile(searchTitle)));

        MongoCursor<Document> cursor = bookCollection.find(query).iterator();
            while (cursor.hasNext()) {
                Document bookDoc = cursor.next();
                result.add(new Book(
                        bookDoc.getString("isbn"),
                        bookDoc.getString("title"),
                        (Date) bookDoc.getDate("published")
                ));
            }
        return result;*/
    }

    /**
     * Takes a string parameter with the searched ISBN and returns all books containing the given string.
     * @param searchISBN takes a string parameter with the searched ISBN
     * @return ArrayList with all books that have a ISBN containing the given string.
     * */
    @Override
    public List<Book> searchBooksByISBN(String searchISBN)
    {
        MongoCollection<Document> bookCollection = database.getCollection("book");

        List<Book> result = new ArrayList<>();
        searchISBN = searchISBN.toLowerCase();

        BasicDBObject query = new BasicDBObject();
        query.put("searchISBN", java.util.regex.Pattern.compile(searchISBN));

        MongoCursor<Document> cursor = bookCollection.find(query).iterator();
        while (cursor.hasNext()) {
            Document bookDoc = cursor.next();
            result.add(new Book(
                    bookDoc.getString("isbn"),
                    bookDoc.getString("title"),
                    (Date) bookDoc.getDate("published")
            ));
        }
        return result;
    }

    /**
     * Takes a string parameter with the searched author and returns all books containing the given string.
     * @param searchAuthor takes a string parameter with the searched author
     * @return ArrayList with all books that have an author's name containing the given string.
     * */
    @Override
    public List<Book> searchBooksByAuthor(String searchAuthor)
    {
        MongoCollection<Document> bookCollection = database.getCollection("book");

        List<Book> result = new ArrayList<>();
        searchAuthor = searchAuthor.toLowerCase();

        BasicDBObject query = new BasicDBObject();
        query.put("searchAuthor", java.util.regex.Pattern.compile(searchAuthor));

        MongoCursor<Document> cursor = bookCollection.find(query).iterator();
        while (cursor.hasNext()) {
            Document bookDoc = cursor.next();
            result.add(new Book(
                    bookDoc.getString("isbn"),
                    bookDoc.getString("title"),
                    (Date) bookDoc.getDate("published")
            ));
        }
        return result;
    }

    /**
     * Takes a string parameter with the searched genre and returns all books containing the given string.
     * @param searchGenre takes a string parameter with the searched genre
     * @return ArrayList with all books that have a genre same as the one searched.
     * */
    @Override
    public List<Book> searchBooksByGenre(String searchGenre)
    {

        MongoCollection<Document> bookCollection = database.getCollection("book");

        List<Book> result = new ArrayList<>();
        searchGenre = searchGenre.toLowerCase();

        BasicDBObject query = new BasicDBObject();
        query.put("searchGenre", java.util.regex.Pattern.compile(searchGenre));

        MongoCursor<Document> cursor = bookCollection.find(query).iterator();
        while (cursor.hasNext()) {
            Document bookDoc = cursor.next();
            result.add(new Book(
                    bookDoc.getString("isbn"),
                    bookDoc.getString("title"),
                    (Date) bookDoc.getDate("published")
            ));
        }
        return result;
    }

    /**
     * Takes a string parameter with the searched stars and returns all books containing the given string.
     * @param searchStars takes a string parameter with the searched stars
     * @return ArrayList with all books that have the same amount of stars as given.
     * */
    @Override
    public List<Book> searchBooksByStars(String searchStars)
    {

        MongoCollection<Document> bookCollection = database.getCollection("book");

        List<Book> result = new ArrayList<>();
        searchStars = searchStars.toLowerCase();

        BasicDBObject query = new BasicDBObject();
        query.put("searchStars", java.util.regex.Pattern.compile(searchStars));

        MongoCursor<Document> cursor = bookCollection.find(query).iterator();
        while (cursor.hasNext()) {
            Document bookDoc = cursor.next();
            result.add(new Book(
                    bookDoc.getString("isbn"),
                    bookDoc.getString("title"),
                    (Date) bookDoc.getDate("published")
            ));
        }
        return result;
    }

    /**
     * This method is used to take a new given title to a book and replace it with the title of an existing book.
     *
     * @param newTitle takes a string with a given title.
     * @param isbn     takes an int with an id to specify a book.
     */
    @Override
    public void updateTitleBook(String newTitle, String isbn)
    {
        MongoCollection<Document> collection = database.getCollection("book");
        collection.updateOne(eq("isbn", isbn), set("title", newTitle));
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
                        .append("genre", Arrays.asList(genreId))
                        .append("review", Arrays.asList());
        MongoCollection<Document> collection = database.getCollection("book");
        collection.insertOne(document);
        return new Book(document.getString("isbn"), document.getString("title"), (Date) document.getDate("published"));
    }

    /**
     * This method adds a chosen genre to a book.
     * @param isbn a string parameter with a given isbn.
     * @param genre a string parameter with a chosen genre.
     * */
    @Override
    public void addGenreToBook(String isbn, String genre) {

        MongoCollection<Document> bookCollection = database.getCollection("book");
        MongoCollection<Document> genreCollection = database.getCollection("genre");
        ObjectId genreId = null;
        FindIterable findGenre = genreCollection.find(eq("genreName", genre));

        for (MongoCursor<Document> cursor = findGenre.iterator(); cursor.hasNext();) {
            Document genreDoc = cursor.next();
            genreId = genreDoc.getObjectId("_id");
        }

        if (genreId == null){
            Document document = new Document("genreName", genre);
            genreCollection.insertOne(document);
            genreId = document.getObjectId("_id");
        }
        Document query = new Document()
                .append("genre", new Document("$in", Collections.singletonList(genreId)))
                .append("isbn", new Document("$eq", isbn));

        FindIterable<Document> result = bookCollection.find(query);

        if (result.first() == null) {
            Document update = new Document("$push", new Document("genre", genreId));
            bookCollection.updateOne(eq("isbn", isbn), update);
        }
    }

    /**
     * This method gets the id for a specific genre.
     * @param genre a string parameter with a chosen genre.
     * @return returns the id for the specific genre.
     * */
    private int getGenreIdByGenreName(String genre)
    {
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

        } finally {
            try {
                rs.close();
            } catch (SQLException e) {

            }
        }
        return 0;
    }

    /**
     * This method adds an author to a specific book.
     * @param isbn a string parameter with a given isbn.
     * @param authorName a string parameter with a given name.
     * */
    @Override
    public void addAuthorToBook(String isbn, String authorName) {
        MongoCollection<Document> bookCollection = database.getCollection("book");
        MongoCollection<Document> authorCollection = database.getCollection("author");
        ObjectId authorId = null;
        FindIterable findAuthor = authorCollection.find(eq("author", authorName));

        for (MongoCursor<Document> cursor = findAuthor.iterator(); cursor.hasNext();) {
            Document authorDoc = cursor.next();
            authorId = authorDoc.getObjectId("_id");
        }

        if (authorId == null){
            Document document = new Document("authorName", authorName);
            authorCollection.insertOne(document);
            authorId = document.getObjectId("_id");
        }
        Document query = new Document()
                .append("author", new Document("$in", Collections.singletonList(authorId)))
                .append("isbn", new Document("$eq", isbn));

        FindIterable<Document> result = bookCollection.find(query);

        if (result.first() == null) {
            Document update = new Document("$push", new Document("author", authorId));
            bookCollection.updateOne(eq("isbn", isbn), update);
        }
    }

    /**
     * this method gets an id for a book with help from the isbn.
     * @param isbn a string parameter with a given isbn.
     * @return returns an int with the id of a specific book.
     * */
    @Override
    public int getBookIdByISBN(String isbn)
    {
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

        } finally {
            try {
                rs.close();
            } catch (SQLException e) {

            }
        }
        return 0;
    }

    /**
     * this method adds an author to the database.
     *
     * @param authorName a string parameter with a given name.
     * @return the newly created author or if
     */
    @Override
    public Author addAuthor(String authorName)
    {
        MongoCollection<Document> collection = database.getCollection("author");
        Document author = collection.find(eq("name", authorName)).first();
        if (author == null) {
            author = new Document("name", authorName);
            collection.insertOne(author);
        }
        return new Author(author.getString("name"));
    }

    /**
     * This method returns an author by searching for the name.
     * @param authorName a string parameter with a name.
     * @return returns an author with its id and name.
     * */
    @Override
    public Author getAuthorByName(String authorName){
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

        } finally {
            try {
                rs.close();
            } catch (SQLException e) {

            }
        }
        return null;
    }

    /**
     * This book returns a book with a given id.
     * @param isbn an int with an id for a specific book.
     * @return returns a book with its id, title, isbn, and published date.
     * */
    @Override
    public Book getBookByISBN(String isbn){
        MongoCollection<Document> booksCollection = database.getCollection("book");
        FindIterable<Document> bookDocs = booksCollection.find(eq("isbn", isbn));
        Document bookDoc = bookDocs.iterator().next();
        return new Book(bookDoc.getString("isbn"), bookDoc.getString("title"), bookDoc.getDate("published"));
    }

    /**
     * This book returns a list of authors connected with a specific bookid.
     * @param bookId an int with an id for a book.
     * @return a list of authors with the given bookid.
     * */
    @Override
    public List<Author> getAuthorsByBookId(int bookId)
    {
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

        } finally {
            try {
                rs.close();
            } catch (SQLException e) {

            }
        }
        return null;
    }

    /**
     * This book returns a list of genres connected with a specific bookid.
     * @param bookId an int with an id for a book.
     * @return a list of genres with the given bookid.
     * */
    @Override
    public List<Genre> getGenresByBookId(int bookId) {
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

        } finally {
            try {
                rs.close();
            } catch (SQLException e) {

            }
        }
        return null;
    }

    /**
     * @param bookId an int with an id for a book.
     * @return a list of all the reviews on the book with the specified bookId
     */
    @Override
    public List<Review> getReviewsByBookId(int bookId)
    {
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

        } finally {
            try {
                rs.close();
            } catch (SQLException e) {

            }
        }
        return null;
    }

    /**
     * This method removes a book from the database by its id.
     *
     * @param isbn an int with an id for a book.
     * @return true if the book has been removed.
     */
    @Override
    public boolean removeBook(String isbn) {
        MongoCollection<Document> bookCollection = database.getCollection("book");
        ObjectId bookId = null;
        FindIterable findbook = bookCollection.find(eq("isbn", isbn));

        for (MongoCursor<Document> cursor = findbook.iterator(); cursor.hasNext();) {
            Document bookDoc = cursor.next();
            bookId = bookDoc.getObjectId("_id");
        }

        if (bookId != null){
            bookCollection.deleteOne(eq("isbn", isbn));
        }
        return false;
    }

    /**
     * @param bookId an int with an id for a book.
     * Removes all reviews on the specified book.
     */
    private void removeBookReviews(int bookId){
        try {
            String sql = "DELETE FROM review WHERE book_id = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {

        }
    }

    /**
     * This method removes a book from an author.
     * @param bookId an int with an id for a book.
     * */
    private void removeBookFromAuthor(int bookId){
        try {
            String sql = "DELETE FROM book_author WHERE book_id = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {

        }
    }

    /**
     * This method removes a book from a genre.
     * @param bookId an int with an id for a book.
     * */
    private void removeBookFromGenre(int bookId){
        try {
            String sql = "DELETE FROM book_genre WHERE book_id = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {

        }
    }


    /**
     * This method allows logging in to an existing user if the password is equal to the encrypted one.
     * @param username a string parameter with a username.
     * @param password a string parameter with a password.
     * @return returns a user where password equals the encrypted one.
     * */
    @Override
    public User loginUser(String username, String password) {
        MongoCollection<Document> userCollection = database.getCollection("user");
        FindIterable findAuthor = userCollection.find(and(eq("username", username), eq("password", encryptPassword(password))));

        for (MongoCursor<Document> cursor = findAuthor.iterator(); cursor.hasNext();) {
            Document userDoc = cursor.next();
            return new User(userDoc.getString("name"), userDoc.getString("username"), userDoc.getString("password"));
        }
        return null;
    }

    /**
     * This method is used to register a new user to the database, by entering name, username and a password.
     * @param name a string parameter with a name.
     * @param username a string parameter with a username.
     * @param password a string password with a password.
     * @return returns true if register is succeeded, and false if not.
     * */
    @Override
    public boolean registerUser(String name, String username, String password) {
        MongoCollection<Document> userCollection = database.getCollection("user");
        ObjectId userId = null;
        FindIterable findAuthor = userCollection.find(eq("username", username));

        for (MongoCursor<Document> cursor = findAuthor.iterator(); cursor.hasNext();) {
            Document userDoc = cursor.next();
            userId = userDoc.getObjectId("_id");
        }

        if (userId == null){
            Document document = new Document("name", name) //Subdokument
                    .append("username", username)
                    .append("password", encryptPassword(password));
            MongoCollection<Document> collection = database.getCollection("user");
            collection.insertOne(document);
            return true;
        }
        return false;
    }

    /**
     * This method gets a list of all books in the database.
     * @return returns a list of books.
     * */
    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("book");
        MongoCursor<Document> cursor = collection.find().iterator();

        try {
            while (cursor.hasNext()) {
                Document book = cursor.next();
                books.add(new Book(book.getString("isbn"), book.getString("title"), book.getDate("published")));
                // do something with the book document
            }
        } finally {
            cursor.close();
        }
        return books;
    }

    /**
     * This method gets a list of all authors in the database.
     * @return returns a list of authors.
     * */
    @Override
    public List<Author> getAllAuthors() {

        List<Author> authors = new ArrayList<>();
        MongoCollection<Document> authorsCollection = database.getCollection("author");
        FindIterable<Document> authorDocs = authorsCollection.find();

        for (Document author: authorDocs) {
            authors.add(new Author(author.getString("authorName")));
        }
        return authors;
    }

    /**
     * This method is to make a review for a book with a rating a review-text.
     * @param isbn an String parameter with an isbn for a book.
     * @param username an String parameter with a username for a user.
     * @param rating a double parameter with the rating for a book.
     * @param reviewText a String parameter with a review text.
     * */
    @Override
    public void reviewBook(String isbn, String username, double rating, String reviewText) {
        MongoCollection<Document> userCollection = database.getCollection("user");
        ObjectId userId = null;
        FindIterable findUser = userCollection.find(eq("username", username));

        for (MongoCursor<Document> cursor = findUser.iterator(); cursor.hasNext();) {
            Document userDoc = cursor.next();
            userId = userDoc.getObjectId("_id");
        }

        if (userId != null) {

            MongoCollection<Document> bookCollection = database.getCollection("book");
            Document query = new Document()
                    .append("review", new Document("$elemMatch", new Document("$eq", userId)))
                    .append("isbn", new Document("$eq", isbn));

            FindIterable<Document> result = bookCollection.find(query);

            if (result.first() == null) {
                Document document = new Document("user", userId) //Subdokument
                        .append("rating", rating)
                        .append("reviewText", reviewText);
                MongoCollection<Document> reviewCollection = database.getCollection("review");
                reviewCollection.insertOne(document);
                addReviewToBook(document.getObjectId("_id"), isbn);
            }

        }

    }

    @Override
    public void addReviewToBook(ObjectId id, String isbn) {
        MongoCollection<Document> bookCollection = database.getCollection("book");
        Document update = new Document("$push", new Document("review", id));
        bookCollection.updateOne(eq("isbn", isbn), update);
    }

    /**
     * This method returns all the books that have yet not been reviewed by a user.
     *
     * @param username an int parameter with an id for a user.
     * @return returns a list of books.
     */
    @Override
    public List<Book> getBooksNotReviewed(String username) {
        ObjectId userId = getUserIdByUsername(username);
        List<Book> books = new ArrayList<>();
        MongoCollection<Document> booksCollection = database.getCollection("book");
        MongoCollection<Document> reviewsCollection = database.getCollection("review");

// Find the object IDs of the reviews written by the user
        List<ObjectId> reviewIds = reviewsCollection.find(eq("user", userId))
                .projection(fields(include("_id")))
                .map(doc -> doc.getObjectId("_id"))
                .into(new ArrayList<>());

        BasicDBObject query = new BasicDBObject();
        query.put("review", new BasicDBObject("$nin", reviewIds));

        MongoCursor<Document> cursor = booksCollection.find(query).iterator();

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            books.add(new Book(doc.getString("isbn"), doc.getString("title"), doc.getDate("published")));
            System.out.println(doc);
        }

        return books;
    }

    @Override
    public ObjectId getUserIdByUsername(String username) {
        MongoCollection<Document> collection = database.getCollection("user");
        FindIterable<Document> userDocs = collection.find(eq("username", username));
        Document userDoc = userDocs.iterator().next();
        return userDoc.getObjectId("_id");
    }

    /**
     * This method returns a user by their id.
     * @param username an string parameter with a username.
     * @return returns a user.
     * */
    @Override
    public User getUserByUsername(String username){
        return new User("", "", "");
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

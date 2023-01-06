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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
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
        List<Book> books = new ArrayList<>();

        bookCollection.createIndex(new BasicDBObject("title", "text"));
        BasicDBObject query = new BasicDBObject();
        query.put("title", new BasicDBObject("$regex", searchTitle).append("$options", "i"));

        MongoCursor<Document> cursor = bookCollection.find(query).iterator();

        while (cursor.hasNext()) {
            Document bookDoc = cursor.next();
            books.add(new Book(
                    bookDoc.getString("isbn"),
                    bookDoc.getString("title"),
                    bookDoc.getDate("published")
            ));
            System.out.println(bookDoc);
        }
        return books;
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
        searchISBN = searchISBN.toLowerCase();
        List<Book> result = new ArrayList<>();

        BasicDBObject query = new BasicDBObject();
        query.put("isbn", new BasicDBObject("$regex", searchISBN));

        MongoCursor<Document> cursor = bookCollection.find(query).iterator();
        while (cursor.hasNext()) {
            Document bookDoc = cursor.next();
            result.add(new Book(
                    bookDoc.getString("isbn"),
                    bookDoc.getString("title"),
                    bookDoc.getDate("published")
            ));
            System.out.println(bookDoc);
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
        //bookCollection.createIndex(new BasicDBObject("author.authorName", "authorText"));
        Document query = new Document("author.authorName", new Document("$regex", searchAuthor).append("$options", "i"));
        MongoCursor<Document> cursor = bookCollection.find(query).iterator();

        while (cursor.hasNext()) {
            Document bookDoc = cursor.next();
            result.add(new Book(
                    bookDoc.getString("isbn"),
                    bookDoc.getString("title"),
                    bookDoc.getDate("published")
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
        List<Bson> pipeline = Arrays.asList(
                new BasicDBObject("$unwind", "$genre"),
                new BasicDBObject("$lookup",
                        new BasicDBObject("from", "genre")
                                .append("localField", "genre")
                                .append("foreignField", "_id")
                                .append("as", "genre")
                ),
                new BasicDBObject("$match", new BasicDBObject("genre.genreName", searchGenre))
        );

        MongoCursor<Document> cursor = bookCollection.aggregate(pipeline).iterator();

        while (cursor.hasNext()) {
            Document bookDoc = cursor.next();
            result.add(new Book(
                    bookDoc.getString("isbn"),
                    bookDoc.getString("title"),
                    bookDoc.getDate("published")
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
        int searchInt = Integer.parseInt(searchStars);
        System.out.println(searchInt);
        List<Book> result = new ArrayList<>();
        List<Bson> pipeline = Arrays.asList(
                new BasicDBObject("$unwind", "$review"),
                new BasicDBObject("$lookup",
                        new BasicDBObject("from", "review")
                                .append("localField", "review")
                                .append("foreignField", "_id")
                                .append("as", "review")
                ),
                new BasicDBObject("$match", new BasicDBObject("rating", new BasicDBObject("$eq", (double) searchInt)))
        );
        MongoCursor<Document> cursor = bookCollection.aggregate(pipeline).iterator();

        while (cursor.hasNext()) {
            Document bookDoc = cursor.next();
            result.add(new Book(
                    bookDoc.getString("isbn"),
                    bookDoc.getString("title"),
                    bookDoc.getDate("published")
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
        MongoCollection<Document> booksCollection = database.getCollection("book");
        if (!(booksCollection.find(eq("isbn", isbn)).iterator().hasNext())) {
            Document document = new Document("title", title)
                    .append("isbn", isbn)
                    .append("published", published)
                    .append("author", Arrays.asList(new Document("authorName", authorName)))
                    .append("genre", Arrays.asList(new Document("genreName", genre)))
                    .append("review", new ArrayList<Document>());
            booksCollection.insertOne(document);
            addAuthor(authorName);
            addGenre(genre);
            return new Book(document.getString("isbn"), document.getString("title"), document.getDate("published"));
        }
        return null;
    }

    private Genre addGenre(String genre) {
        MongoCollection<Document> collection = database.getCollection("genre");
        Document genreDoc = collection.find(eq("genreName", genre)).first();
        if (genreDoc == null) {
            genreDoc = new Document("genreName", genre);
            collection.insertOne(genreDoc);
        }
        return Genre.valueOf(genreDoc.getString("genreName"));
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
        boolean newGenre = true;
        Document book = bookCollection.find(eq("isbn", isbn)).first();


        List<Document> genres = book.get("genre", List.class);

        // Add the new genre to the list
        FindIterable findGenre = genreCollection.find(eq("genreName", genre));

        if (!(findGenre.iterator().hasNext())){
            genreCollection.insertOne(new Document("genreName", genre));
        }

        for (Document genreDoc: genres) {
            if (genreDoc.get("genreName").equals(genre)){
                newGenre = false;
            }
        }
        if (newGenre){
            genres.add(new Document("genreName", genre));
        }
        // Update the book document with the modified list of genres
        Document update = new Document("$set", new Document("genre", genres));
        bookCollection.updateOne(eq("isbn", isbn), update);
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
        boolean newAuthor = true;
        Document book = bookCollection.find(eq("isbn", isbn)).first();


        List<Document> authors = book.get("author", List.class);

        // Add the new genre to the list
        FindIterable findAuthor = authorCollection.find(eq("authorName", authorName));

        if (!(findAuthor.iterator().hasNext())){
            authorCollection.insertOne(new Document("authorName", authorName));
        }

        for (Document authorDoc: authors) {
            if (authorDoc.get("authorName").equals(authorName)){
                newAuthor = false;
            }
        }
        if (newAuthor){
            authors.add(new Document("authorName", authorName));
        }
        // Update the book document with the modified list of genres
        Document update = new Document("$set", new Document("author", authors));
        bookCollection.updateOne(eq("isbn", isbn), update);
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
        Document author = collection.find(eq("authorName", authorName)).first();
        if (author == null) {
            author = new Document("authorName", authorName);
            collection.insertOne(author);
        }
        return new Author(author.getString("authorName"));
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
     * @param isbn an int with an id for a book.
     * @return a list of authors with the given bookid.
     * */
    @Override
    public List<Author> getAuthorsByISBN(String isbn)
    {
        List<Author> authors = new ArrayList<>();
        MongoCollection<Document> booksCollection = database.getCollection("book");

        List<Document> pipeline = Arrays.asList(
                new Document("$match", new Document("isbn", isbn))
        );

        MongoCursor<Document> cursor = booksCollection.aggregate(pipeline).iterator();

        while (cursor.hasNext()) {
            Document author = cursor.next();
            authors.add(new Author(author.getString("authorName")));
        }
        return authors;
    }

    /**
     * This book returns a list of genres connected with a specific bookid.
     * @param isbn an int with an id for a book.
     * @return a list of genres with the given bookid.
     * */
    @Override
    public List<Genre> getGenresByISBN(String isbn) {
        List<Genre> genres = new ArrayList<>();
        MongoCollection<Document> booksCollection = database.getCollection("book");
        List<Document> pipeline = Arrays.asList(
                new Document("$match", new Document("isbn", isbn))
        );

        MongoCursor<Document> cursor = booksCollection.aggregate(pipeline).iterator();

        while (cursor.hasNext()) {
            Document author = cursor.next();
            genres.add(Genre.valueOf(author.getString("genreName")));
        }
        return genres;
    }

    /**
     * @param isbn an int with an id for a book.
     * @return a list of all the reviews on the book with the specified bookId
     */
    @Override
    public List<Review> getReviewsByISBN(String isbn)
    {
        List<Review> reviews = new ArrayList<>();
        MongoCollection<Document> booksCollection = database.getCollection("book");

        List<Document> pipeline = Arrays.asList(
                new Document("$match", new Document("isbn", isbn))
        );

        MongoCursor<Document> cursor = booksCollection.aggregate(pipeline).iterator();

        while (cursor.hasNext()) {
            Document reviewDoc = cursor.next();
            reviews.add(new Review(isbn, reviewDoc.getString("user"), reviewDoc.getDouble("rating").intValue(), reviewDoc.getDate("reviewDate"), reviewDoc.getString("reviewText")));
        }

        return reviews;
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
        Document userDoc = null;
        FindIterable findUser = userCollection.find(eq("username", username));

        for (MongoCursor<Document> cursor = findUser.iterator(); cursor.hasNext();) {
            userDoc = cursor.next();
        }

        if (userDoc != null) {

            MongoCollection<Document> bookCollection = database.getCollection("book");
            Document query = new Document()
                    .append("review", new Document("$elemMatch", new Document("$eq", userDoc.get("username"))))
                    .append("isbn", new Document("$eq", isbn));

            FindIterable<Document> result = bookCollection.find(query);

            if (result.first() == null) {
                Document document = new Document("user", userDoc.get("username"))
                        .append("rating", rating)
                        .append("reviewDate", Date.valueOf(LocalDate.now()))
                        .append("reviewText", reviewText);
                MongoCollection<Document> reviewCollection = database.getCollection("review");
                reviewCollection.insertOne(document);
                addReviewToBook(document, isbn);
            }

        }
    }

    @Override
    public void addReviewToBook(Document document, String isbn) {
        MongoCollection<Document> bookCollection = database.getCollection("book");
        Document update = new Document("$push", new Document("review", document));
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
        List<Book> books = new ArrayList<>();
        MongoCollection<Document> booksCollection = database.getCollection("book");
        Document query = new Document("$or", Arrays.asList(
                new Document("review", new Document("$size", 0)),
                new Document("review.user", new Document("$ne", username))
        ));

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

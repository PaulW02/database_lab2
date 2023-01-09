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

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
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
    private MongoClient mongoClient;

    private Connection con;

    public BooksDbImpl() {}

    @Override
    public void connect() {
        ConnectionString connectionString = new ConnectionString("mongodb://PaulW02:Adda2002%21@ac-edsmrsu-shard-00-00.4jdrzx1.mongodb.net:27017,ac-edsmrsu-shard-00-01.4jdrzx1.mongodb.net:27017,ac-edsmrsu-shard-00-02.4jdrzx1.mongodb.net:27017/?ssl=true&replicaSet=atlas-7w5ul6-shard-0&authSource=admin&retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase(DB_NAME);
    }

    @Override
    public void disconnect() {
        this.mongoClient.close();
    }

    /**
     * Takes a string parameter with the searched title and returns all books containing the given string.
     * @param searchTitle takes a string parameter with the searched title
     * @return a list of books that match the search criteria
     * @throws BooksDbException
     * */
    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException {
        MongoCollection<Document> bookCollection = database.getCollection("book");
        List<Book> result = new ArrayList<>();

        Document query = new Document("title", new Document("$regex", searchTitle).append("$options", "i"));
        try {
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
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while searching for books by title: ", e);
        }
    }

    /**
     * Takes a string parameter with the searched ISBN and returns all books containing the given string.
     * @param searchISBN takes a string parameter with the searched ISBN
     * @return a list of books that match the search criteria
     * @throws BooksDbException
     * */
    @Override
    public List<Book> searchBooksByISBN(String searchISBN) throws BooksDbException {
        MongoCollection<Document> bookCollection = database.getCollection("book");
        List<Book> result = new ArrayList<>();

        Document query = new Document("isbn", new Document("$regex", searchISBN));
        try {
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
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while searching for books by isbn: ", e);
        }
    }

    /**
     * Takes a string parameter with the searched author and returns all books containing the given string.
     * @param searchAuthor takes a string parameter with the searched author
     * @return a list of books that match the search criteria
     * @throws BooksDbException
     * */
    @Override
    public List<Book> searchBooksByAuthor(String searchAuthor) throws BooksDbException {
        MongoCollection<Document> bookCollection = database.getCollection("book");
        List<Book> result = new ArrayList<>();

        Document query = new Document("author.authorName", new Document("$regex", searchAuthor).append("$options", "i"));
        try {
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
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while searching for books by author: ", e);
        }
    }

    /**
     * Takes a string parameter with the searched genre and returns all books containing the given string.
     * @param searchGenre takes a string parameter with the searched genre
     * @return a list of books that match the search criteria
     * @throws BooksDbException
     * */
    @Override
    public List<Book> searchBooksByGenre(String searchGenre) throws BooksDbException {

        MongoCollection<Document> bookCollection = database.getCollection("book");
        List<Book> result = new ArrayList<>();

        Document query = new Document("genre.genreName", new Document("$regex", searchGenre).append("$options", "i"));
        try {
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
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while searching for books by genre: ", e);
        }
    }

    /**
     * Searches the "book" collection for books that have received a specific rating in their reviews.
     *
     * @param searchStars the rating to search for
     * @return a list of books that match the search criteria
     * @throws BooksDbException
     */
    @Override
    public List<Book> searchBooksByStars(String searchStars) throws BooksDbException {
        int parsedRating = Integer.parseInt(searchStars);
        MongoCollection<Document> bookCollection = database.getCollection("book");
        List<Book> result = new ArrayList<>();

        List<Document> pipeline = Arrays.asList(
                new Document("$unwind", "$review"),
                new Document("$match", new Document("review.rating", parsedRating))
        );

        try {
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
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while searching for books by rating: ", e);
        }
    }

    /**
     * Updates the title of a book in the "book" collection.
     *
     * @param newTitle the new title of the book
     * @param isbn the ISBN of the book
     * @throws BooksDbException
     */
    @Override
    public void updateTitleBook(String newTitle, String isbn) throws BooksDbException {
        MongoCollection<Document> collection = database.getCollection("book");
        try {
            collection.updateOne(eq("isbn", isbn), set("title", newTitle));
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while updating the title of a book: ", e);
        }
    }

    /**
     * Adds a book to the "book" collection.
     *
     * @param title the title of the book
     * @param isbn the ISBN of the book
     * @param published the publication date of the book
     * @param authorName the name of the author of the book
     * @param genre the genre of the book
     * @return the added book, or null if the book already exists
     * @throws BooksDbException
     */
    @Override
    public Book addBook(String title, String isbn, Date published, String authorName, String genre) throws BooksDbException {
        MongoCollection<Document> booksCollection = database.getCollection("book");
        Book book = null;
        try {
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
                book = new Book(document.getString("isbn"), document.getString("title"), document.getDate("published"));
            }
            return book;
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while adding a book: ", e);
        }
    }

    /**
     * Adds a genre to the "genre" collection.
     *
     * @param genre the genre to add
     * @return the added genre
     * @throws BooksDbException
     */
    @Override
    public Genre addGenre(String genre) throws BooksDbException {
        MongoCollection<Document> collection = database.getCollection("genre");
        Genre result = null;
        try {
            Document genreDoc = collection.find(eq("genreName", genre)).first();
            if (genreDoc == null) {
                genreDoc = new Document("genreName", genre);
                collection.insertOne(genreDoc);
            }
            result = Genre.valueOf(genreDoc.getString("genreName"));
            return result;
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while adding a genre: ", e);
        }
    }

    /**
     * This method adds a chosen genre to a book.
     * @param isbn a string parameter with a given isbn.
     * @param genre a string parameter with a chosen genre.
     * @throws BooksDbException
     * */
    @Override
    public void addGenreToBook(String isbn, String genre) throws BooksDbException {
        MongoCollection<Document> bookCollection = database.getCollection("book");
        MongoCollection<Document> genreCollection = database.getCollection("genre");
        try {
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
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while adding a genre to a book: ", e);
        }
    }

    /**
     * This method adds an author to a specific book.
     * @param isbn a string parameter with a given isbn.
     * @param authorName a string parameter with a given name.
     * @throws BooksDbException
     * */
    @Override
    public void addAuthorToBook(String isbn, String authorName) throws BooksDbException {
        MongoCollection<Document> bookCollection = database.getCollection("book");
        MongoCollection<Document> authorCollection = database.getCollection("author");
        try {
            boolean newAuthor = true;
            Document book = bookCollection.find(eq("isbn", isbn)).first();


            List<Document> authors = book.get("author", List.class);

            // Add the new genre to the list
            FindIterable findAuthor = authorCollection.find(eq("authorName", authorName));

            if (!(findAuthor.iterator().hasNext())) {
                authorCollection.insertOne(new Document("authorName", authorName));
            }

            for (Document authorDoc : authors) {
                if (authorDoc.get("authorName").equals(authorName)) {
                    newAuthor = false;
                }
            }
            if (newAuthor) {
                authors.add(new Document("authorName", authorName));
            }
            // Update the book document with the modified list of genres
            Document update = new Document("$set", new Document("author", authors));
            bookCollection.updateOne(eq("isbn", isbn), update);
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while adding an author to a book: ", e);
        }
    }

    /**
     * Adds an author to the "author" collection.
     *
     * @param authorName the name of the author
     * @return the added author
     * @throws BooksDbException
     */
    @Override
    public Author addAuthor(String authorName) throws BooksDbException {
        MongoCollection<Document> collection = database.getCollection("author");
        try {
            Document author = collection.find(eq("authorName", authorName)).first();
            if (author == null) {
                author = new Document("authorName", authorName);
                collection.insertOne(author);
            }
            return new Author(author.getString("authorName"));
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while adding an author: ", e);
        }
    }

    /**
     * Gets a book from the "book" collection by its ISBN.
     *
     * @param isbn the ISBN of the book
     * @return the book with the specified ISBN, or null if it does not exist
     * @throws BooksDbException
     */
    @Override
    public Book getBookByISBN(String isbn) throws BooksDbException {
        MongoCollection<Document> booksCollection = database.getCollection("book");

        try {
            FindIterable<Document> bookDocs = booksCollection.find(eq("isbn", isbn));
            Document bookDoc = bookDocs.iterator().next();
            return new Book(bookDoc.getString("isbn"), bookDoc.getString("title"), bookDoc.getDate("published"));
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while getting book by isbn: ", e);
        }
    }

    /**
     * Gets a list of authors for a book in the "book" collection by its ISBN.
     *
     * @param isbn the ISBN of the book
     * @return a list of authors for the book with the specified ISBN
     * @throws BooksDbException
     */
    @Override
    public List<Author> getAuthorsByISBN(String isbn) throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        MongoCollection<Document> booksCollection = database.getCollection("book");
        try {
            MongoCursor<Document> cursor = booksCollection.find(eq("isbn", isbn)).iterator();
            while (cursor.hasNext()) {
                Document bookDoc = cursor.next();
                List<Document> authorDocs = bookDoc.get("author", List.class);
                for (Document authorDoc : authorDocs) {
                    authors.add(new Author(authorDoc.getString("authorName")));
                }
            }
            return authors;
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while getting authors to a book by isbn: ", e);
        }
    }

    /**
     * Gets a list of genres for a book in the "book" collection by its ISBN.
     *
     * @param isbn the ISBN of the book
     * @return a list of genres for the book with the specified ISBN
     * @throws BooksDbException
     */
    @Override
    public List<Genre> getGenresByISBN(String isbn) throws BooksDbException {
        List<Genre> genres = new ArrayList<>();
        MongoCollection<Document> booksCollection = database.getCollection("book");
        try {
            MongoCursor<Document> cursor = booksCollection.find(eq("isbn", isbn)).iterator();

            while (cursor.hasNext()) {
                Document bookDoc = cursor.next();
                List<Document> genreDocs = bookDoc.get("genre", List.class);
                for (Document genreDoc : genreDocs) {
                    genres.add(Genre.valueOf(genreDoc.getString("genreName")));
                }
            }
            return genres;
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while getting genres to a book by isbn: ", e);
        }
    }

    /**
     * Gets a list of reviews for a book in the "book" collection by its ISBN.
     *
     * @param isbn the ISBN of the book
     * @return a list of reviews for the book with the specified ISBN
     * @throws BooksDbException
     */
    @Override
    public List<Review> getReviewsByISBN(String isbn) throws BooksDbException {
        List<Review> reviews = new ArrayList<>();
        MongoCollection<Document> booksCollection = database.getCollection("book");

        try {
            MongoCursor<Document> cursor = booksCollection.find(eq("isbn", isbn)).iterator();

            while (cursor.hasNext()) {
                Document bookDoc = cursor.next();
                List<Document> reviewDocs = bookDoc.get("review", List.class);
                for (Document reviewDoc : reviewDocs) {
                    reviews.add(new Review(isbn, reviewDoc.getString("user"), reviewDoc.getDouble("rating").intValue(), reviewDoc.getDate("reviewDate"), reviewDoc.getString("reviewText")));
                }
            }

            return reviews;
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while getting reviews to a book by isbn: ", e);
        }
    }

    /**
     * Removes a book from the "book" collection by its ISBN.
     *
     * @param isbn the ISBN of the book
     * @throws BooksDbException
     */
    @Override
    public void removeBook(String isbn) throws BooksDbException {
        MongoCollection<Document> bookCollection = database.getCollection("book");
        ObjectId bookId = null;

        try {
            FindIterable findbook = bookCollection.find(eq("isbn", isbn));

            for (MongoCursor<Document> cursor = findbook.iterator(); cursor.hasNext(); ) {
                Document bookDoc = cursor.next();
                bookId = bookDoc.getObjectId("_id");
            }

            if (bookId != null) {
                bookCollection.deleteOne(eq("isbn", isbn));
            }
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while removing a book by isbn: ", e);
        }
    }


    /**
     * This method allows logging in to an existing user if the password is equal to the encrypted one.
     * @param username a string parameter with a username.
     * @param password a string parameter with a password.
     * @return returns a user where password equals the encrypted one.
     * @throws BooksDbException
     * */
    @Override
    public User loginUser(String username, String password) throws BooksDbException {
        MongoCollection<Document> userCollection = database.getCollection("user");
        User user = null;
        try {
            FindIterable findAuthor = userCollection.find(and(eq("username", username), eq("password", encryptPassword(password))));

            for (MongoCursor<Document> cursor = findAuthor.iterator(); cursor.hasNext(); ) {
                Document userDoc = cursor.next();
                user = new User(userDoc.getString("name"), userDoc.getString("username"), userDoc.getString("password"));
            }
            return user;
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while user logging in: ", e);
        }
    }

    /**
     * This method is used to register a new user to the database, by entering name, username and a password.
     * @param name a string parameter with a name.
     * @param username a string parameter with a username.
     * @param password a string password with a password.
     * @return returns true if register is succeeded, and false if not.
     * @throws BooksDbException
     * */
    @Override
    public boolean registerUser(String name, String username, String password) throws BooksDbException {
        MongoCollection<Document> userCollection = database.getCollection("user");
        ObjectId userId = null;
        boolean registered = false;
        try {
            FindIterable findAuthor = userCollection.find(eq("username", username));

            for (MongoCursor<Document> cursor = findAuthor.iterator(); cursor.hasNext(); ) {
                Document userDoc = cursor.next();
                userId = userDoc.getObjectId("_id");
            }

            if (userId == null) {
                Document document = new Document("name", name) //Subdokument
                        .append("username", username)
                        .append("password", encryptPassword(password));
                MongoCollection<Document> collection = database.getCollection("user");
                collection.insertOne(document);
                registered = true;
            }
            return registered;
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while registering user: ", e);
        }
    }

    /**
     * This method gets a list of all books in the database.
     * @return returns a list of books.
     * @throws BooksDbException
     * */
    @Override
    public List<Book> getAllBooks() throws BooksDbException {
        List<Book> books = new ArrayList<>();
        MongoCollection<Document> collection = database.getCollection("book");

        try {
            MongoCursor<Document> cursor = collection.find().iterator();

            try {
                while (cursor.hasNext()) {
                    Document book = cursor.next();
                    books.add(new Book(book.getString("isbn"), book.getString("title"), book.getDate("published")));
                }
            } finally {
                cursor.close();
            }
            return books;
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while getting all books: ", e);
        }
    }

    /**
     * This method gets a list of all authors in the database.
     * @return returns a list of authors.
     * @throws BooksDbException
     * */
    @Override
    public List<Author> getAllAuthors() throws BooksDbException {

        List<Author> authors = new ArrayList<>();
        MongoCollection<Document> authorsCollection = database.getCollection("author");

        try {
            FindIterable<Document> authorDocs = authorsCollection.find();

            for (Document author : authorDocs) {
                authors.add(new Author(author.getString("authorName")));
            }
            return authors;
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while getting all authors: ", e);
        }
    }


    /**
     * This method is to make a review for a book with a rating a review-text.
     * @param isbn a String parameter with an isbn for a book.
     * @param username a String parameter with a username for a user.
     * @param rating a double parameter with the rating for a book.
     * @param reviewText a String parameter with a review text.
     * @throws BooksDbException
     */
    @Override
    public void reviewBook(String isbn, String username, double rating, String reviewText) throws BooksDbException {
        MongoCollection<Document> userCollection = database.getCollection("user");
        Document userDoc = null;

        try {
            FindIterable findUser = userCollection.find(eq("username", username));

            for (MongoCursor<Document> cursor = findUser.iterator(); cursor.hasNext(); ) {
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
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while reviewing a book: ", e);
        }
    }

    /**
     * Adds a review to a book in the "book" collection by its ISBN.
     *
     * @param document the review to add
     * @param isbn the ISBN of the book
     * @throws BooksDbException
     */
    @Override
    public void addReviewToBook(Document document, String isbn) throws BooksDbException {
        MongoCollection<Document> bookCollection = database.getCollection("book");
        try {
            Document update = new Document("$push", new Document("review", document));
            bookCollection.updateOne(eq("isbn", isbn), update);
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while adding a review to a book: ", e);
        }
    }

    /**
     * Gets a list of books in the "book" collection that have not been reviewed by a specified user.
     *
     * @param username the user
     * @return a list of books that have not been reviewed by the user
     * @throws BooksDbException
     */
    @Override
    public List<Book> getBooksNotReviewed(String username) throws BooksDbException {
        List<Book> books = new ArrayList<>();
        MongoCollection<Document> booksCollection = database.getCollection("book");
        try {
            Document query = new Document("$or", Arrays.asList(
                    new Document("review", new Document("$size", 0)),
                    new Document("review.user", new Document("$ne", username))
            ));

            MongoCursor<Document> cursor = booksCollection.find(query).iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                books.add(new Book(doc.getString("isbn"), doc.getString("title"), doc.getDate("published")));
            }

            return books;
        } catch (MongoException e) {
            throw new BooksDbException("An exception occurred while getting books that have not been reviewed by the user: ", e);
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

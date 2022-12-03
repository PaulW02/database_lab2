/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.database_lab1.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A mock implementation of the BooksDBInterface interface to demonstrate how to
 * use it together with the user interface.
 * <p>
 * Your implementation must access a real database.
 *
 * @author anderslm@kth.se
 */
public class BooksDbMockImpl implements BooksDbInterface {

    public static final String DB_NAME = "booksdb";
    private Connection con;
    private final List<Book> books;

    public BooksDbMockImpl() {
        books = Arrays.asList(DATA);
    }

    @Override
    public boolean connect(String database) throws BooksDbException {
        String server = "jdbc:mysql://localhost:3306/" + database+ "?UseClientEnc=UTF8";
        this.con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(server, "root", "123457");
            System.out.println("Connection done!");
            return true;
        } catch (ClassNotFoundException e) {
            throw new BooksDbException("Class could not be found, check your driver", e);
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with your connection", e);
        }
    }

    @Override
    public void disconnect() throws BooksDbException {
        try {
            this.con.close();
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the connection", e);
        }
    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle)
            throws BooksDbException {
        try {
            List<Book> result = new ArrayList<>();
            searchTitle = searchTitle.toLowerCase();
            String sql = "SELECT * FROM book WHERE title LIKE '%" + searchTitle + "%'";
            Statement stmt = this.con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                result.add(new Book(rs.getInt("book_id"), rs.getString("isbn"), rs.getString("title"), rs.getDate("published")));
            }
            return result;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    @Override
    public List<Book> searchBooksByISBN(String searchISBN)
            throws BooksDbException {
        try {
            List<Book> result = new ArrayList<>();
            String sql = "SELECT * FROM book WHERE ISBN LIKE '%" + searchISBN + "%'";
            Statement stmt = this.con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                result.add(new Book(rs.getInt("book_id"), rs.getString("isbn"), rs.getString("title"), rs.getDate("published")));
            }
            return result;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    @Override
    public List<Book> searchBooksByAuthor(String searchAuthor)
            throws BooksDbException {
        try{
            List<Book> result = new ArrayList<>();
            searchAuthor = searchAuthor.toLowerCase();
            String sql = "SELECT * FROM book b, author  a, book_author ba WHERE b.bookid = ba.bookid AND ba.authorid = a.authorid AND a.name LIKE '%"+searchAuthor+"%'";
            Statement stmt = this.con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()){
                result.add(new Book(rs.getInt("book_id"), rs.getString("isbn"), rs.getString("title"), rs.getDate("published")));
            }
            return result;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    @Override
    public List<Book> searchBooksByGenre(String searchGenre)
            throws BooksDbException {
        try {
            List<Book> result = new ArrayList<>();
            searchGenre = searchGenre.toLowerCase();
            String sql = "SELECT * FROM book b, book_genre bg, genre g WHERE b.bookid = bg.bookid AND bg.genre_id = g.genre AND g.genre_name LIKE '%" + searchGenre + "%'";
            Statement stmt = this.con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                result.add(new Book(rs.getInt("book_id"), rs.getString("isbn"), rs.getString("title"), rs.getDate("published")));
            }
            return result;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    @Override
    public List<Book> searchBooksByStars(String searchStars)
            throws BooksDbException {
        try {
            List<Book> result = new ArrayList<>();
            String sql = "SELECT * FROM book b, review r WHERE b.bookid = r.bookid AND stars LIKE '%" + searchStars + "%'";
            Statement stmt = this.con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                result.add(new Book(rs.getInt("book_id"), rs.getString("isbn"), rs.getString("title"), rs.getDate("published")));
            }
            return result;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }//gör man såhär för stars?

    @Override
    public Book addBook(String title, String isbn, Date published, String authorName)
            throws BooksDbException {
        try {
            String sql = "INSERT INTO book (title, isbn, published) VALUES (?, ?, ?)";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, isbn);
            stmt.setDate(3, published);
            stmt.executeUpdate();
            addAuthor(authorName);
            addAuthorToBook(title, isbn, authorName);
            return new Book(getBookIdByTitleAndISBN(title, isbn), title, isbn, published);
        } catch (SQLException e) {
            System.out.println("");
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    @Override
    public void addAuthorToBook(String title, String isbn, String authorName) throws BooksDbException, SQLException {
        String sql;
        PreparedStatement stmt;
        sql = "INSERT INTO book_author (book_id, author_id) VALUES (?,?)";
        stmt = this.con.prepareStatement(sql);
        int bookId = getBookIdByTitleAndISBN(title, isbn);
        int authorId = getAuthorByName(authorName).getAuthorId();
        stmt.setInt(1, bookId);
        stmt.setInt(2, authorId);
        stmt.executeUpdate();
    }

    @Override
    public int getBookIdByTitleAndISBN(String title, String isbn)
            throws BooksDbException {
        try {
            String sql = "SELECT * FROM book WHERE title = ? AND isbn = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, isbn);
            ResultSet rs = stmt.executeQuery();
            int bookId = 0;
            while (rs.next()){
                bookId = rs.getInt("book_id");
            }
            return bookId;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    @Override
    public void addAuthor(String authorName)
            throws BooksDbException {
        try {
            if (getAuthorByName(authorName).getAuthorId() == 0) {
                String sql = "INSERT INTO author (name) VALUES (?)";
                PreparedStatement stmt = this.con.prepareStatement(sql);
                stmt.setString(1, authorName);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    @Override
    public Author getAuthorByName(String authorName) throws BooksDbException{
        try {
            String sql = "SELECT * FROM author WHERE name = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setString(1, authorName);
            ResultSet rs = stmt.executeQuery();
            int authorId = 0;
            String name = "";
            while (rs.next()){
                authorId = rs.getInt("author_id");
                name = rs.getString("name");
            }
            return new Author(authorId, name);
        }catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    @Override
    public List<Author> getAuthorsByBookId(int bookId)
            throws BooksDbException {
        try {
            List<Author> result = new ArrayList<>();
            String sql = "SELECT a.author_id, a.name FROM book b, author a, book_author ba WHERE b.book_id = ba.book_id AND ba.author_id = a.author_id AND b.book_id = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(new Author(rs.getInt("author_id"), rs.getString("name")));
            }
            return result;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    @Override
    public boolean loginUser(String username, String password)
            throws BooksDbException {
        try {
            User user = null;
            String sql = "SELECT * FROM user WHERE username = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                user = new User(rs.getInt("user_id"), rs.getString("name"), rs.getString("username"), rs.getString("password"));
            }
            return user != null && user.getPassword().equals(encryptPassword(password)) ? true : false;
        } catch (SQLException e) {
            throw new BooksDbException("There is something wrong with the SQL statement", e);
        }
    }

    @Override
    public boolean registerUser(String name, String username, String password)
            throws BooksDbException {
        try {
            String sql = "SELECT * FROM user WHERE username = ?";
            PreparedStatement stmt = this.con.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
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

    private String encryptPassword(String password){
        String encryptedpassword = null;
        try
        {
            /* MessageDigest instance for MD5. */
            MessageDigest m = MessageDigest.getInstance("MD5");

            /* Add plain-text password bytes to digest using MD5 update() method. */
            m.update(password.getBytes());

            /* Convert the hash value into bytes */
            byte[] bytes = m.digest();

            /* The bytes array has bytes in decimal form. Converting it into hexadecimal format. */
            StringBuilder s = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            /* Complete hashed password in hexadecimal format */
            encryptedpassword = s.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        /* Display the unencrypted and encrypted passwords. */
        System.out.println("Plain-text password: " + password);
        System.out.println("Encrypted password using MD5: " + encryptedpassword);
        return encryptedpassword;
    }


    private static final Book[] DATA = {
            new Book(1, "123456789", "Databases Illuminated", new Date(2018, 1, 1)),
            new Book(2, "234567891", "Dark Databases", new Date(1990, 1, 1)),
            new Book(3, "456789012", "The buried giant", new Date(2000, 1, 1)),
            new Book(4, "567890123", "Never let me go", new Date(2000, 1, 1)),
            new Book(5, "678901234", "The remains of the day", new Date(2000, 1, 1)),
            new Book(6, "234567890", "Alias Grace", new Date(2000, 1, 1)),
            new Book(7, "345678911", "The handmaids tale", new Date(2010, 1, 1)),
            new Book(8, "345678901", "Shuggie Bain", new Date(2020, 1, 1)),
            new Book(9, "345678912", "Microserfs", new Date(2000, 1, 1)),
    };
}

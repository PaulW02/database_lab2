/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.database_lab1.model;

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

    private Connection con;
    private final List<Book> books;

    public BooksDbMockImpl() {
        books = Arrays.asList(DATA);
    }

    @Override
    public boolean connect(String database) throws BooksDbException, ClassNotFoundException, SQLException {
        String server = "jdbc:mysql://localhost:3306/" + database+ "?UseClientEnc=UTF8";
        this.con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(server, "root", "123457");
            return true;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disconnect() throws BooksDbException, SQLException {
        this.con.close();
    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle)
            throws BooksDbException, SQLException, ClassNotFoundException {
        List<Book> result = new ArrayList<>();
        searchTitle = searchTitle.toLowerCase();
        connect("booksdb");
        String sql = "SELECT * FROM book WHERE title LIKE '%"+searchTitle+"%'";
        Statement stmt = this.con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()){
            result.add(new Book(rs.getInt("book_id"), rs.getString("isbn"), rs.getString("title"), rs.getDate("published")));
        }
        return result;
    }

    @Override
    public List<Book> searchBooksByISBN(String searchISBN)
            throws BooksDbException, SQLException, ClassNotFoundException {
        List<Book> result = new ArrayList<>();
        connect("booksdb");
        String sql = "SELECT * FROM book WHERE ISBN LIKE '%"+searchISBN+"%'";
        Statement stmt = this.con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()){
            result.add(new Book(rs.getInt("book_id"), rs.getString("isbn"), rs.getString("title"), rs.getDate("published")));
        }
        return result;
    }

    @Override
    public List<Book> searchBooksByAuthor(String searchAuthor)
            throws BooksDbException, SQLException, ClassNotFoundException {
        List<Book> result = new ArrayList<>();
        searchAuthor = searchAuthor.toLowerCase();
        connect("booksdb");
        String sql = "SELECT * FROM book WHERE author LIKE '%"+searchAuthor+"%'";
        Statement stmt = this.con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()){
            result.add(new Book(rs.getInt("book_id"), rs.getString("isbn"), rs.getString("title"), rs.getDate("published")));
        }
        return result;
    }

    @Override
    public List<Book> searchBooksByGenre(String searchGenre)
            throws BooksDbException, SQLException, ClassNotFoundException {
        List<Book> result = new ArrayList<>();
        searchGenre = searchGenre.toLowerCase();
        connect("booksdb");
        String sql = "SELECT * FROM book WHERE genre LIKE '%"+searchGenre+"%'";
        Statement stmt = this.con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()){
            result.add(new Book(rs.getInt("book_id"), rs.getString("isbn"), rs.getString("title"), rs.getDate("published")));
        }
        return result;
    }

    @Override
    public List<Book> searchBooksByStars(String searchStars)
            throws BooksDbException, SQLException, ClassNotFoundException {
        List<Book> result = new ArrayList<>();
        connect("booksdb");
        String sql = "SELECT * FROM book WHERE stars LIKE '%"+searchStars+"%'";
        Statement stmt = this.con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()){
            result.add(new Book(rs.getInt("book_id"), rs.getString("isbn"), rs.getString("title"), rs.getDate("published")));
        }
        return result;
    }//gör man såhär för stars?

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

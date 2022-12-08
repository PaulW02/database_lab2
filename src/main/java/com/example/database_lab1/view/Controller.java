package com.example.database_lab1.view;

import com.example.database_lab1.model.*;
import javafx.application.Platform;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.control.Alert.AlertType.*;

/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 * @author anderslm@kth.se
 */
public class Controller {

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model

    public static final String DB_NAME = "booksdb";

    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }

    protected void onAddBook(String title, String isbn, LocalDate published, String authorName, String genre){
        new Thread(() -> {
            try {
                if (title != null && isbn != null && published != null && authorName != null && genre != null) {
                    Date publishedDate = Date.valueOf(published);
                    booksDb.connect(DB_NAME);
                    booksDb.addBook(title, isbn, publishedDate, authorName, genre);
                    booksDb.disconnect();
                } else {
                    booksView.showAlertAndWait("Fill in all fields!", WARNING);
                }
            } catch (Exception e) {
                System.out.println(e);
                booksView.showAlertAndWait("Database error.", ERROR);
            }
        }).start();
    }

    protected void onRemoveBook(int bookId){
        new Thread(() -> {
            try {
                if (bookId != 0) {
                    booksDb.connect(DB_NAME);
                    booksDb.removeBook(bookId);
                    booksDb.disconnect();
                } else {
                    booksView.showAlertAndWait("Fill in all fields!", WARNING);
                }
            } catch (Exception e) {
                booksView.showAlertAndWait("Database error", ERROR);
            }
        }).start();
    }

    protected void onUpdateBook(int bookId, String newTitle, String newAuthor, String newGenre) {
        new Thread(() -> {
            try {
                booksDb.connect(DB_NAME);
                Book book = booksDb.getBookById(bookId);
                if (!newTitle.equals("")) {
                    booksDb.updateTitleBook(newTitle, bookId);
                }
                if (!newAuthor.equals("")) {
                    booksDb.addAuthor(newAuthor);
                    booksDb.addAuthorToBook(book.getTitle(), book.getIsbn(), newAuthor);
                }
                if (newGenre != null) {
                    booksDb.addGenreToBook(book.getTitle(), book.getIsbn(), newGenre);
                }
                booksDb.disconnect();
            } catch (Exception e) {
                booksView.showAlertAndWait("Database error", ERROR);
            }
        }).start();
    }

    protected void onSearchSelected(String searchFor, SearchMode mode) {
       new Thread() {
            {
                try {
                    System.out.println(searchFor);
                    if (searchFor != null && searchFor.length() > 1) {
                        booksDb.connect(DB_NAME);
                        List<Book> result = null;
                        System.out.println(searchFor);
                        switch (mode) {
                            case Title:
                                System.out.println(searchFor);
                                result = booksDb.searchBooksByTitle(searchFor);
                                System.out.println(result);
                                break;
                            case ISBN:
                                result = booksDb.searchBooksByISBN(searchFor);
                                break;
                            case Author:
                                result = booksDb.searchBooksByAuthor(searchFor);
                                break;
                            case Genre:
                                result = booksDb.searchBooksByGenre(searchFor);
                                break;
                            case Stars:
                                result = booksDb.searchBooksByStars(searchFor);
                            default:
                                result = new ArrayList<>();
                        }
                        if (result == null || result.isEmpty()) {
                            booksView.showAlertAndWait(
                                    "No results found.", INFORMATION);
                        } else {
                            for (Book book : result) {
                                book.setAuthors(booksDb.getAuthorsByBookId(book.getBookId()));
                            }
                            booksView.displayBooks(result);
                        }
                        booksDb.disconnect();
                    } else {
                        booksView.showAlertAndWait(
                                "Enter a search string!", WARNING);
                    }
                } catch (Exception e) {
                    booksView.showAlertAndWait("Database error.", ERROR);
                }
                Platform.runLater(
                        () -> {
                            System.out.println("TESSTT");
                        });
            }
        }.start();
    }

    protected void onLoginUser(String username, String password) {
        new Thread() {
            {
                try {
                    if (username != "" && password != "") {
                        booksDb.connect(DB_NAME);
                        final User user = booksDb.loginUser(username, password);
                        booksDb.disconnect();
                        System.out.println(user);
                        booksView.setCurrentUser(user);
                        Platform.runLater(
                                () -> {
                                    try {
                                        this.join();
                                        System.out.println(this.getId());
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                    } else {
                        booksView.showAlertAndWait("Fill in all fields!", WARNING);
                    }
                    booksDb.disconnect();
                } catch (Exception e) {
                    booksView.showAlertAndWait("Database error.", ERROR);
                }
            }
        }.start();
    }

    protected boolean onRegisterUser(String name, String username, String password) {
        try{
            if (name != null && username != null && password != null){
                booksDb.connect(DB_NAME);
                if (booksDb.registerUser(name, username, password)){
                    booksDb.disconnect();
                    return true;
                }else{
                    booksDb.disconnect();
                    return false;
                }
            }else{
                booksDb.disconnect();
                booksView.showAlertAndWait("Fill in all fields!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
        return false;
    }

    protected List<Book> getAllBooks() {
        try{
            List<Book> books = new ArrayList<>();
            booksDb.connect(DB_NAME);
            books = booksDb.getAllBooks();
            booksDb.disconnect();
            return books;
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
        return new ArrayList<>();
    }

    protected void onReviewBook(int bookId, int userId, String reviewText, double rating) {
        try{
            if (bookId != 0 && userId != 0 && reviewText != "" && rating != 0){
                booksDb.connect(DB_NAME);
                booksDb.reviewBook(bookId, userId, rating, reviewText);
                booksDb.disconnect();
            }else{
                booksView.showAlertAndWait("Fill in all fields!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
    }

    protected List<Book> getBooksNotReviewed(int userId) {
        try{
            booksDb.connect(DB_NAME);
            List<Book> books = booksDb.getBooksNotReviewed(userId);
            booksDb.disconnect();
            return books;
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
        return new ArrayList<>();
    }


    // TODO:
    // Add methods for all types of user interaction (e.g. via  menus).
}

package com.example.database_lab1.view;

import com.example.database_lab1.model.*;

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
        try{
            if (title != null && isbn != null && published != null && authorName != null && genre != null){
                Date publishedDate = Date.valueOf(published);
                booksDb.connect(DB_NAME);
                booksDb.addBook(title, isbn, publishedDate, authorName, genre);
            }else{
                booksView.showAlertAndWait("Fill in all fields!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
    }

    protected void onRemoveBook(int bookId){
        try {
            if (bookId != 0){
                booksDb.connect(DB_NAME);
                booksDb.removeBook(bookId);
            }else{
                booksView.showAlertAndWait("Fill in all fields!",WARNING);
            }
        }catch (Exception e){
            booksView.showAlertAndWait("Database error",ERROR);
        }
    }

    protected void onUpdateBook(String title, String isbn,String newTitle){
        try {
            if (title != null){
                booksDb.connect(DB_NAME);
                booksDb.updateBook(title,newTitle,isbn);
            }else{
                booksView.showAlertAndWait("Fill in all fields!",WARNING);
            }
        }catch (Exception e){
            booksView.showAlertAndWait("Database error",ERROR);
        }
    }

    protected void onSearchSelected(String searchFor, SearchMode mode) {
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
                        result= new ArrayList<>();
                }
                if (result == null || result.isEmpty()) {
                    booksView.showAlertAndWait(
                            "No results found.", INFORMATION);
                } else {
                    for (Book book: result) {
                        book.setAuthors(booksDb.getAuthorsByBookId(book.getBookId()));
                    }
                    booksView.displayBooks(result);
                }
            } else {
                booksView.showAlertAndWait(
                        "Enter a search string!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
    }

    protected User onLoginUser(String username, String password) {
        try{
            if (username != null && password != null){
                booksDb.connect(DB_NAME);
                return booksDb.loginUser(username, password);
            }else{
                booksView.showAlertAndWait("Fill in all fields!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
        return null;
    }

    protected boolean onRegisterUser(String name, String username, String password) {
        try{
            if (name != null && username != null && password != null){
                booksDb.connect(DB_NAME);
                return booksDb.registerUser(name, username, password) ? true : false;
            }else{
                booksView.showAlertAndWait("Fill in all fields!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
        return false;
    }

    protected List<Book> getAllBooks() {
        try{
            booksDb.connect(DB_NAME);
            return booksDb.getAllBooks();
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
        return new ArrayList<>();
    }

    protected List<Book> getBooksNotReviewed(int userId) {
        try{
            booksDb.connect(DB_NAME);
            return booksDb.getBooksNotReviewed(userId);
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
        return new ArrayList<>();
    }

    protected void onReviewBook(int bookId, int userId, String reviewText, double rating) {
        try{
            if (bookId != 0 && userId != 0 && reviewText != null && rating != 0){
                booksDb.connect(DB_NAME);
                booksDb.reviewBook(bookId, userId, rating, reviewText);
            }else{
                booksView.showAlertAndWait("Fill in all fields!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
    }


    // TODO:
    // Add methods for all types of user interaction (e.g. via  menus).
}

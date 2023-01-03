package com.example.database_lab2.view;

import com.example.database_lab2.model.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
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
        this.booksDb.connect();
    }

    protected void onAddBook(String title, String isbn, LocalDate published, String authorName, String genre){
        new Thread(() -> {
            try {
                if (title != "" && isbn != "" && published != null && authorName != "" && genre != "") {
                    Date publishedDate = Date.valueOf(published);
                    //booksDb.connect(DB_NAME);
                    Book newBook = booksDb.addBook(title, isbn, publishedDate, authorName, genre);

                    Platform.runLater(() -> {
                        List<Book> books = booksView.getBooks();
                        List<Book> booksNotReviewed = booksView.getBooksNotReviewed();
                        books.add(newBook);
                        booksNotReviewed.add(newBook);
                        booksView.setBooks(books);
                        booksView.setBooksNotReviewed(booksNotReviewed);
                    });
                    booksDb.disconnect();
                } else {
                    Platform.runLater(() -> booksView.showAlertAndWait("Fill in all fields!", WARNING));
                }
            } catch (Exception e) {
                Platform.runLater(() -> booksView.showAlertAndWait("Database error", ERROR));
            }
        }).start();
    }

    protected void onRemoveBook(String isbn){
        new Thread(() -> {
            try {
                if (isbn != "") {
                    //booksDb.connect(DB_NAME);
                    Book deletedBook = booksDb.getBookByISBN(isbn);
                    booksDb.removeBook(isbn);
                    Platform.runLater(() -> {
                        List<Book> books = booksView.getBooks();
                        List<Book> booksNotReviewed = booksView.getBooksNotReviewed();
                        for (Book book: books) {
                            if (book.getBookId() == deletedBook.getBookId()){
                                books.remove(books.indexOf(book));
                                break;
                            }
                        }
                        for (Book b: booksNotReviewed) {
                            if (b.getBookId() == deletedBook.getBookId()){
                                booksNotReviewed.remove(booksNotReviewed.indexOf(b));
                                break;
                            }
                        }
                        booksView.setBooks(books);
                        booksView.setBooksNotReviewed(booksNotReviewed);
                    });
                    booksDb.disconnect();
                } else {
                    Platform.runLater(() -> booksView.showAlertAndWait("Fill in all fields!", WARNING));
                }
            } catch (Exception e) {
                Platform.runLater(() -> booksView.showAlertAndWait("Database error", ERROR));
            }
        }).start();
    }

    protected void onUpdateBook(String isbn, String newTitle, String newAuthor, String newGenre) {
        new Thread(() -> {
            try {
                //booksDb.connect(DB_NAME);
                Book bookToBeUpdated = booksDb.getBookByISBN(isbn);
                if (newTitle.length() > 1) {
                    booksDb.updateTitleBook(newTitle, isbn);
                }
                if (newAuthor.length() > 1) {
                    booksDb.addAuthor(newAuthor);
                    booksDb.addAuthorToBook(bookToBeUpdated.getIsbn(), newAuthor);
                }
                if (newGenre != null) {
                    booksDb.addGenreToBook(bookToBeUpdated.getIsbn(), newGenre);
                }
                System.out.println("Test1");
                Book updatedBook = booksDb.getBookByISBN(isbn);
                System.out.println("Test2");
                Platform.runLater(() -> {
                    List<Book> books = booksView.getBooks();
                    List<Book> booksNotReviewed = booksView.getBooksNotReviewed();
                    for (Book b: books) {
                        if (b.getBookId() == updatedBook.getBookId()){
                            books.set(books.indexOf(b), updatedBook);
                            break;
                        }
                    }
                    for (Book b: booksNotReviewed) {
                        if (b.getBookId() == updatedBook.getBookId()){
                            booksNotReviewed.set(booksNotReviewed.indexOf(b), updatedBook);
                            break;
                        }
                    }
                    booksView.setBooks(books);
                    booksView.setBooksNotReviewed(booksNotReviewed);
                });
                booksDb.disconnect();
            } catch (Exception e) {
                Platform.runLater(() -> booksView.showAlertAndWait("Database error", ERROR));
            }
        }).start();
    }

    protected void onSearchSelected(String searchFor, SearchMode mode) {
       new Thread() {
            {
                try {
                    if (searchFor != null && searchFor.length() > 0) {
                        //booksDb.connect(DB_NAME);
                        List<Book> result = new ArrayList<>();
                        switch (mode) {
                            case Title:
                                result = booksDb.searchBooksByTitle(searchFor);
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
                        }
                        if (result == null || result.isEmpty()) {
                            Platform.runLater(() -> booksView.showAlertAndWait("No results found.", INFORMATION));
                        } else {
                            for (Book book : result) {
                                book.setAuthors(booksDb.getAuthorsByBookId(book.getBookId()));
                                book.setGenres(booksDb.getGenresByBookId(book.getBookId()));
                                book.setReviews(booksDb.getReviewsByBookId(book.getBookId()));
                            }
                            final List<Book> books = result;
                            Platform.runLater(() -> booksView.displayBooks(books));

                        }
                        booksDb.disconnect();
                    } else {
                        Platform.runLater(() -> booksView.showAlertAndWait("Fill in all fields!", WARNING));
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> booksView.showAlertAndWait("Database error", ERROR));
                }
            }
        }.start();
    }

    protected void onLoginUser(String username, String password) {
        new Thread(() -> {
            try {
                if (username != "" && password != "") {
                    //booksDb.connect(DB_NAME);
                    final User user = booksDb.loginUser(username, password);
                    javafx.application.Platform.runLater(() -> {
                        this.booksView.setCurrentUser(user);
                        if (this.booksView.getCurrentUser() != null){
                            this.booksView.getInitLoginPopupwindow().close();
                            this.booksView.getUsernameLbl().setText("Hello " + user.getUsername());
                            this.booksView.getLoginBtn().setText("Log out");
                            this.booksView.getLoginBtn().setOnAction(l -> {
                                this.booksView.setCurrentUser(null);
                                this.booksView.getUsernameLbl().setText("");
                                this.booksView.getSignUpBtn().setDisable(false);
                                booksView.initUserView(this);
                            });
                            this.booksView.getSignUpBtn().setDisable(true);
                            getBooksNotReviewed(user.getUsername());
                        }else{
                            booksView.showAlertAndWait("The password is wrong!", Alert.AlertType.WARNING);
                        }
                    });
                    booksDb.disconnect();
                }else {
                    Platform.runLater(() -> booksView.showAlertAndWait("Fill in all fields!", WARNING));
                }
                booksDb.disconnect();
            } catch (Exception e) {
                Platform.runLater(() -> booksView.showAlertAndWait("This username does not exist!", ERROR));
            }
        }).start();
    }

    protected void onRegisterUser(String name, String username, String password) {
        new Thread(() -> {
            try {
                if (name != "" && username != "" && password != "") {
                    //booksDb.connect(DB_NAME);
                    if (booksDb.registerUser(name, username, password)) {
                        final User user = booksDb.loginUser(username, password);
                        javafx.application.Platform.runLater(() -> {
                            this.booksView.setCurrentUser(user);
                            if (this.booksView.getCurrentUser() != null){
                                this.booksView.getInitRegisterPopupWindow().close();
                                this.booksView.getUsernameLbl().setText("Hello " + user.getUsername());
                                this.booksView.getLoginBtn().setText("Log out");
                                this.booksView.getLoginBtn().setOnAction(l -> {
                                    this.booksView.setCurrentUser(null);
                                    this.booksView.getUsernameLbl().setText("");
                                    this.booksView.getSignUpBtn().setDisable(false);
                                    booksView.initUserView(this);
                                });
                                this.booksView.getSignUpBtn().setDisable(true);
                                //getBooksNotReviewed(user.getUserId());
                            }else{
                                booksView.showAlertAndWait("The username or password is wrong!", Alert.AlertType.WARNING);
                            }
                        });
                    }
                    booksDb.disconnect();
                } else {
                    Platform.runLater(() -> booksView.showAlertAndWait("Fill in all fields!", WARNING));
                }
            } catch (Exception e) {
                Platform.runLater(() -> booksView.showAlertAndWait("Database error", ERROR));
            }
        }).start();
    }

    protected void getAllBooks() {
        new Thread(() -> {
            try {
                //booksDb.connect(DB_NAME);
                final List<Book> books = booksDb.getAllBooks();
                javafx.application.Platform.runLater(() -> booksView.setBooks(books));
                booksDb.disconnect();
            } catch (Exception e) {
                Platform.runLater(() -> booksView.showAlertAndWait("Database error", ERROR));
            }
        }).start();
    }

    protected void onReviewBook(String isbn, String username, String reviewText, double rating) {
        new Thread(() -> {
            try {
                if (isbn != "" && username != "" && reviewText != "" && rating != 0) {
                    //booksDb.connect(DB_NAME);
                    Book reviewedBook = booksDb.getBookByISBN(isbn);
                    booksDb.reviewBook(isbn, username, rating, reviewText);
                    Platform.runLater(() -> {
                        List<Book> books = booksView.getBooksNotReviewed();
                        Iterator<Book> bookIterator = books.iterator();
                        while (bookIterator.hasNext()){
                            Book book = bookIterator.next();
                            if (book.getBookId() == reviewedBook.getBookId()){
                                books.remove(books.indexOf(book));
                                break;
                            }
                        }
                        booksView.setBooksNotReviewed(books);
                    });
                    booksDb.disconnect();
                } else {
                    Platform.runLater(() -> booksView.showAlertAndWait("Fill in all fields!", WARNING));
                }
            } catch (Exception e) {
                Platform.runLater(() -> booksView.showAlertAndWait("Database error", ERROR));
            }
        }).start();
    }

    protected void getBooksNotReviewed(String username) {
        new Thread(() -> {
            try {
                //booksDb.connect(DB_NAME);
                final List<Book> books = booksDb.getBooksNotReviewed(username);
                Platform.runLater(() -> booksView.setBooksNotReviewed(books));
                booksDb.disconnect();
            } catch (Exception e) {
                Platform.runLater(() -> booksView.showAlertAndWait("Database error", ERROR));
            }
        }).start();
    }

    // TODO:
    // Add methods for all types of user interaction (e.g. via  menus).
}

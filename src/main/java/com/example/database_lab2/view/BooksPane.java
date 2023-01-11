package com.example.database_lab2.view;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.example.database_lab2.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;

/**
 * The main pane for the view, extending VBox and including the menus. An
 * internal BorderPane holds the TableView for books and a search utility.
 *
 * @author anderslm@kth.se
 */
public class BooksPane extends VBox {
    private TableView<Book> booksTable;
    private ObservableList<Book> booksInTable; // the data backing the table view
    private ComboBox<SearchMode> searchModeBox;
    private ComboBox<Genre> genreComboBox;
    private ComboBox<Book> booksComboBox;
    private TextField searchField;
    private Button searchButton;
    private Button loginBtn = new Button();
    private Button signUpBtn = new Button();
    private Label usernameLbl = new Label();
    private MenuBar menuBar;
    private User currentUser = null;
    private List<Book> books;
    private List<Book> booksNotReviewed;
    private Stage bookViewPopup;
    private Stage initLoginPopupwindow;
    private Stage initRegisterPopupWindow;
    private Stage initAddBookPopup;
    private Stage initRemoveBookPopup;
    private Stage initUpdateBookPopup;
    private Stage initReviewBookPopup;

    public BooksPane(BooksDbImpl booksDb) throws BooksDbException {
        final Controller controller = new Controller(booksDb, this);
        this.init(controller);
    }

    /**
     * Display a new set of books, e.g. from a database select, in the
     * booksTable table view.
     *
     * @param books the books to display
     */
    public void displayBooks(List<Book> books) {
        booksInTable.clear();
        booksInTable.addAll(books);
    }

    /**
     * Notify user on input error or exceptions.
     *
     * @param msg the message
     * @param type types: INFORMATION, WARNING et c.
     */
    protected void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void init(Controller controller) {

        booksInTable = FXCollections.observableArrayList();

        // init views and event handlers
        controller.getAllBooks();
        initBooksTable();
        initSearchView(controller);
        initUserView(controller);
        initMenus(controller);

        FlowPane topPane = new FlowPane();
        topPane.setHgap(10);
        topPane.setPadding(new Insets(10, 10, 10, 10));
        usernameLbl.setAlignment(Pos.CENTER_RIGHT);
        topPane.getChildren().addAll(loginBtn, signUpBtn, usernameLbl);

        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(searchModeBox, searchField, searchButton);

        BorderPane mainPane = new BorderPane();
        mainPane.setTop(topPane);
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    private void initBooksTable() {
        booksTable = new TableView<>();

        booksTable.setRowFactory( tv -> {
            TableRow<Book> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Book bookClicked = row.getItem();
                    initBookView(bookClicked);
                }
            });
            return row;
        });

        booksTable.setEditable(false); // don't allow user updates (yet)
        booksTable.setPlaceholder(new Label("No rows to display"));

        // define columns
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        TableColumn<Book, Date> publishedCol = new TableColumn<>("Published");
        TableColumn<Book, String> authorsCol = new TableColumn<>("Author(s)");
        booksTable.getColumns().addAll(titleCol, isbnCol, publishedCol, authorsCol);
        // give title column some extra space
        titleCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.5));
        authorsCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.5));

        // define how to fill data for each cell,
        // get values from Book properties
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        publishedCol.setCellValueFactory(new PropertyValueFactory<>("published"));
        authorsCol.setCellValueFactory(new PropertyValueFactory<>("authors"));

        // associate the table view with the data
        booksTable.setItems(booksInTable);
    }

    private void initBookView(Book book) {
        if(bookViewPopup == null) {
            bookViewPopup = new Stage();
            bookViewPopup.initModality(Modality.APPLICATION_MODAL);
            bookViewPopup.setTitle(book.getTitle());
        }

        Label titleLbl = new Label("Title: " + book.getTitle());
        VBox titleVbox = new VBox();
        titleVbox.getChildren().addAll(titleLbl);
        titleVbox.setAlignment(Pos.CENTER);

        Label isbnLbl = new Label("ISBN: " + book.getIsbn());
        VBox isbnVbox = new VBox();
        isbnVbox.getChildren().addAll(isbnLbl);
        isbnVbox.setAlignment(Pos.CENTER);

        Label publishedLbl = new Label("Published: " + book.getPublished());
        VBox publishedVbox = new VBox();
        publishedVbox.getChildren().addAll(publishedLbl);
        publishedVbox.setAlignment(Pos.CENTER);

        Label authorsLbl = new Label("Authors: ");
        VBox authorsVbox = new VBox();
        authorsVbox.getChildren().addAll(authorsLbl);
        for (Author author: book.getAuthors()) {
            authorsVbox.getChildren().addAll(new Label(author.getName()));
        }
        authorsVbox.setAlignment(Pos.CENTER);

        Label genresLbl = new Label("Genres: ");
        VBox genresVbox = new VBox();
        genresVbox.getChildren().addAll(genresLbl);
        for (Genre genre: book.getGenres()) {
            genresVbox.getChildren().addAll(new Label(genre.name()));
        }
        genresVbox.setAlignment(Pos.CENTER);

        Label reviewsLbl = new Label("Reviews: ");
        VBox reviewsVbox = new VBox();
        reviewsVbox.getChildren().addAll(reviewsLbl);
        for (Review review: book.getReviews()) {
            HBox reviewHbox = new HBox();
            Label reviewerTextLbl = new Label(review.getReviewText());
            Label reviewerDateLbl = new Label(review.getReviewDate().toString());
            Rating reviewerRating = new Rating(5, review.getStars());
            reviewerRating.setDisable(true);
            reviewHbox.setSpacing(10);
            reviewHbox.getChildren().addAll(reviewerTextLbl, reviewerRating, reviewerDateLbl);
            reviewHbox.setAlignment(Pos.CENTER);
            reviewsVbox.getChildren().addAll(reviewHbox);
        }


        reviewsVbox.setAlignment(Pos.CENTER);

        VBox layout= new VBox(10);

        layout.getChildren().addAll(titleVbox, isbnVbox, publishedVbox, authorsVbox, genresVbox, reviewsVbox);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(40);
        Scene bookViewScene= new Scene(layout, 600, 600);
        bookViewPopup.setScene(bookViewScene);
        bookViewPopup.showAndWait();

    }

    private void initSearchView(Controller controller) {
        searchField = new TextField();
        searchField.setPromptText("Search for...");
        searchModeBox = new ComboBox<>();
        searchModeBox.getItems().addAll(SearchMode.values());
        searchModeBox.setValue(SearchMode.Title);
        searchButton = new Button("Search");

        // event handling (dispatch to controller)
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String searchFor = searchField.getText();
                SearchMode mode = searchModeBox.getValue();
                controller.onSearchSelected(searchFor, mode);
            }
        });
    }

    public void initUserView(Controller controller) {
        loginBtn.setText("Login");
        signUpBtn.setText("Sign up");

        loginBtn.setOnAction(e -> {
            initLoginUserView(controller);
        });

        signUpBtn.setOnAction(e -> {
            initRegisterUserView(controller);
        });
    }
    private void initLoginUserView(Controller controller) {
        if(initLoginPopupwindow == null) {
            initLoginPopupwindow = new Stage();
            initLoginPopupwindow.initModality(Modality.APPLICATION_MODAL);
            initLoginPopupwindow.setTitle("Login");
        }

        Label usernameLbl = new Label("Username:");
        TextField usernameField = new TextField();
        VBox usernameVbox = new VBox();
        usernameVbox.getChildren().addAll(usernameLbl, usernameField);
        usernameVbox.setSpacing(10);

        Label passwordLbl = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        VBox passwordVbox = new VBox();
        passwordVbox.getChildren().addAll(passwordLbl, passwordField);
        passwordVbox.setSpacing(10);

        Button login= new Button("Login");

        login.setOnAction(e -> {
            controller.onLoginUser(usernameField.getText(), passwordField.getText());
        });

        VBox layout= new VBox(10);

        layout.getChildren().addAll(usernameVbox, passwordVbox, login);
        layout.setAlignment(Pos.CENTER);
        Scene loginScene= new Scene(layout, 500, 350);
        initLoginPopupwindow.setScene(loginScene);
        initLoginPopupwindow.showAndWait();
    }

    private void initRegisterUserView(Controller controller) {
        if(initRegisterPopupWindow == null) {
            initRegisterPopupWindow = new Stage();
            initRegisterPopupWindow.initModality(Modality.APPLICATION_MODAL);
            initRegisterPopupWindow.setTitle("Sign up");
        }

        Label nameLbl = new Label("Name:");
        TextField nameField = new TextField();
        VBox nameVbox = new VBox();
        nameVbox.getChildren().addAll(nameLbl, nameField);
        nameVbox.setSpacing(10);

        Label usernameLbl = new Label("Username:");
        TextField usernameField = new TextField();
        VBox usernameVbox = new VBox();
        usernameVbox.getChildren().addAll(usernameLbl, usernameField);
        usernameVbox.setSpacing(10);

        Label passwordLbl = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        VBox passwordVbox = new VBox();
        passwordVbox.getChildren().addAll(passwordLbl, passwordField);
        passwordVbox.setSpacing(10);

        Button register= new Button("Register");

        register.setOnAction(e -> {
            controller.onRegisterUser(nameField.getText(), usernameField.getText(), passwordField.getText());
        });

        VBox layout= new VBox(10);

        layout.getChildren().addAll(nameVbox, usernameVbox, passwordVbox, register);
        layout.setAlignment(Pos.CENTER);
        Scene signupScene= new Scene(layout, 500, 350);
        initRegisterPopupWindow.setScene(signupScene);
        initRegisterPopupWindow.showAndWait();
    }

    private void initAddBookPopup(Controller controller){
        if(initAddBookPopup == null) {
            initAddBookPopup = new Stage();
            initAddBookPopup.initModality(Modality.APPLICATION_MODAL);
            initAddBookPopup.setTitle("Add book");
        }

        Label titleLbl = new Label("Title:");
        TextField titleField = new TextField();
        VBox titleVbox = new VBox();
        titleVbox.getChildren().addAll(titleLbl, titleField);
        titleVbox.setSpacing(10);

        Label isbnLbl = new Label("ISBN:");
        TextField isbnField = new TextField();
        VBox isbnVbox = new VBox();
        isbnVbox.getChildren().addAll(isbnLbl, isbnField);
        isbnVbox.setSpacing(10);

        Label publishedLbl = new Label("Published:");
        DatePicker publishedField = new DatePicker();
        VBox publishedVbox = new VBox();
        publishedVbox.getChildren().addAll(publishedLbl, publishedField);
        publishedVbox.setSpacing(10);

        Label authorLbl = new Label("Author:");
        TextField authorField = new TextField();
        VBox authorVbox = new VBox();
        authorVbox.getChildren().addAll(authorLbl, authorField);
        authorVbox.setSpacing(10);

        Label genreLbl = new Label("Genre:");
        genreComboBox = new ComboBox<>();
        genreComboBox.getItems().addAll(Genre.values());
        VBox genreVbox = new VBox();
        genreVbox.getChildren().addAll(genreLbl, genreComboBox);
        genreVbox.setSpacing(10);

        Button addBookBtn= new Button("Add book");

        addBookBtn.setOnAction(e -> {
            controller.onAddBook(titleField.getText(), isbnField.getText(), publishedField.getValue(), authorField.getText(), genreComboBox.getValue().toString());
            initAddBookPopup.close();
        });

        VBox layout= new VBox(10);

        layout.getChildren().addAll(titleVbox, isbnVbox, publishedVbox, authorVbox, genreVbox, addBookBtn);
        layout.setAlignment(Pos.CENTER);
        Scene addBookScene= new Scene(layout, 600, 350);
        initAddBookPopup.setScene(addBookScene);
        initAddBookPopup.showAndWait();
    }

    private void initRemoveBookPopup(Controller controller){
        if (initRemoveBookPopup == null) {
            initRemoveBookPopup = new Stage();
            initRemoveBookPopup.initModality(Modality.APPLICATION_MODAL);
            initRemoveBookPopup.setTitle("Remove book");
        }

        Label booksLbl = new Label("Choose a book to delete:");
        booksComboBox = new ComboBox<>();
        booksComboBox.getItems().addAll(books);
        VBox booksVbox = new VBox();
        booksVbox.getChildren().addAll(booksLbl, booksComboBox);
        booksVbox.setSpacing(10);
        booksVbox.setAlignment(Pos.CENTER);

        Button removeBookBtn= new Button("Remove book");

        removeBookBtn.setOnAction(e -> {
            controller.onRemoveBook(booksComboBox.getValue().getIsbn());
            initRemoveBookPopup.close();
        });

        VBox layout= new VBox(10);

        layout.getChildren().addAll(booksVbox,removeBookBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(40);
        Scene removeBookScene= new Scene(layout, 600, 350);
        initRemoveBookPopup.setScene(removeBookScene);
        initRemoveBookPopup.showAndWait();
    }

    private void initUpdateBookPopup(Controller controller){
        if (initUpdateBookPopup == null) {
            initUpdateBookPopup = new Stage();
            initUpdateBookPopup.initModality(Modality.APPLICATION_MODAL);
            initUpdateBookPopup.setTitle("Update book");
        }

        Label booksLbl = new Label("Choose a book to update:");
        booksComboBox = new ComboBox<>();
        booksComboBox.getItems().addAll(books);
        VBox booksVbox = new VBox();
        booksVbox.getChildren().addAll(booksLbl, booksComboBox);
        booksVbox.setSpacing(10);
        booksVbox.setAlignment(Pos.CENTER);

        Label newTitleLbl = new Label("New Title:");
        TextField newTitleField = new TextField();
        VBox newTitleVbox = new VBox();
        newTitleVbox.getChildren().addAll(newTitleLbl, newTitleField);
        newTitleVbox.setSpacing(10);

        Label authorLbl = new Label("Add Author");
        TextField authorField = new TextField ();
        VBox authorVbox = new VBox();
        authorVbox.getChildren().addAll(authorLbl, authorField);
        authorVbox.setSpacing(10);

        Label genreLbl = new Label("Add Genre:");
        genreComboBox = new ComboBox<>();
        genreComboBox.getItems().addAll(Genre.values());
        VBox genreVbox = new VBox();
        genreVbox.getChildren().addAll(genreLbl, genreComboBox);
        genreVbox.setSpacing(10);

        Button updateBookBtn= new Button("Update book");

        updateBookBtn.setOnAction(e -> {
            controller.onUpdateBook(booksComboBox.getValue().getIsbn(),newTitleField.getText(),authorField.getText(),genreComboBox.getValue() == null ? null:genreComboBox.getValue().toString());
            initUpdateBookPopup.close();
        });

        VBox layout= new VBox(10);

        layout.getChildren().addAll(booksVbox,newTitleVbox,authorVbox,genreVbox,updateBookBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(40);
        Scene updateBookScene= new Scene(layout, 600, 350);
        initUpdateBookPopup.setScene(updateBookScene);
        initUpdateBookPopup.showAndWait();
    }


    private void initMenus(Controller controller) {

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        MenuItem connectItem = new MenuItem("Connect to Db");
        MenuItem disconnectItem = new MenuItem("Disconnect");
        fileMenu.getItems().addAll(exitItem, connectItem, disconnectItem);

        Menu searchMenu = new Menu("Search");
        MenuItem titleItem = new MenuItem("Title");
        MenuItem isbnItem = new MenuItem("ISBN");
        MenuItem authorItem = new MenuItem("Author");
        searchMenu.getItems().addAll(titleItem, isbnItem, authorItem);

        Menu manageMenu = new Menu("Manage");
        MenuItem addItem = new MenuItem("Add");
        MenuItem removeItem = new MenuItem("Remove");
        MenuItem updateItem = new MenuItem("Update");
        MenuItem reviewItem = new MenuItem("Review");
        manageMenu.getItems().addAll(addItem, removeItem, updateItem, reviewItem);

        EventHandler<ActionEvent> addBookHandler = actionEvent -> {
            if (currentUser != null) {
                initAddBookPopup(controller); // save data?
            }else{
                showAlertAndWait("You need to login first!", Alert.AlertType.WARNING);
            }
        };
        addItem.addEventHandler(ActionEvent.ACTION, addBookHandler);

        EventHandler<ActionEvent> removeBookHandler = actionEvent -> {
            if (currentUser != null) {
                initRemoveBookPopup(controller); // save data?
            }else{
                showAlertAndWait("You need to login first!", Alert.AlertType.WARNING);
            }// save data?
        };
        removeItem.addEventHandler(ActionEvent.ACTION, removeBookHandler);

        EventHandler<ActionEvent> updateBookHandler = actionEvent -> {
            if (currentUser != null) {
                initUpdateBookPopup(controller); // save data?
            }else{
                showAlertAndWait("You need to login first!", Alert.AlertType.WARNING);
            }
        };
        updateItem.addEventHandler(ActionEvent.ACTION, updateBookHandler);

        EventHandler<ActionEvent> reviewBookHandler = actionEvent -> {
            if (currentUser != null) {
                initReviewBookPopup(controller); // save data?
            }else{
                showAlertAndWait("You need to login first!", Alert.AlertType.WARNING);
            }// save data?
        };
        reviewItem.addEventHandler(ActionEvent.ACTION, reviewBookHandler);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, searchMenu, manageMenu);
    }

    private void initReviewBookPopup(Controller controller) {
        if (initReviewBookPopup == null) {
            initReviewBookPopup = new Stage();
            initReviewBookPopup.initModality(Modality.APPLICATION_MODAL);
            initReviewBookPopup.setTitle("Remove book");
        }

        Label booksLbl = new Label("Choose a book to review:");
        ComboBox<Book> booksNotReviewedComboBox = new ComboBox<>();
        booksNotReviewedComboBox.getItems().addAll(booksNotReviewed);
        VBox booksVbox = new VBox();
        booksVbox.getChildren().addAll(booksLbl, booksNotReviewedComboBox);
        booksVbox.setSpacing(10);
        booksVbox.setAlignment(Pos.CENTER);

        Label reviewLbl = new Label("Review:");
        TextArea reviewTextArea = new TextArea();
        VBox reviewTextVbox = new VBox();
        reviewTextVbox.getChildren().addAll(reviewLbl, reviewTextArea);
        reviewTextVbox.setSpacing(10);
        reviewTextVbox.setAlignment(Pos.CENTER);

        Label starsLbl = new Label("Stars:");
        Rating rating = new Rating();
        VBox ratingVbox = new VBox();
        ratingVbox.getChildren().addAll(starsLbl, rating);
        ratingVbox.setSpacing(10);
        ratingVbox.setAlignment(Pos.CENTER);

        Button reviewBookBtn= new Button("Submit review");

        reviewBookBtn.setOnAction(e -> {
            controller.onReviewBook(booksNotReviewedComboBox.getValue().getIsbn(), this.currentUser.getUsername(), reviewTextArea.getText(), rating.getRating());
            initReviewBookPopup.close();
        });

        VBox layout= new VBox(10);

        layout.getChildren().addAll(booksVbox, reviewTextVbox, ratingVbox, reviewBookBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(40);
        Scene reviewBookScene = new Scene(layout, 600, 350);
        initReviewBookPopup.setScene(reviewBookScene);
        initReviewBookPopup.showAndWait();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<Book> getBooksNotReviewed() {
        return booksNotReviewed;
    }

    public void setBooksNotReviewed(List<Book> booksNotReviewed) {
        this.booksNotReviewed = booksNotReviewed;
    }

    public Stage getInitLoginPopupwindow() {
        return initLoginPopupwindow;
    }

    public Stage getInitRegisterPopupWindow() {
        return initRegisterPopupWindow;
    }
    public Label getUsernameLbl() {
        return usernameLbl;
    }

    public Button getLoginBtn() {
        return loginBtn;
    }

    public Button getSignUpBtn() {
        return signUpBtn;
    }
}

package com.example.database_lab1.view;

import java.sql.Date;
import java.util.List;

import com.example.database_lab1.model.*;
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
    private ComboBox<Author> authorsComboBox;
    private TextField searchField;
    private Button searchButton;
    private Button loginBtn = new Button();
    private Button signUpBtn = new Button();

    private Label usernameLbl = new Label();
    private MenuBar menuBar;

    private User currentUser = null;

    public BooksPane(BooksDbImpl booksDb) {
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

    private void initUserView(Controller controller) {
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
        Stage popupwindow=new Stage();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Login");

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
            this.currentUser = controller.onLoginUser(usernameField.getText(), passwordField.getText());
            if (this.currentUser != null){
                popupwindow.close();
                this.usernameLbl.setText("Hello " + usernameField.getText());
                loginBtn.setText("Log out");
                loginBtn.setOnAction(l -> {
                    this.currentUser = null;
                    this.usernameLbl.setText("");
                    signUpBtn.setDisable(false);
                    initUserView(controller);
                });
                signUpBtn.setDisable(true);

            }else{
                showAlertAndWait("Wrong username or password, try again", Alert.AlertType.WARNING);
            }
        });

        VBox layout= new VBox(10);

        layout.getChildren().addAll(usernameVbox, passwordVbox, login);
        layout.setAlignment(Pos.CENTER);
        Scene loginScene= new Scene(layout, 500, 350);
        popupwindow.setScene(loginScene);
        popupwindow.showAndWait();
    }
    private void initRegisterUserView(Controller controller) {
        Stage popupwindow=new Stage();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Sign up");

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
            if (controller.onRegisterUser(nameField.getText(), usernameField.getText(), passwordField.getText())){
                popupwindow.close();
            }else{
                showAlertAndWait("User already exists, try another username", Alert.AlertType.WARNING);
            }
        });

        VBox layout= new VBox(10);

        layout.getChildren().addAll(nameVbox, usernameVbox, passwordVbox, register);
        layout.setAlignment(Pos.CENTER);
        Scene signupScene= new Scene(layout, 500, 350);
        popupwindow.setScene(signupScene);
        popupwindow.showAndWait();
    }

    private void initAddBookPopup(Controller controller){
        Stage popupwindow=new Stage();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Add book");

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
            popupwindow.close();
        });

        VBox layout= new VBox(10);

        layout.getChildren().addAll(titleVbox, isbnVbox, publishedVbox, authorVbox, genreVbox, addBookBtn);
        layout.setAlignment(Pos.CENTER);
        Scene addBookScene= new Scene(layout, 600, 350);
        popupwindow.setScene(addBookScene);
        popupwindow.showAndWait();
    }

    private void initRemoveBookPopup(Controller controller){
        Stage popupwindow=new Stage();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Remove book");

        Label booksLbl = new Label("Choose a book to delete:");
        booksComboBox = new ComboBox<>();
        booksComboBox.getItems().addAll(controller.getAllBooks());
        VBox booksVbox = new VBox();
        booksVbox.getChildren().addAll(booksLbl, booksComboBox);
        booksVbox.setSpacing(10);
        booksVbox.setAlignment(Pos.CENTER);

        Button removeBookBtn= new Button("Remove book");

        removeBookBtn.setOnAction(e -> {
            controller.onRemoveBook(booksComboBox.getValue().getBookId());
            popupwindow.close();
        });

        VBox layout= new VBox(10);

        layout.getChildren().addAll(booksVbox,removeBookBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(40);
        Scene addBookScene= new Scene(layout, 600, 350);
        popupwindow.setScene(addBookScene);
        popupwindow.showAndWait();
    }

    private void initUpdateBookPopup(Controller controller){
        Stage popupwindow=new Stage();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Update book");

        Label booksLbl = new Label("Choose a book to update:");
        booksComboBox = new ComboBox<>();
        booksComboBox.getItems().addAll(controller.getAllBooks());
        VBox booksVbox = new VBox();
        booksVbox.getChildren().addAll(booksLbl, booksComboBox);
        booksVbox.setSpacing(10);
        booksVbox.setAlignment(Pos.CENTER);

        Button updateBookBtn= new Button("Update book");
/*
        updateBookBtn.setOnAction(e -> {
            controller.onUpdateBook(booksComboBox.getValue().getBookId());
            popupwindow.close();
        });
*/
        VBox layout= new VBox(10);

        layout.getChildren().addAll(booksVbox,updateBookBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(40);
        Scene addBookScene= new Scene(layout, 600, 350);
        popupwindow.setScene(addBookScene);
        popupwindow.showAndWait();

        /*Stage popupwindow=new Stage();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Update book");

        Label booksLbl = new Label("Choose a book to update:");
        booksComboBox = new ComboBox<>();
        booksComboBox.getItems().addAll(controller.getAllBooks());
        VBox booksVbox = new VBox();
        booksVbox.getChildren().addAll(booksLbl, booksComboBox);
        booksVbox.setSpacing(10);
        booksVbox.setAlignment(Pos.CENTER);

        Label titleLbl = new Label("Title:");
        TextField titleField = new TextField ();
        VBox titleVbox = new VBox();
        titleVbox.getChildren().addAll(titleLbl, titleField);
        titleVbox.setSpacing(10);

        Label isbnLbl = new Label("ISBN:");
        TextField isbnField = new TextField ();
        VBox isbnVbox = new VBox();
        isbnVbox.getChildren().addAll(isbnLbl, isbnField);
        isbnVbox.setSpacing(10);

        Label newTitleLbl = new Label("New Title:");
        TextField newTitleField = new TextField ();
        VBox newTitleVbox = new VBox();
        titleVbox.getChildren().addAll(newTitleLbl, newTitleField);
        titleVbox.setSpacing(10);

        Button updateBookBtn= new Button("Update book");

        updateBookBtn.setOnAction(e -> {
            controller.onUpdateBook(titleField.getText(), isbnField.getText(),newTitleField.getText());
            popupwindow.close();
        });

        VBox layout= new VBox(10);

        layout.getChildren().addAll(titleVbox,isbnVbox,newTitleVbox,updateBookBtn);
        layout.setAlignment(Pos.CENTER);
        Scene addBookScene= new Scene(layout, 600, 350);
        popupwindow.setScene(addBookScene);
        popupwindow.showAndWait();*/
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
            initUpdateBookPopup(controller); // save data?
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
        Stage popupwindow=new Stage();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Remove book");

        Label booksLbl = new Label("Choose a book to review:");
        ComboBox<Book> booksNotReviewedComboBox = new ComboBox<>();
        booksNotReviewedComboBox.getItems().addAll(controller.getBooksNotReviewed(this.currentUser.getUserId()));
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
            controller.onReviewBook(booksNotReviewedComboBox.getValue().getBookId(), this.currentUser.getUserId(), reviewTextArea.getText(), rating.getRating());
            popupwindow.close();
        });

        VBox layout= new VBox(10);

        layout.getChildren().addAll(booksVbox, reviewTextVbox, ratingVbox, reviewBookBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(40);
        Scene addBookScene= new Scene(layout, 600, 350);
        popupwindow.setScene(addBookScene);
        popupwindow.showAndWait();
    }
}

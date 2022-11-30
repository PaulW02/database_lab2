module se.kth.anderslm.booksdb {
    requires javafx.controls;
    requires javafx.base;

    opens com.example.database_lab1 to javafx.base;
    opens com.example.database_lab1.model to javafx.base; // open model package for reflection from PropertyValuesFactory (sigh ...)
    exports com.example.database_lab1;

    requires java.sql;
    requires mysql.connector.j;
}
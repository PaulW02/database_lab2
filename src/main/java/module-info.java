module se.kth.anderslm.booksdb {
    requires javafx.controls;
    requires javafx.base;

    opens com.example.database_lab2 to javafx.base;
    opens com.example.database_lab2.model to javafx.base; // open model package for reflection from PropertyValuesFactory (sigh ...)
    exports com.example.database_lab2;

    requires java.sql;
    requires org.controlsfx.controls;
    requires mongo.java.driver;
}
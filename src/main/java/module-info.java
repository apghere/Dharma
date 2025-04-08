module com.dharmaapp.hellojavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.sql;

    exports com.dharmaapp.hellojavafx;
    opens com.dharmaapp.hellojavafx to javafx.fxml, javafx.graphics;
}
module org.nicholas.guicardealershipsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.nicholas.guicardealershipsystem to javafx.fxml;
    exports org.nicholas.guicardealershipsystem;
}
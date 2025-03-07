module org.example.galleryapp {
    requires javafx.controls;
    requires javafx.fxml;


    requires org.controlsfx.controls;
    requires java.sql;


    opens org.example.galleryapp to javafx.fxml;
    exports org.example.galleryapp;

    opens Model to javafx.base;

    exports View to javafx.graphics;

    opens View to javafx.fxml;
}
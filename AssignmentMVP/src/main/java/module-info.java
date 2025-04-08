module org.example.galleryapp {
    requires javafx.controls;
    requires javafx.fxml;


    requires org.controlsfx.controls;
    requires java.sql;
    requires java.desktop;


    opens org.example.galleryapp to javafx.fxml;
    exports org.example.galleryapp;

    exports View to javafx.graphics, javafx.fxml;

    opens Model to javafx.base;

    opens View to javafx.fxml;

    opens Presenter to javafx.fxml;

}
module org.example.assignmentmvc {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;

    opens org.example.assignmentmvc to javafx.fxml;
    exports org.example.assignmentmvc;

    exports View;
    opens View to javafx.fxml;
}
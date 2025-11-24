module com.github.marlon2132.studentsorttask {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.github.marlon2132.studentsorttask to javafx.fxml;
    exports com.github.marlon2132.studentsorttask;
}
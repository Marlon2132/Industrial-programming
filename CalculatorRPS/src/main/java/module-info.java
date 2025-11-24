module com.github.marlon2132.calculatorrps {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.github.marlon2132.calculatorrps to javafx.fxml;
    opens com.github.marlon2132.calculatorrps.parser to javafx.fxml; // не обязательно
    exports com.github.marlon2132.calculatorrps;
}

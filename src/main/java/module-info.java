module Project.Managment.System {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires java.logging;

    opens com.COMP1815.java to javafx.fxml, javafx.controls;
    opens com.COMP1815.java.controllers to javafx.fxml, javafx.controls;
    opens com.COMP1815.kotlin.model to kotlin.stdlib;

    exports com.COMP1815.java;
    exports com.COMP1815.kotlin.model;
    exports com.COMP1815.kotlin.data;
}
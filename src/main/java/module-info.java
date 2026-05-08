module com.polymarket {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens com.polymarket to javafx.fxml;
    opens com.polymarket.ui to javafx.fxml;
    opens com.polymarket.ui.auth to javafx.fxml;
    exports com.polymarket;
    exports com.polymarket.ui;
    exports com.polymarket.ui.auth;
}

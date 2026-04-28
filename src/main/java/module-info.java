module com.polymarket {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens com.polymarket to javafx.fxml;
    exports com.polymarket;
}

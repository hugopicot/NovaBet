module com.polymarket {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens com.polymarket to javafx.fxml;
    exports com.polymarket;
    exports com.polymarket.domain.model;
    exports com.polymarket.domain.exception;
    exports com.polymarket.domain.port;
    exports com.polymarket.domain.service;
    exports com.polymarket.infrastructure.db;
}

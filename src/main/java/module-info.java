module com.polymarket {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens com.polymarket to javafx.fxml;
    exports com.polymarket;
    exports com.polymarket.model;
    exports com.polymarket.dao;
    exports com.polymarket.controller;
    exports com.polymarket.service;
    exports com.polymarket.util;
    exports com.polymarket.domain.exception;
    exports com.polymarket.domain.service;
    exports com.polymarket.domain.dto;
}

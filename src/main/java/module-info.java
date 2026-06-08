module com.polymarket {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires jbcrypt;
    requires stripe.java;
    requires java.desktop;
    requires java.net.http;
    requires com.google.gson;

    opens com.polymarket to javafx.fxml;
    opens com.polymarket.ui to javafx.fxml;
    opens com.polymarket.ui.auth to javafx.fxml;
    opens com.polymarket.oto to javafx.fxml;
    opens com.polymarket.app.controllers to javafx.fxml;
    opens com.polymarket.casino.lobby to javafx.fxml;
    exports com.polymarket.oto;
    exports com.polymarket.casino.lobby;
    exports com.polymarket;
    exports com.polymarket.model;
    exports com.polymarket.dao;
    exports com.polymarket.controller;
    exports com.polymarket.service;
    exports com.polymarket.util;
    exports com.polymarket.domain.exception;
    exports com.polymarket.domain.service;
    exports com.polymarket.domain.dto;
    exports com.polymarket.ui;
    exports com.polymarket.ui.auth;
    exports com.polymarket.app.controllers;
    exports com.polymarket.app.services;
    exports com.polymarket.infrastructure;
    exports com.polymarket.infrastructure.polymarket;
    exports com.polymarket.infrastructure.polymarket.model;
    opens com.polymarket.domain.service to javafx.base;
    opens com.polymarket.infrastructure.polymarket.model to com.google.gson;
}

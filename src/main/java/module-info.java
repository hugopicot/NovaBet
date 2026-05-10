module com.novabet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires jbcrypt;

    opens com.novabet to javafx.fxml;
    exports com.novabet;
    
    opens com.novabet.app.controllers to javafx.fxml;
    exports com.novabet.app.controllers;
}

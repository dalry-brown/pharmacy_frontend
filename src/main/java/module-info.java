module com.example.demo3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;  // Add this line
    requires com.fasterxml.jackson.core;      // Add this line
    requires com.fasterxml.jackson.annotation; // Add this line

    opens com.example.demo3 to javafx.fxml, com.google.gson, com.fasterxml.jackson.databind; // Add com.fasterxml.jackson.databind

    exports com.example.demo3;
}


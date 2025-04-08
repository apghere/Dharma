package com.dharmaapp.hellojavafx;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;

public class AuthenticationDialog extends Dialog<Boolean> {
    private final String VALID_USERNAME = "admin";
    private final String VALID_PASSWORD = "dharma2025";

    public AuthenticationDialog() {
        setTitle("Dharma - Login");
        setHeaderText("🔒 Secure Login to Access Dharma Rituals App");
        initModality(Modality.APPLICATION_MODAL);

        //  Stylish Grid Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(40, 40, 40, 40));  // Larger padding
        grid.setVgap(15);  // Increased vertical gap
        grid.setHgap(15);  // Increased horizontal gap
        grid.setStyle("-fx-background-color: #f0f0f0;");  // Light background color

        //  Username field
        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(Font.font("Arial", 16));  // Larger font
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setFont(Font.font(14));  // Slightly larger input font
        usernameField.setPrefHeight(40);  // Taller input box
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        //  Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Arial", 16));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setFont(Font.font(14));
        passwordField.setPrefHeight(40);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);

        //  Login button
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        //  Style the dialog pane
        getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-border-color: #0078d7; -fx-border-width: 2;");
        getDialogPane().lookupButton(loginButtonType).setStyle(
                "-fx-background-color: #0078d7; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-pref-width: 100px; " +
                        "-fx-pref-height: 40px;"
        );

        getDialogPane().setContent(grid);

        //  Enable/disable login button based on input
        getDialogPane().lookupButton(loginButtonType).disableProperty().bind(
                usernameField.textProperty().isEmpty()
                        .or(passwordField.textProperty().isEmpty())
        );

        //  Convert result to boolean on login attempt
        setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return authenticate(usernameField.getText(), passwordField.getText());
            }
            return false;
        });
    }

    //  Authentication logic
    private boolean authenticate(String username, String password) {
        return VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password);
    }
}

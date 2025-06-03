package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import models.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ProfileController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    
    private String currentUsername;
    private Stage stage;

    public void initializeData(String username, Stage stage) {
        this.currentUsername = username;
        this.stage = stage;
        usernameField.setText(username);
    }

    @FXML
    private void handleSaveProfile() {
        String newPassword = passwordField.getText();
        String confirmedPassword = confirmPasswordField.getText();
        
        if (!newPassword.equals(confirmedPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas");
            return;
        }
        
        if (updatePasswordInDatabase(newPassword)) {
            showAlert("Succès", "Mot de passe mis à jour avec succès");
            stage.close();
        }
    }

    private boolean updatePasswordInDatabase(String newPassword) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE admin SET password = ? WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, newPassword); // Dans la pratique, vous devriez hasher le mot de passe
            stmt.setString(2, currentUsername);
            
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la mise à jour: " + e.getMessage());
            return false;
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
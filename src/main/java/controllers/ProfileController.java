package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.application.Platform;
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
        
        // Focus sur le champ mot de passe à l'ouverture
        Platform.runLater(() -> passwordField.requestFocus());
    }

    @FXML
    private void handleSaveProfile() {
        String newPassword = passwordField.getText().trim();
        String confirmedPassword = confirmPasswordField.getText().trim();
        
        // Validation
        if (newPassword.isEmpty()) {
            showAlert("Erreur", "Le mot de passe ne peut pas être vide", Alert.AlertType.ERROR);
            return;
        }
        
        if (!newPassword.equals(confirmedPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas", Alert.AlertType.ERROR);
            return;
        }
        
        if (newPassword.length() < 6) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 6 caractères", Alert.AlertType.ERROR);
            return;
        }
        
        // Mise à jour dans la base de données
        if (updatePasswordInDatabase(newPassword)) {
            showAlert("Succès", "Mot de passe mis à jour avec succès", Alert.AlertType.INFORMATION);
            stage.close(); // Fermer la fenêtre après succès
        }
    }

    private boolean updatePasswordInDatabase(String newPassword) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Dans une application réelle, vous devriez HASHER le mot de passe
            String query = "UPDATE admin SET password = ? WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, newPassword);
            stmt.setString(2, currentUsername);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            showAlert("Erreur", "Échec de la mise à jour: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
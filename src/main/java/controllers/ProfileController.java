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
        // Forçage à "admin" si valeur null
        this.currentUsername = (username != null) ? username : "admin";
        this.stage = stage;
        usernameField.setText(this.currentUsername);
        
        // Focus sur le champ mot de passe à l'ouverture
        Platform.runLater(() -> passwordField.requestFocus());
    }

    @FXML
    private void handleSaveProfile() {
        // Vérification finale du nom d'utilisateur
        if (currentUsername == null || currentUsername.isEmpty()) {
            currentUsername = "admin"; // Forçage final
        }
        
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
        } else {
            showAlert("Erreur", "Échec de la mise à jour du mot de passe", Alert.AlertType.ERROR);
        }
    }

    private boolean updatePasswordInDatabase(String newPassword) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                showAlert("Erreur", "Pas de connexion à la base de données", Alert.AlertType.ERROR);
                return false;
            }
            
            // Forçage final du nom d'utilisateur
            String usernameToUpdate = (currentUsername != null && !currentUsername.isEmpty()) 
                                    ? currentUsername : "admin";
            
            System.out.println("Tentative de mise à jour pour: " + usernameToUpdate);
            
            // REQUÊTE SIMPLIFIÉE AVEC UTILISATEUR FIXE
            String query = "UPDATE admin SET password = ? WHERE username = 'admin'";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, newPassword);
                
                int rowsAffected = stmt.executeUpdate();
                System.out.println("Lignes affectées: " + rowsAffected);
                
                if (rowsAffected == 0) {
                    showAlert("Erreur", "Aucun utilisateur admin trouvé", Alert.AlertType.ERROR);
                }
                
                return rowsAffected > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
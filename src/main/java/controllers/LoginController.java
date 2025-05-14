package controllers;


import models.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Rediriger vers le dashboard
                System.out.println("Connexion réussie !");
                // Charger dashboard.fxml ici
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
                javafx.scene.Parent root = loader.load();
                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setTitle("Dashboard");
                stage.setScene(new javafx.scene.Scene(root));
                stage.show();

                // Fermer la fenêtre de connexion
                javafx.stage.Stage loginStage = (javafx.stage.Stage) usernameField.getScene().getWindow();
                loginStage.close();
            } else {
                System.out.println("Identifiants incorrects.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

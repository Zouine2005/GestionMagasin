package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import models.DatabaseConnection;

public class AddProductController {
    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private TextField imagePathField;
    
    private ProductController productController;

    public void setProductController(ProductController productController) {
        this.productController = productController;
    }

    @FXML
    private void addProduct() {
        try {
            // Validation des données
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            String imagePath = imagePathField.getText().trim();

            if (name.isEmpty() || description.isEmpty()) {
                productController.showAlert("Erreur", "Tous les champs sont obligatoires");
                return;
            }

            // Insertion en base de données
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO product (name, description, price, quantity, image_path) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setDouble(3, price);
                stmt.setInt(4, quantity);
                stmt.setString(5, imagePath.isEmpty() ? null : imagePath);
                stmt.executeUpdate();
            }

            // Fermeture de la fenêtre
            nameField.getScene().getWindow().hide();
            productController.refreshTable();
            productController.showAlert("Succès", "Produit ajouté avec succès", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            productController.showAlert("Erreur", "Veuillez entrer des valeurs numériques valides pour le prix et la quantité");
        } catch (Exception e) {
            productController.showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    @FXML
    private void browseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("Tous fichiers", "*.*")
        );
        File file = fileChooser.showOpenDialog(imagePathField.getScene().getWindow());
        if (file != null) {
            imagePathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void cancel() {
        nameField.getScene().getWindow().hide();
    }
}
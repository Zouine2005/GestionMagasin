package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Product;
import java.io.File;

public class EditProductController {
    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private TextField imagePathField;
    
    private Product productToEdit;
    private ProductController mainController;

    public void setProductToEdit(Product product) {
        this.productToEdit = product;
        // Remplir les champs avec les données du produit
        nameField.setText(product.getName());
        descriptionField.setText(product.getDescription());
        priceField.setText(String.valueOf(product.getPrice()));
        quantityField.setText(String.valueOf(product.getQuantity()));
        imagePathField.setText(product.getImagePath());
    }

    public void setMainController(ProductController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleSave() {
        try {
            // Validation des données
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            String imagePath = imagePathField.getText().trim();

            if (name.isEmpty()) {
                mainController.showAlert("Erreur", "Le nom est obligatoire");
                return;
            }

            // Mise à jour du produit
            productToEdit.setName(name);
            productToEdit.setDescription(description);
            productToEdit.setPrice(price);
            productToEdit.setQuantity(quantity);
            productToEdit.setImagePath(imagePath);

            // Sauvegarde en base de données
            if (mainController.updateProductInDatabase(productToEdit)) {
                mainController.refreshTable();
                closeWindow();
                mainController.showAlert("Succès", "Produit mis à jour", Alert.AlertType.INFORMATION);
            }
        } catch (NumberFormatException e) {
            mainController.showAlert("Erreur", "Veuillez entrer des valeurs numériques valides pour le prix et la quantité");
        } catch (Exception e) {
            mainController.showAlert("Erreur", "Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    @FXML
    private void handleBrowseImage() {
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
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
}
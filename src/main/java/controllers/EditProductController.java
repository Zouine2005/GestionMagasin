package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Product;
import java.io.File;

public class EditProductController {
    // Champs FXML
    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private TextField imagePathField;
    
    // Données du produit
    private Product productToEdit;
    private ProductController mainController;
    private Stage currentStage;

    // Méthode d'initialisation sécurisée
    public void initializeData(Product product, ProductController mainController, Stage stage) {
        if (product == null || mainController == null || stage == null) {
            throw new IllegalArgumentException("Les paramètres ne peuvent pas être null");
        }
        
        this.productToEdit = product;
        this.mainController = mainController;
        this.currentStage = stage;
        
        // Vérification que les champs FXML sont injectés
        if (nameField == null || descriptionField == null || priceField == null || 
            quantityField == null || imagePathField == null) {
            throw new IllegalStateException("Les champs FXML n'ont pas été correctement initialisés");
        }
        
        // Remplissage des champs
        populateFields();
    }

    private void populateFields() {
        nameField.setText(productToEdit.getName());
        descriptionField.setText(productToEdit.getDescription());
        priceField.setText(String.format("%.2f", productToEdit.getPrice()));
        quantityField.setText(String.valueOf(productToEdit.getQuantity()));
        imagePathField.setText(productToEdit.getImagePath() != null ? productToEdit.getImagePath() : "");
    }

    @FXML
    private void handleSave() {
        try {
            if (!validateInputs()) {
                return;
            }

            updateProductFromFields();
            
            if (mainController.updateProductInDatabase(productToEdit)) {
                mainController.refreshTable();
                closeWindow();
                showSuccessAlert();
            }
        } catch (NumberFormatException e) {
            showErrorAlert("Format invalide", "Veuillez entrer des valeurs numériques valides");
        } catch (Exception e) {
            showErrorAlert("Erreur", "Échec de la mise à jour: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty()) {
            showErrorAlert("Erreur", "Le nom du produit est obligatoire");
            return false;
        }
        
        try {
            Double.parseDouble(priceField.getText().trim());
            Integer.parseInt(quantityField.getText().trim());
            return true;
        } catch (NumberFormatException e) {
            showErrorAlert("Erreur", "Prix et quantité doivent être des nombres valides");
            return false;
        }
    }

    private void updateProductFromFields() {
        productToEdit.setName(nameField.getText().trim());
        productToEdit.setDescription(descriptionField.getText().trim());
        productToEdit.setPrice(Double.parseDouble(priceField.getText().trim()));
        productToEdit.setQuantity(Integer.parseInt(quantityField.getText().trim()));
        productToEdit.setImagePath(imagePathField.getText().trim());
    }

    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        
        File file = fileChooser.showOpenDialog(currentStage);
        if (file != null) {
            imagePathField.setText(file.getAbsolutePath());
        }
    }

    private void configureFileChooser(FileChooser fileChooser) {
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
            new FileChooser.ExtensionFilter("Tous fichiers", "*.*")
        );
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        if (currentStage != null) {
            currentStage.close();
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(currentStage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(currentStage);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Produit mis à jour avec succès");
        alert.showAndWait();
    }
}
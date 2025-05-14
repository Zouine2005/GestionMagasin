package controllers;

import models.Product;
import models.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.File;

public class ProductController {
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, Integer> quantityColumn;
    @FXML private TableColumn<Product, String> imageColumn;
    @FXML private TextField searchField;
    
    // Champs du formulaire
    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private TextField imagePathField;
    
    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private Stage addProductStage;

    @FXML
    public void initialize() {
        // Configuration des colonnes
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

        loadProducts();
    }

    private void loadProducts() {
        productList.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM product";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                productList.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getString("image_path")
                ));
            }
            productTable.setItems(productList);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement: " + e.getMessage());
        }
    }

    @FXML
    private void showAddProductForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/add_product_form.fxml"));
            
            // Créez une nouvelle instance du contrôleur
            ProductController addProductController = new ProductController();
            
            // Configurez les données nécessaires
            addProductController.productList = this.productList;
            
            // Définissez le contrôleur
            loader.setController(addProductController);
            
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter un produit");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(productTable.getScene().getWindow());
            stage.showAndWait();
            
            // Rafraîchir après fermeture
            loadProducts();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addProduct() {
        try {
            // Validation
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            String priceText = priceField.getText().trim();
            String quantityText = quantityField.getText().trim();
            String imagePath = imagePathField.getText().trim();

            if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
                showAlert("Erreur", "Tous les champs sont obligatoires");
                return;
            }

            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            if (price <= 0 || quantity < 0) {
                showAlert("Erreur", "Prix et quantité doivent être positifs");
                return;
            }

            // Insertion BD
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

            // Rafraîchissement
            loadProducts();
            addProductStage.close();
            clearFormFields();
            showAlert("Succès", "Produit ajouté", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Prix et quantité doivent être numériques");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void browseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("Tous fichiers", "*.*")
        );
        File file = fileChooser.showOpenDialog(addProductStage);
        if (file != null) {
            imagePathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void cancelAddProduct() {
        addProductStage.close();
        clearFormFields();
    }

    private void clearFormFields() {
        nameField.clear();
        descriptionField.clear();
        priceField.clear();
        quantityField.clear();
        imagePathField.clear();
    }

    @FXML
    private void deleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Aucun produit sélectionné");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer " + selected.getName() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM product WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, selected.getId());
                stmt.executeUpdate();
                loadProducts();
                showAlert("Succès", "Produit supprimé", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur de suppression: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.ERROR);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProductController {
    // Composants de la vue principale
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, String> descriptionColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, Integer> quantityColumn;
    @FXML private TableColumn<Product, String> imageColumn;
    @FXML private TextField searchField;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configuration des colonnes de la table
        configureTableColumns();
        loadProducts();
    }

    private void configureTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
    }

    public void loadProducts() {
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
            showAlert("Erreur", "Erreur lors du chargement des produits: " + e.getMessage());
        }
    }

    @FXML
    private void showAddProductForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/add_product_form.fxml"));
            
            // Création d'une nouvelle instance du contrôleur dédié
            AddProductController addProductController = new AddProductController();
            addProductController.setProductController(this);
            
            loader.setController(addProductController);
            Parent root = loader.load();
            
            // Configuration de la fenêtre modale
            Stage stage = new Stage();
            stage.setTitle("Ajouter un produit");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(productTable.getScene().getWindow());
            stage.showAndWait();
            
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Erreur", "Veuillez sélectionner un produit à supprimer");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + selectedProduct.getName() + "?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM product WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, selectedProduct.getId());
                stmt.executeUpdate();
                loadProducts();
                showAlert("Succès", "Produit supprimé avec succès", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    public void refreshTable() {
        loadProducts();
    }

    public void showAlert(String title, String message) {
        showAlert(title, message, Alert.AlertType.ERROR);
    }

    public void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Ajoutez cette méthode dans ProductController
   @FXML
private void showEditProductForm() {
    Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
    if (selectedProduct == null) {
        showAlert("Erreur", "Veuillez sélectionner un produit");
        return;
    }

    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/edit_product_form.fxml"));
        Parent root = loader.load();
        
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        
        EditProductController controller = loader.getController();
        controller.initializeData(selectedProduct, this, stage);
        
        stage.setTitle("Modifier Produit");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        
        refreshTable();
    } catch (IOException e) {
        showAlert("Erreur", "Impossible d'ouvrir l'éditeur: " + e.getMessage());
    }
    }
    
// Ajoutez cette méthode pour mettre à jour le produit en base de données
    public boolean updateProductInDatabase(Product product) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE product SET name = ?, description = ?, price = ?, quantity = ?, image_path = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getQuantity());
            stmt.setString(5, product.getImagePath());
            stmt.setInt(6, product.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            showAlert("Erreur", "Erreur base de données: " + e.getMessage());
            return false;
        }
    }

    
}
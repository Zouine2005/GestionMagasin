package models;

import javafx.beans.property.*;

public class Product {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final DoubleProperty price;
    private final IntegerProperty quantity;
    private final StringProperty imagePath;

    public Product(int id, String name, String description, double price, int quantity, String imagePath) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.price = new SimpleDoubleProperty(price);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.imagePath = new SimpleStringProperty(imagePath);
    }

    // Getters classiques (pour TableView)
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getDescription() { return description.get(); }
    public double getPrice() { return price.get(); }
    public int getQuantity() { return quantity.get(); }
    public String getImagePath() { return imagePath.get(); }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setName(String name) { this.name.set(name); }
    public void setDescription(String description) { this.description.set(description); }
    public void setPrice(double price) { this.price.set(price); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public void setImagePath(String imagePath) { this.imagePath.set(imagePath); }

    // Property getters (utiles si tu veux les binder dans le futur)
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public DoubleProperty priceProperty() { return price; }
    public IntegerProperty quantityProperty() { return quantity; }
    public StringProperty imagePathProperty() { return imagePath; }
}

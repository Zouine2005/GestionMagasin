package models;

public class Admin {
    private final String username;
    private String password;

    public Admin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur ne peut pas être vide.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères.");
        }
        this.username = username.trim();
        this.password = password;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    // Setter pour le mot de passe
    public void setPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères.");
        }
        this.password = password;
    }
}
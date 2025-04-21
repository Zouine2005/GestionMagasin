import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/magasin";
        String user = "root";
        String password = "12345678";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            if (conn != null) {
                System.out.println("Connexion à MySQL réussie ✅");
            }
        } catch (SQLException e) {
            System.out.println("Erreur de connexion ❌");
            e.printStackTrace();
        }
    }
}

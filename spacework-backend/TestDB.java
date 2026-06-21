import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDB {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=Sistema_Reserva_Espacios;encrypt=true;trustServerCertificate=true;";
        String user = "sa";
        String password = "Klinzxd";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Usuarios.Usuarios")) {

            while (rs.next()) {
                System.out.println(rs.getString("CorreoElectronico"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

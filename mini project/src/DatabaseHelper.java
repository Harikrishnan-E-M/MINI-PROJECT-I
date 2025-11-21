import java.sql.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:traffic.db";

    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC"); // load SQLite driver
            Connection conn = DriverManager.getConnection(DB_URL);
            System.out.println(" Connected to SQLite!");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void insertVehicle(String direction, String number, String type) {
        String sql = "INSERT INTO vehicles(direction, vehicleNumber, type) VALUES(?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, direction);
            pstmt.setString(2, number);
            pstmt.setString(3, type);
            pstmt.executeUpdate();

            System.out.println(" Vehicle inserted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getAllVehicles() {
        String sql = "SELECT * FROM vehicles";
        try {
            Connection conn = connect();
            if (conn != null) {
                return conn.createStatement().executeQuery(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteVehicles(String direction) {
        String sql = "DELETE FROM vehicles WHERE direction = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, direction);
            int deleted = pstmt.executeUpdate();
            System.out.println(deleted + " vehicle(s) deleted from " + direction + " direction.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

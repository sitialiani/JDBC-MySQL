import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Create {

    static final String DB_URL = "jdbc:mysql://localhost:3306/";
    static final String USER = "root";
    static final String PASSWORD = "password";
public static void main(String[] args) {
    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
    Statement stmt = conn.createStatement();)
    {
       String sql = "CREATE DATABASE Supermarket";
       stmt.executeUpdate(sql);
       System.out.println("Database Created Successfully...");
    } catch (SQLException e)
    {
        e.printStackTrace();
    }
}

}

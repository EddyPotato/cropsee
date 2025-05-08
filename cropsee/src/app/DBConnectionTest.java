package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionTest { 
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/cropsee_db";
        String username = "root";
        String password = ""; // Leave empty if you're using XAMPP default

        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database successfully!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }
}

//THIS IS FOR TESTING CONNECTIVITY
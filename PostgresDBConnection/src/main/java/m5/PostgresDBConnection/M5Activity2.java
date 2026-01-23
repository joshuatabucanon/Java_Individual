package m5.PostgresDBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class M5Activity2 {

    private static final String URL = "jdbc:postgresql://localhost:5432/training_db";
    private static final String USER = "jrptabucanon";
    private static final String PASSWORD = "postgres";
    
    public static void main( String[] args )
    {

    	Connection conn = null;

        try {
        	 
            // Attempt connection
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Connected successfully");

        } catch (SQLException e) {

            System.out.println("Connection failed: " + e.getMessage());


        } finally {

            // Close connection if it was opened
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("Database connection closed.");
                }
            } catch (SQLException ex) {
                System.out.println("Error closing connection: " + ex.getMessage());
            }




        }
    }
}

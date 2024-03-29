/**
 * Nicholas Dhannie
 * CEN 3024C - Software Development 1
 * March 29, 2024
 * JDBS.java
 * This class is what will be testing the connection to the database
 */

package org.nicholas.guicardealershipsystem;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBC {
    public static void main(String[] args) throws SQLException {

        String query = "select * from Cars";
        String url = "jdbc:sqlite:C:/sqlite/db/CarDealership.db";

        try {
            Connection con = DriverManager.getConnection(url);
            Statement statement = con.createStatement();
            ResultSet result = statement.executeQuery(query);

            while (result.next()) {
                String CarDealershipData = "";
                for (int i = 1; i <= 9; i++) {
                    CarDealershipData += result.getString(i) + " ";
                }
                System.out.println(CarDealershipData);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

    }
}

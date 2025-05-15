package io.github.lo3ba.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnexionBD {
    static Connection connexion=null;
    static String url="jdbc:mysql://localhost:3306/jet_game";
    static String user="root";
    static String password ="";

    public static Statement seConnecter(){
        Statement stm = null;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            connexion= DriverManager.getConnection(url,user, password);
            stm = connexion.createStatement();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return stm;
    }

    public static void seDeconnecter(){
        try {
            connexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

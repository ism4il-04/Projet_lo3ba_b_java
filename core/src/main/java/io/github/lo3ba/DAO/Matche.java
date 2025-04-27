package io.github.lo3ba.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Matche {


    public Matche() {}

    public void AjouterMatche(String nomPlayer,int score, String niveau){
        try {
            Statement stm = ConnexionBD.seConnecter();
            stm.executeUpdate("insert into matche (idPlayer,score,niveau) values ("+getPlayerIdByName(nomPlayer)+","+score+",'"+niveau+"')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerIdByName(String playerName) {
        try {
            Statement stm = ConnexionBD.seConnecter();
            ResultSet rs = stm.executeQuery("select id from player where nom = '" + playerName + "';");
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}

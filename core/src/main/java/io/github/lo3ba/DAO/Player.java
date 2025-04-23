package io.github.lo3ba.DAO;

import java.sql.SQLException;
import java.sql.Statement;

public class Player {
    public int id;
    public String name;
    public int meilleurScore;

    public Player(){
    }

    public void MajScore(int id, int score) {
        try {
            Statement stm = ConnexionBD.seConnecter();
            stm.executeUpdate("update Player set meilleurScore ="+score+" where id ="+id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void Ajouter(Player l){
        try {
            Statement stm = ConnexionBD.seConnecter();
            stm.executeUpdate(("insert into Player values("+this.id+",'"
                +this.name+"','"+this.meilleurScore+"')"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

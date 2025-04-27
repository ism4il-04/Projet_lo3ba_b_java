package io.github.lo3ba.DAO;

import com.badlogic.gdx.utils.Array;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Player {
    public int id;
    public String name;
    public int meilleurScore;
    public String niveau;

    public Player(){
    }

    public Player(int id, String name, int meilleurScore, String niveau) {
        this.id = id;
        this.name = name;
        this.meilleurScore = meilleurScore;
        this.niveau = niveau;
    }

    public Player(String name) {
        this.name = name;
    }

    public void MajScore(String nom, int score) {
        try {
            Statement stm = ConnexionBD.seConnecter();
            stm.executeUpdate("update Player set meilleurScore ="+score+" where nom ='"+nom+"'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void Ajouter(String l){
        try {
            Statement stm = ConnexionBD.seConnecter();
            stm.executeUpdate("insert into Player (nom,meilleurScore,niveau) values('"+l+"',0,'easy');");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Player> getAllPlayers(){
        List<Player> players = new ArrayList<Player>();
        try {
            Statement stm = ConnexionBD.seConnecter();
            ResultSet rs=stm.executeQuery("SELECT * FROM player");
            while(rs.next()){
                int id1 = rs.getInt(1);
                String name = rs.getString(2);
                int meilleurScore = rs.getInt(3);
                String niveau = rs.getString(4);
                players.add(new Player(id1,name,meilleurScore,niveau));
}
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    public List<Player> getTop3Players() {
        List<Player> players = new ArrayList<Player>();
        try {
            Statement stm = ConnexionBD.seConnecter();
            ResultSet rs = stm.executeQuery("SELECT * FROM player ORDER BY meilleurScore DESC LIMIT 3");
            while (rs.next()) {
                int id1 = rs.getInt(1);
                String name = rs.getString(2);
                int meilleurScore = rs.getInt(3);
                String niveau = rs.getString(4);
                players.add(new Player(id1, name, meilleurScore, niveau));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    public Array<String> getAllPlayersNames(){
        Array<String> players = new Array<>();
        try {
            Statement stm = ConnexionBD.seConnecter();
            ResultSet rs=stm.executeQuery("SELECT nom FROM player");
            while(rs.next()){
                players.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

}

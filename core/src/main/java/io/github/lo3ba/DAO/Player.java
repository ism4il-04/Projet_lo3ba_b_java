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

    public void majScoreSiDepasse(String nom, int score) {
        try {
            Statement stm = ConnexionBD.seConnecter();

            // Retrieve the current meilleurScore for the player
            ResultSet rs = stm.executeQuery("SELECT meilleurScore FROM Player WHERE nom = '" + nom + "'");
            if (rs.next()) {
                int meilleurScore = rs.getInt("meilleurScore");

                // Update the score only if the new score is higher
                if (score > meilleurScore) {
                    stm.executeUpdate("UPDATE Player SET meilleurScore = " + score + " WHERE nom = '" + nom + "'");
                    System.out.println("Score mis à jour pour " + nom);
                } else {
                    System.out.println("Le nouveau score n'est pas supérieur au meilleur score actuel.");
                }
            } else {
                System.out.println("Aucun joueur trouvé avec le nom : " + nom);
            }
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


    public String getName() {
        return name;
    }
    public int getMeilleurScore() {
        return meilleurScore;
    }
    public List<Jet> getAllJets() {
        List<Jet> jets = new ArrayList<>();
        try {
            Statement stm = ConnexionBD.seConnecter();
            ResultSet rs = stm.executeQuery("SELECT * FROM Jet");
            while (rs.next()) {
                jets.add(new Jet(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("texture_path"),
                    rs.getInt("speed"),
                    rs.getInt("attack"),
                    rs.getInt("unlock_score")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jets;
    }

    public Jet getJetByName(String name) {
        try {
            Statement stm = ConnexionBD.seConnecter();
            ResultSet rs = stm.executeQuery("SELECT * FROM Jet WHERE name = '" + name + "'");
            if (rs.next()) {
                return new Jet(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("texture_path"),
                    rs.getInt("speed"),
                    rs.getInt("attack"),
                    rs.getInt("unlock_score")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Inner class for Jet (add this inside Player class)
    public static class Jet {
        public int id;
        public String name;
        public String texturePath;
        public int speed;
        public int attack;
        public int unlockScore;

        public Jet(int id, String name, String texturePath, int speed, int attack, int unlockScore) {
            this.id = id;
            this.name = name;
            this.texturePath = texturePath;
            this.speed = speed;
            this.attack = attack;
            this.unlockScore = unlockScore;
        }
    }
}

package io.github.lo3ba.DAO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private int id;
    private String name;
    private Integer bestScore;
    private String difficulty;

    public Player() {}

    public Player(int id, String name, Integer bestScore, String difficulty) {
        this.id = id;
        this.name = name;
        this.bestScore = bestScore;
        this.difficulty = difficulty;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public Integer getBestScore() { return bestScore; }
    public String getDifficulty() { return difficulty; }

    public void updateScoreIfHigher(String playerName, int score) {
        if (playerName == null) {
            Gdx.app.error("Player", "Null player name");
            return;
        }

        String sql = "UPDATE player SET meilleurScore = ? WHERE nom = ? AND (? > IFNULL(meilleurScore,0))";

        try {
            ConnexionBD.executeUpdate(sql, score, playerName, score);
            Gdx.app.log("Player", "Score updated for " + playerName);
        } catch (SQLException e) {
            Gdx.app.error("Player", "Score update failed", e);
        }
    }

    public void addPlayer(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            Gdx.app.error("Player", "Invalid player name");
            return;
        }

        String sql = "INSERT INTO player (nom) VALUES (?)";

        try {
            ConnexionBD.executeUpdate(sql, playerName.trim());
            Gdx.app.log("Player", "Added player: " + playerName);
        } catch (SQLException e) {
            Gdx.app.error("Player", "Failed to add player", e);
        }
    }

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT id, nom, meilleurScore, niveau FROM player";

        try (ResultSet rs = ConnexionBD.executeQuery(sql)) {
            while (rs.next()) {
                players.add(new Player(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getObject("meilleurScore", Integer.class), // Handles NULL
                    rs.getString("niveau")
                ));
            }
        } catch (SQLException e) {
            Gdx.app.error("Player", "Failed to load players", e);
            // Fallback data
            players.add(new Player(0, "Player1", 1000, "easy"));
            players.add(new Player(0, "Player2", 800, "easy"));
        }
        return players;
    }

    public List<Player> getTop3Players() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT id, nom, meilleurScore, niveau FROM player ORDER BY IFNULL(meilleurScore,0) DESC LIMIT 3";

        try (ResultSet rs = ConnexionBD.executeQuery(sql)) {
            while (rs.next()) {
                players.add(new Player(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getObject("meilleurScore", Integer.class),
                    rs.getString("niveau")
                ));
            }
        } catch (SQLException e) {
            Gdx.app.error("Player", "Failed to load top players", e);
            // Fallback data
            players.add(new Player(0, "Top1", 5000, "hard"));
            players.add(new Player(0, "Top2", 4000, "normal"));
            players.add(new Player(0, "Top3", 3000, "easy"));
        }
        return players;
    }

    public Array<String> getAllPlayerNames() {
        Array<String> names = new Array<>();
        String sql = "SELECT nom FROM player";

        try (ResultSet rs = ConnexionBD.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("nom");
                if (name != null) names.add(name);
            }
        } catch (SQLException e) {
            Gdx.app.error("Player", "Failed to load names", e);
            names.addAll("Player1", "Player2", "Player3");
        }
        return names;
    }
}

package io.github.lo3ba.DAO;

import com.badlogic.gdx.Gdx;
import java.sql.*;

public class Matche {
    public void addMatch(String playerName, int score, String difficulty) {
        if (playerName == null || difficulty == null) {
            Gdx.app.error("Matche", "Invalid input");
            return;
        }

        // First ensure player exists
        int playerId = getOrCreatePlayer(playerName);
        if (playerId == -1) {
            Gdx.app.error("Matche", "Could not resolve player: " + playerName);
            return;
        }

        // Insert match
        String sql = "INSERT INTO matche (idPlayer, score, niveau) VALUES (?, ?, ?)";

        try {
            ConnexionBD.executeUpdate(sql, playerId, score, difficulty);
            Gdx.app.log("Matche", "Saved match for " + playerName);
        } catch (SQLException e) {
            Gdx.app.error("Matche", "Failed to save match", e);
        }
    }

    private int getOrCreatePlayer(String playerName) {
        // Try to get existing player
        int playerId = getPlayerIdByName(playerName);
        if (playerId != -1) return playerId;

        // Create if doesn't exist
        try {
            new Player().addPlayer(playerName);
            return getPlayerIdByName(playerName);
        } catch (Exception e) {
            Gdx.app.error("Matche", "Failed to create player", e);
            return -1;
        }
    }

    private int getPlayerIdByName(String playerName) {
        String sql = "SELECT id FROM player WHERE nom = ?";

        try (ResultSet rs = ConnexionBD.executeQuery(sql, playerName)) {
            return rs.next() ? rs.getInt("id") : -1;
        } catch (SQLException e) {
            Gdx.app.error("Matche", "Failed to get player ID", e);
            return -1;
        }
    }
}

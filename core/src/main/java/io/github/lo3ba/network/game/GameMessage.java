package io.github.lo3ba.network.game;

public class GameMessage {
    public static class Connect {
        public int playerId;
        public String playerName;
    }

    public static class Disconnect {
        public int playerId;
    }

    public static class PlayerPosition {
        public int playerId;  // Unique ID for each player
        public float x, y;    // Position coordinates
        public float rotation; // Facing direction (if needed)
    }
}

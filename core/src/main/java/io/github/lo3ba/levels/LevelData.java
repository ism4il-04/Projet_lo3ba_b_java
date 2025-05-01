package io.github.lo3ba.levels;

import com.badlogic.gdx.graphics.Texture;

public class LevelData {
    private final int levelNumber;
    private final int enemiesRequired;
    private final float enemySpawnRate;
    private final float enemySpeed;
    private final int scoreToUnlock;
    private final String backgroundTexturePath;
    private final float powerUpChance;

    public LevelData(int levelNumber, int enemiesRequired, float enemySpawnRate,
                     float enemySpeed, int scoreToUnlock, String backgroundTexturePath,
                     float powerUpChance) {
        this.levelNumber = levelNumber;
        this.enemiesRequired = enemiesRequired;
        this.enemySpawnRate = enemySpawnRate;
        this.enemySpeed = enemySpeed;
        this.scoreToUnlock = scoreToUnlock;
        this.backgroundTexturePath = backgroundTexturePath;
        this.powerUpChance = powerUpChance;
    }

    // Getters
    public int getLevelNumber() {
        return levelNumber;
    }

    public int getEnemiesRequired() {
        return enemiesRequired;
    }

    public float getEnemySpawnRate() {
        return enemySpawnRate;
    }

    public float getEnemySpeed() {
        return enemySpeed;
    }

    public int getScoreToUnlock() {
        return scoreToUnlock;
    }

    public String getBackgroundTexture() {
        return backgroundTexturePath;
    }

    public float getPowerUpChance() {
        return powerUpChance;
    }
}

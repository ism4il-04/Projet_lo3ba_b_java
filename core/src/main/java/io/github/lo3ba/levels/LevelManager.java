package io.github.lo3ba.levels;
import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;

public class LevelManager {
    private final Array<LevelData> levels;
    private int currentLevelIndex;
    private int enemiesToDefeat;
    private int enemiesDefeated;

    public LevelManager() {
        this.levels = new Array<>();
        this.currentLevelIndex = 0;
        this.enemiesToDefeat = 0;
        this.enemiesDefeated = 0;
    }

    public void addLevel(LevelData levelData) {
        levels.add(levelData);
    }

    public void clearLevels() {
        levels.clear();
        currentLevelIndex = 0;
        resetEnemyCounters();
    }

    public void initializeLevels(String difficulty) {
        clearLevels();

        int baseEnemies = 10;
        float baseSpawnRate = 1.5f;
        float powerUpChance = 0.2f;

        switch (difficulty) {
            case "normal":
                baseEnemies = 15;
                baseSpawnRate = 1.2f;
                powerUpChance = 0.15f;
                break;
            case "hard":
                baseEnemies = 20;
                baseSpawnRate = 1.0f;
                powerUpChance = 0.1f;
                break;
        }

        // Level 1
        addLevel(new LevelData(
            1, baseEnemies, baseSpawnRate, 150f, 1000,
            "fontdecran.png", powerUpChance
        ));

        // Level 2
        addLevel(new LevelData(
            2, baseEnemies + 5, baseSpawnRate * 0.9f, 180f, 2000,
            "fontdecran.png", powerUpChance * 0.9f
        ));

        // Level 3 only for hard difficulty
        if (difficulty.equals("hard")) {
            addLevel(new LevelData(
                3, baseEnemies + 10, baseSpawnRate * 0.8f, 210f, 3000,
                "fontdecran.png", powerUpChance * 0.8f
            ));
        }

        startCurrentLevel();
    }

    public void startCurrentLevel() {
        if (levels.size == 0) {
            Gdx.app.error("LevelManager", "No levels loaded!");
            initializeLevels("easy"); // Fallback
        }

        if (currentLevelIndex < levels.size) {
            LevelData level = levels.get(currentLevelIndex);
            enemiesToDefeat = level.getEnemiesRequired();
            enemiesDefeated = 0;
        }
    }

    public void enemyDefeated() {
        enemiesDefeated++;
    }

    public boolean isLevelComplete() {
        return currentLevelIndex < levels.size && enemiesDefeated >= enemiesToDefeat;
    }

    public boolean advanceToNextLevel() {
        if (isLevelComplete()) {
            currentLevelIndex++;
            if (currentLevelIndex < levels.size) {
                startCurrentLevel();
                return true;
            }
        }
        return false;
    }

    public void resetEnemyCounters() {
        enemiesToDefeat = 0;
        enemiesDefeated = 0;
    }

    // Getters
    public LevelData getCurrentLevelData() {
        return levels.get(currentLevelIndex);
    }

    public int getCurrentLevelNumber() {
        return currentLevelIndex + 1;
    }

    public int getTotalLevels() {
        return levels.size;
    }

    public int getEnemiesToDefeat() {
        return enemiesToDefeat;
    }

    public int getEnemiesDefeated() {
        return enemiesDefeated;
    }
}

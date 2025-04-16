package io.github.lo3ba.entities;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.lo3ba.Main_Game;

public class Main {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Lo3ba_b_java");
        config.setWindowedMode(800, 600);
        config.setResizable(false);

        // Additional recommended configuration
        config.setForegroundFPS(60);
        config.setIdleFPS(30);
        config.useVsync(true);

        try {
            new Lwjgl3Application(new Main_Game(), config);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

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
        new Lwjgl3Application(new Main_Game(), config);

    }
}

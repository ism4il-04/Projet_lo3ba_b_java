package io.github.lo3ba.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.lo3ba.Main_Game;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Lo3ba B Java");
        config.setWindowedMode(800, 600);
        config.setResizable(false);
        new Lwjgl3Application(new Main_Game(), config);
    }
}

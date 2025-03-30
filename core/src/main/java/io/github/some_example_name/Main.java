package io.github.some_example_name;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    Texture backgroundTexture;
    Texture jetTexture;
    Texture enemyJet;
    Sound hitSound;
    Sound explosionSound;
    Texture shotTexture;
    SpriteBatch spriteBatch;
    FitViewport viewport;
    OrthographicCamera camera;
    int backgroundOffset;
    private final int WORLD_HEIGHT = 5; // Set your world height here
    private final int WORLD_WIDTH = 8;
    Jet jet;
    Array<Bullet> bullets;
    float bulletCooldown = 0.2f; // Time between shots (in seconds)
    float timeSinceLastShot = 0f;
    @Override
    public void create() {
        backgroundTexture = new Texture("back.png");
        jetTexture = new Texture("Jet1.png");
        enemyJet = new Texture("Jet1.png");
        shotTexture = new Texture("shot_1.png");
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5); // Your screen size
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        backgroundOffset = 0;
        jet = new Jet(jetTexture);
        bullets = new Array<>();




    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        updateBackgroundOffset();
        input();
        logic();
        draw();
    }
    public void updateBackgroundOffset() {
        float delta = Gdx.graphics.getDeltaTime();
        float speed = 56f;  // You can tweak this value for a slower or faster background
        backgroundOffset += speed * delta;  // Increment the background offset

        // Reset background offset when it goes beyond the height of the screen
        if (backgroundOffset >= viewport.getWorldHeight()) {
            backgroundOffset = 0;
        }
    }
    public void input() {
        float delta = Gdx.graphics.getDeltaTime();
        jet.update(delta);

    }
    public void logic(){
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        jet.clampPosition(worldWidth, worldHeight);
        float delta = Gdx.graphics.getDeltaTime();

        // Update all bullets
        for (Bullet bullet : jet.getBullets()) {
            bullet.update(delta); // Move bullets
        }

    }
    public void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // Scroll the background using the backgroundOffset
        spriteBatch.draw(backgroundTexture, 0, -backgroundOffset, worldWidth, worldHeight); // First layer
        spriteBatch.draw(backgroundTexture, 0, -backgroundOffset + worldHeight, worldWidth, worldHeight); // Second layer (repeats as the first one scrolls off screen)

        // Draw the jet
        jet.draw(spriteBatch);

        spriteBatch.end();
    }



    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        jetTexture.dispose();
        enemyJet.dispose();
        shotTexture.dispose();
        spriteBatch.dispose();

    }
}

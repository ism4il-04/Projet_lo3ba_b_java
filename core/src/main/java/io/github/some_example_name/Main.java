package io.github.some_example_name;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;


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
    Sprite jetSprite;
    Array<Sprite> shotSprites;
    @Override
    public void create() {
        backgroundTexture = new Texture("back.png");
        jetTexture = new Texture("Jet1.png");
        enemyJet = new Texture("Jet1.png");
        shotTexture = new Texture("shot_1.png");
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8,5);

        jetSprite = new Sprite(jetTexture);
        jetSprite.setSize(1,1);
        shotSprites = new Array<>();
        createShot();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    public void input() {
        float speed = 2f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            jetSprite.translateX(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            jetSprite.translateX(-speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            jetSprite.translateY(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            jetSprite.translateY(-speed * delta);
        }
    }
    public void logic(){
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float jetWidth = jetSprite.getWidth();
        float jetHeight = jetSprite.getHeight();

        jetSprite.setX(MathUtils.clamp(jetSprite.getX(),0,worldWidth-jetWidth));
        jetSprite.setY(MathUtils.clamp(jetSprite.getY(),0,worldHeight-jetHeight));

        float delta = Gdx.graphics.getDeltaTime();

        for (Sprite shotSprite : shotSprites) {
            shotSprite.translateY(2f*delta);
        }
    }
    public void draw(){
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        float worldwidth = viewport.getWorldWidth();
        float worldheight = viewport.getWorldHeight();
        spriteBatch.draw(backgroundTexture, 0, 0, worldwidth, worldheight);
        jetSprite.draw(spriteBatch);
        for (Sprite shotSprite : shotSprites) {
            shotSprite.draw(spriteBatch);
        }
        spriteBatch.end();
    }

    private void createShot(){
        float shotWidth = .1f;
        float shotHeight = .1f;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        Sprite shotSprite = new Sprite(shotTexture);
        shotSprite.setSize(shotWidth, shotHeight);
        shotSprite.setX(MathUtils.random(0f,worldWidth-shotWidth));
        shotSprite.setY(worldHeight);
        shotSprites.add(shotSprite);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
    }
}

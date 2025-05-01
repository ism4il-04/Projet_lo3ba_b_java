package io.github.lo3ba.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Ship {
    private String shipName;
    private float speed,laserSpeed;
    private float xPosition,yPosition;
    private float width,height,laserWidth,laserHeight;
    private Texture shipTexture, laserTexture;
    private float timeBetweenShots;
    private float timeSinceLastShot = 0;


    public Ship(String shipName, float speed, float xPosition, float yPosition, float width, float height, Texture shipTexture, Texture laserTexture) {
        this.shipName = shipName;
        this.speed = speed;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
        this.shipTexture = shipTexture;
        this.laserTexture = laserTexture;
        this.laserSpeed = 200;
        this.laserHeight=10;
        this.timeBetweenShots = .5f;
        this.laserWidth=10;
    }

    public void draw(Batch batch) {
        batch.draw(shipTexture,xPosition,yPosition,width,height);
    }

    public void update(float delta) {
        timeSinceLastShot += delta;
    }

    public boolean canFireLaser () {
        return  timeSinceLastShot - timeBetweenShots >= 0;
    }

    public Laser[] fireLasers (){
        if (!canFireLaser()) return new Laser[0];

        timeSinceLastShot = 0;

        Laser[] lasers = new Laser[1];

        lasers[0] = new Laser(10,xPosition + width/2 - laserWidth/2,yPosition + height,laserWidth,laserHeight,laserTexture);

        return lasers;
    }

    public void translateX(float v) {
        xPosition += v;
    }
    public void translateY(float v) {
        yPosition += v;
    }

    public void setPosition(float x, float y) {
        this.xPosition = x;
        this.yPosition = y;
    }

    public String getX() {
        return String.valueOf(xPosition);
    }

    public String getY() {
        return String.valueOf(yPosition);
    }
}

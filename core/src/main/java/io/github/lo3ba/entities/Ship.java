package io.github.lo3ba.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import jdk.jfr.DataAmount;


public class Ship {
    private String shipName;
    private float speed,laserSpeed;
    private float xPosition,yPosition;
    private float width,height,laserWidth,laserHeight;
    private Texture shipTexture, laserTexture;
    private int maxHealth, health;
    private float timeBetweenShots;
    private float timeSinceLastShot = 0;


    public Ship(String shipName, float speed, float xPosition, float yPosition,int maxHealth, Texture shipTexture, Texture laserTexture) {
        this.shipName = shipName;
        this.speed = speed;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = 50;
        this.height = 50;
        this.shipTexture = shipTexture;
        this.laserTexture = laserTexture;
        this.laserSpeed = 200;
        this.laserHeight=10;
        this.timeBetweenShots = .5f;
        this.laserWidth=10;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
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

        lasers[0] = new Laser(10,xPosition + width/2 - laserWidth/2,yPosition + height,laserWidth,laserHeight,1,laserTexture);

        return lasers;
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(xPosition, yPosition, width, height);
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

    public float getX() {
        return xPosition;
    }

    public float getY() {
        return yPosition;
    }

    public float getHeight() {
        return height;
    }

    public void setY(float max) {
        this.yPosition = max;
    }

    public void setX(float max) {
        this.xPosition = max;
    }

    public float getWidth() {
        return width;
    }
    public void takeDamage(int damage) {
        health -= damage;
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}

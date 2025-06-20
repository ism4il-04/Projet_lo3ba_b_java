package io.github.lo3ba.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public class Laser  {

    private float movementSpeed;

    private float xPosition, yPosition; //bottom center
    private float width, height;
    private int direction = 1;

    public float getxPosition() {
        return xPosition;
    }

    public float getyPosition() {
        return yPosition;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public int getDirection() {
        return direction;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public void setxPosition(float xPosition) {
        this.xPosition = xPosition;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    //graphics
    Texture laserTexture;

    public Laser(float movementSpeed, float xPosition, float yPosition, float width, float height,int direction, Texture laserTexture) {
        this.movementSpeed = movementSpeed;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
        this.direction = direction;
        this.laserTexture = laserTexture;

    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(xPosition, yPosition, width, height);
    }

    public void update(float delta) {
        yPosition += 200 * delta * direction; // laser speed
    }

    public boolean isOffScreen(float screenHeight) {
        return yPosition > screenHeight || yPosition + height < 0;
    }


    public void draw (Batch batch) {
        batch.draw(laserTexture, xPosition, yPosition, width, height);
    }

    public void setDirection(int direction) {
        this.direction = direction; // 1 for up, -1 for down
    }


    public void setyPosition(float yPosition) {
        this.yPosition=yPosition;
    }
}

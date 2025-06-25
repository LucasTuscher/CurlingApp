package com.curlingapp.game.physics.gamesensorphysics;

import java.util.Random;

public class GameSensorModiStonePhysics {
    private float x, y;
    private float velocityX, velocityY;
    private float size;
    private float friction;
    private float rotation;
    private float rotationFactor;
    private Random random = new Random();

    private static final float STOP_THRESHOLD = 1f;
    private static final float FRICTION_MULTIPLIER = 0.995f;
    private static final float SMOOTH_STOP_FACTOR = 0.951f;
    private static final float TOP_BAR_HEIGHT = 150;

    public GameSensorModiStonePhysics(float x, float y, float size, float friction, float rotationFactor) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.friction = friction;
        this.rotationFactor = rotationFactor;
        this.rotation = 0;
    }

    public void update(float delta, int screenWidth, int screenHeight, float accelX, float accelY, float gyroZ) {
        float velocityMagnitude = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        float currentFriction = friction * FRICTION_MULTIPLIER;

        velocityX += accelX * 0.1f;
        velocityY += accelY * 0.1f;
        rotation += gyroZ * rotationFactor;

        velocityX *= currentFriction;
        velocityY *= currentFriction;

        float curlEffect = rotation * 0.001f * velocityMagnitude;
        velocityX += curlEffect;

        rotation *= 0.998f;

        x += velocityX * delta;
        y += velocityY * delta;

        if (velocityMagnitude < STOP_THRESHOLD) {
            velocityX *= SMOOTH_STOP_FACTOR;
            velocityY *= SMOOTH_STOP_FACTOR;
        }

        if (x < 0) x = 0;
        if (x > screenWidth - size) x = screenWidth - size;
        if (y < 0) y = 0;
        if (y > screenHeight - size - TOP_BAR_HEIGHT) y = screenHeight - size - TOP_BAR_HEIGHT;
    }

    public void applyRandomVariation(float accelVariation, float rotationVariation) {
        float accelXVariation = (random.nextFloat() - 0.5f) * accelVariation;
        float accelYVariation = (random.nextFloat() - 0.5f) * accelVariation;
        float rotationZVariation = (random.nextFloat() - 0.5f) * rotationVariation;

        velocityX += accelXVariation;
        velocityY += accelYVariation;
        rotation += rotationZVariation;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getSize() {
        return size;
    }

    public void setVelocity(float velocityX, float velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}

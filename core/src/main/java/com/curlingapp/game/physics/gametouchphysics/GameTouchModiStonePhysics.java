package com.curlingapp.game.physics.gametouchphysics;

public class GameTouchModiStonePhysics {
    private float x, y;
    private float velocityX, velocityY;
    private float size;
    private float friction;
    private float rotation;
    private float rotationFactor;

    private static final float STOP_THRESHOLD = 1f;
    private static final float FRICTION_MULTIPLIER = 0.995f;
    private static final float SMOOTH_STOP_FACTOR = 0.951f;
    private static final float TOP_BAR_HEIGHT = 150; // Höhe der oberen Leiste

    public GameTouchModiStonePhysics(float x, float y, float size, float friction, float rotationFactor) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.friction = friction;
        this.rotationFactor = rotationFactor;
        this.rotation = 0;
    }

    public void update(float delta, int screenWidth, int screenHeight) {
        float velocityMagnitude = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        float currentFriction = friction * FRICTION_MULTIPLIER;

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

    // Getter und Setter
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
}

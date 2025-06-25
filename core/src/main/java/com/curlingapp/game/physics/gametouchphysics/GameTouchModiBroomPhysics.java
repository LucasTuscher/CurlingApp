package com.curlingapp.game.physics.gametouchphysics;

import com.badlogic.gdx.math.Vector2;

public class GameTouchModiBroomPhysics {
    private Vector2 position;
    private float width, height, speed;

    private static final float BROOM_DECELERATION = 0.9f;

    public GameTouchModiBroomPhysics(float x, float y, float width, float height, float speed) {
        this.position = new Vector2(x, y);
        this.width = width;
        this.height = height;
        this.speed = speed;
    }

    public void update(float touchX, float touchY, boolean isTouching) {
        if (isTouching) {
            position.lerp(new Vector2(touchX - width / 2f, touchY - height / 2f), BROOM_DECELERATION);
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getSpeed() {
        return speed;
    }

    public boolean checkCollision(GameTouchModiStonePhysics stone) {
        return position.x < stone.getX() + stone.getSize() &&
            position.x + width > stone.getX() &&
            position.y < stone.getY() + stone.getSize() &&
            position.y + height > stone.getY();
    }
}

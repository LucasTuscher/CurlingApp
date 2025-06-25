package com.curlingapp.game.physics.gametouchphysics;

public class GameTouchModiCollisionManager {

    public static boolean checkBroomStoneCollision(GameTouchModiBroomPhysics broom, GameTouchModiStonePhysics stone, float delta) {
        if (broom.checkCollision(stone)) {
            stone.setVelocity(
                (stone.getX() - broom.getPosition().x) / 10f * broom.getSpeed() * delta,
                (stone.getY() - broom.getPosition().y) / 10f * broom.getSpeed() * delta
            );
            return true;
        }
        return false;
    }
}

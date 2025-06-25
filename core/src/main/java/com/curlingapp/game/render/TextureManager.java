package com.curlingapp.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import java.util.Random;

public class TextureManager {
    private Texture[] stoneTextures;
    private Texture fieldTexture;
    private Texture broomTexture;
    private Texture pixelTexture;
    private Random random;

    public TextureManager() {
        stoneTextures = new Texture[]{
            new Texture("images/stone.png"),
            new Texture("images/stone2.png"),
            new Texture("images/stone99.png")
        };
        fieldTexture = new Texture("images/GameFieldBackground.png");
        broomTexture = new Texture("images/broom.png");
        pixelTexture = new Texture(Gdx.files.internal("images/blueColor.png"));
        random = new Random();
    }

    public Texture getRandomStoneTexture() {
        return stoneTextures[random.nextInt(stoneTextures.length)];
    }

    public Texture getFieldTexture() {
        return fieldTexture;
    }

    public Texture getBroomTexture() {
        return broomTexture;
    }

    public Texture getPixelTexture() {
        return pixelTexture;
    }

    public void dispose() {
        for (Texture texture : stoneTextures) {
            texture.dispose();
        }
        fieldTexture.dispose();
        broomTexture.dispose();
        pixelTexture.dispose();
    }
}

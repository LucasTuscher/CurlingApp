package com.curlingapp.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Color;
import com.curlingapp.game.CurlingAppClass;
import com.curlingapp.game.ResultScreen;
import com.curlingapp.game.data.GameResult;
import com.curlingapp.game.fontloader.FontLoader;
import com.curlingapp.game.gamemodes.GameMode;
import com.curlingapp.game.physics.gametouchphysics.GameTouchModiCollisionManager;
import com.curlingapp.game.utils.GameTimerUtils;
import com.badlogic.gdx.utils.Timer;
import com.curlingapp.game.utils.SaveGameUtils;
import com.curlingapp.game.physics.gametouchphysics.GameTouchModiStonePhysics;
import com.curlingapp.game.physics.gametouchphysics.GameTouchModiBroomPhysics;
import com.curlingapp.game.render.TextureManager;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.Texture;
import com.curlingapp.game.settings.gametouch.GameTouchModiSettings;

public class GameTouchModiScreen implements Screen {
    private final CurlingAppClass game;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private GameTimerUtils gameTimer;
    private int timeLeft;
    private int score = 0;
    private int screenWidth, screenHeight;

    private float stoneSize;
    private float stoneSpeedMultiplier;
    private float targetRadius;
    private float targetX;
    private float targetY;

    private static final float FRICTION = 0.98f;
    private static final float ROTATION_FACTOR = 0.02f;
    private static final float STOP_THRESHOLD = 1f;

    private GameTouchModiStonePhysics stonePhysics;
    private GameTouchModiBroomPhysics broomPhysics;
    private boolean isTouching = false;
    private boolean stoneStopped = false;
    private Stage stage;
    private TextureManager textureManager;
    private Texture targetTexture;
    private Texture stoneTexture;

    private boolean stoneShot = false;
    private GameTouchModiSettings settings;

    public GameTouchModiScreen(CurlingAppClass game, GameTouchModiSettings settings) {
        this.game = game;
        this.settings = settings;

        // Debug-Ausgaben hinzufügen
        Gdx.app.log("GameTouchModiScreen", "Difficulty: " + settings.getDifficulty());
        Gdx.app.log("GameTouchModiScreen", "Stone Size: " + settings.getStoneSize());
        Gdx.app.log("GameTouchModiScreen", "Time Limit: " + settings.getTimeLimit());
        Gdx.app.log("GameTouchModiScreen", "Stone Speed Multiplier: " + settings.getStoneSpeedMultiplier());
        Gdx.app.log("GameTouchModiScreen", "Target Radius: " + settings.getTargetRadius());
    }

    @Override
    public void show() {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        timeLeft = settings.getTimeLimit();
        stoneSize = settings.getStoneSize();
        stoneSpeedMultiplier = settings.getStoneSpeedMultiplier();
        targetRadius = settings.getTargetRadius();
        targetX = (screenWidth - targetRadius * 2) / 2f;
        targetY = (screenHeight - targetRadius * 2) * 0.85f;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(3);
        font.setColor(Color.WHITE);

        textureManager = new TextureManager();
        targetTexture = new Texture(Gdx.files.internal("images/GameFieldTarget.png"));

        resetStonePosition();
        broomPhysics = new GameTouchModiBroomPhysics(screenWidth / 2f, 100, 100, 100, 5000 * stoneSpeedMultiplier);

        gameTimer = new GameTimerUtils();
        gameTimer.scheduleTask(1, 1, new Timer.Task() {
            @Override
            public void run() {
                timeLeft--;
                if (timeLeft <= 0) {
                    gameTimer.stop();
                    SaveGameUtils.saveGameResult(new GameResult(score, System.currentTimeMillis()));
                    game.setScreen(new ResultScreen(game, score, GameMode.GameTouchModi));
                }
            }
        });

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
    }

    private void resetStonePosition() {
        stoneTexture = textureManager.getRandomStoneTexture();
        stonePhysics = new GameTouchModiStonePhysics(screenWidth / 2f - stoneSize / 2f, screenHeight / 2f - stoneSize / 2f - 750, stoneSize, FRICTION, ROTATION_FACTOR);
        stoneStopped = false;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float touchX = Gdx.input.getX();
        float touchY = screenHeight - Gdx.input.getY();
        isTouching = Gdx.input.isTouched();

        broomPhysics.update(touchX, touchY, isTouching);
        boolean collision = GameTouchModiCollisionManager.checkBroomStoneCollision(broomPhysics, stonePhysics, delta);
        stonePhysics.update(delta, screenWidth, screenHeight);

        if (collision) {
            stoneShot = true;
        }

        checkStoneStoppedAndReset();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.setColor(0.2f, 0.2f, 0.2f, 1f);
        batch.draw(textureManager.getPixelTexture(), 0, screenHeight - 150, screenWidth, 150);
        batch.setColor(Color.WHITE);

        batch.draw(textureManager.getFieldTexture(), 0, 0, screenWidth, screenHeight - 150);
        batch.draw(targetTexture, targetX, targetY, targetRadius * 2, targetRadius * 2);
        batch.draw(stoneTexture, stonePhysics.getX(), stonePhysics.getY(), stoneSize, stoneSize);

        if (isTouching) {
            batch.draw(textureManager.getBroomTexture(), broomPhysics.getPosition().x, broomPhysics.getPosition().y, broomPhysics.getWidth(), broomPhysics.getHeight());
        }

        font = FontLoader.getInterTightFont(44, Color.WHITE, FontLoader.FontStyle.BOLD_ITALIC);

        GlyphLayout glyphLayout = new GlyphLayout();

        glyphLayout.setText(font, "Punkte: " + score);
        float punkteWidth = glyphLayout.width;

        glyphLayout.setText(font, "Zeit: " + timeLeft);
        float zeitWidth = glyphLayout.width;

        glyphLayout.setText(font, "Modus: GameTouch");
        float modusWidth = glyphLayout.width;

        float punkteX = 50;
        float zeitX = screenWidth - zeitWidth - 50;
        float modusX = (screenWidth - modusWidth) / 2;

        float padding = -20;
        float textY = screenHeight - (150 - font.getCapHeight()) / 2 - font.getCapHeight() / 2 - padding;

        // Hier ändern wir die Farbe des Modus-Texts basierend auf der Schwierigkeit
        Color modeColor = Color.WHITE; // Standardfarbe für den Modus

        switch (settings.getDifficulty()) {
            case EASY:
                modeColor = Color.GREEN;  // Grün für Easy
                break;
            case NORMAL:
                modeColor = Color.ORANGE;  // Orange für Normal
                break;
            case HARD:
                modeColor = Color.RED;  // Rot für Hard
                break;
        }

        font.setColor(modeColor);

        font.draw(batch, "Punkte: " + score, punkteX, textY);
        font.draw(batch, "Zeit: " + timeLeft, zeitX, textY);
        font.draw(batch, "Modus: GameTouch", modusX, textY);

        batch.end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private void checkStoneStoppedAndReset() {
        float velocityMagnitude = (float) Math.sqrt(Math.pow(stonePhysics.getVelocityX(), 2) + Math.pow(stonePhysics.getVelocityY(), 2));
        if (velocityMagnitude < STOP_THRESHOLD && stoneShot) {
            if (!stoneStopped) {
                stoneStopped = true;
                if (isStoneInsideTarget()) {
                    calculateScore();
                    resetStonePosition();
                    stoneShot = false;
                } else {
                    stoneShot = false;
                }
            }
        } else {
            stoneStopped = false;
        }
    }

    private boolean isStoneInsideTarget() {
        float stoneCenterX = stonePhysics.getX() + stoneSize / 2;
        float stoneCenterY = stonePhysics.getY() + stoneSize / 2;
        float distance = (float) Math.sqrt(Math.pow(stoneCenterX - (targetX + targetRadius), 2) + Math.pow(stoneCenterY - (targetY + targetRadius), 2));
        return distance < targetRadius;
    }

    private void calculateScore() {
        float distance = (float) Math.sqrt(Math.pow(stonePhysics.getX() + stoneSize / 2 - (targetX + targetRadius), 2) +
            Math.pow(stonePhysics.getY() + stoneSize / 2 - (targetY + targetRadius), 2));

        if (distance < targetRadius / 2) {
            score += 10;
        } else if (distance < targetRadius * 3 / 4) {
            score += 5;
        } else {
            score += 2;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        stage.dispose();
        textureManager.dispose();
        targetTexture.dispose();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}

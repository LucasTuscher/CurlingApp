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
import com.curlingapp.game.utils.GameTimerUtils;
import com.badlogic.gdx.utils.Timer;
import com.curlingapp.game.utils.SaveGameUtils;
import com.curlingapp.game.physics.gamesensorphysics.GameSensorModiStonePhysics;
import com.curlingapp.game.render.TextureManager;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.Texture;
import com.curlingapp.game.sensors.SensorManager;
import com.curlingapp.game.settings.gamesensor.GameSensorModiSettings;
import java.util.ArrayList;
import java.util.List;

public class GameSensorModiScreen implements Screen {
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
    private List<GameSensorModiStonePhysics> stones;
    private List<Boolean> stonesShot;
    private Stage stage;
    private TextureManager textureManager;
    private Texture targetTexture;
    private List<Texture> stoneTextures;

    private SensorManager sensorManager;
    private GameSensorModiSettings settings;

    private float stoneSpeed = 500; // Anpassbar
    private static final float MAX_SPEED_FOR_SCORE = 20f; // Maximale Geschwindigkeit für Punkte

    public GameSensorModiScreen(CurlingAppClass game, GameSensorModiSettings settings) {
        this.game = game;
        this.settings = settings;

        Gdx.app.log("GameSensorModiScreen", "Difficulty: " + settings.getDifficulty());
        Gdx.app.log("GameSensorModiScreen", "Stone Size: " + settings.getStoneSize());
        Gdx.app.log("GameSensorModiScreen", "Time Limit: " + settings.getTimeLimit());
        Gdx.app.log("GameSensorModiScreen", "Stone Speed Multiplier: " + settings.getStoneSpeedMultiplier());
        Gdx.app.log("GameSensorModiScreen", "Target Radius: " + settings.getTargetRadius());
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

        // Erstellt zufällig verteilte Steine beim Start
        generateRandomStones();

        gameTimer = new GameTimerUtils();
        gameTimer.scheduleTask(1, 1, new Timer.Task() {
            @Override
            public void run() {
                timeLeft--;
                if (timeLeft <= 0) {
                    gameTimer.stop();
                    SaveGameUtils.saveGameResult(new GameResult(score, System.currentTimeMillis()));
                    game.setScreen(new ResultScreen(game, score, GameMode.GameSensorModi));
                }
            }
        });

        sensorManager = new SensorManager();

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
    }

    private void generateRandomStones() {
        stones = new ArrayList<>();
        stonesShot = new ArrayList<>();
        stoneTextures = new ArrayList<>();

        // Zufallswerte für Startposition & Bewegung
        float positionSpreadX = screenWidth * 0.6f;  // Mehr Zufälligkeit in X-Richtung
        float positionSpreadY = screenHeight * 0.4f; // Mehr Zufälligkeit in Y-Richtung
        float speedFactor = 500f; // Höhere Startgeschwindigkeit für mehr Varianz
        float rotationFactor = 50f; // Stärkere Startrotation für mehr Zufälligkeit
        float frictionVariation = 0.03f; // Unterschiedliche Reibung für jeden Stein
        float driftFactor = 15f; // Zusätzliche zufällige Drift-Richtung

        for (int i = 0; i < 5; i++) {
            // Zufällige Position innerhalb des Startbereichs
            float randomOffsetX = (float) (Math.random() * positionSpreadX - positionSpreadX / 2);
            float randomOffsetY = (float) (Math.random() * positionSpreadY - positionSpreadY / 2);

            // Zufällige individuelle Reibung für jeden Stein
            float stoneFriction = FRICTION - (float) (Math.random() * frictionVariation);

            GameSensorModiStonePhysics stone = new GameSensorModiStonePhysics(
                screenWidth / 2f - stoneSize / 2f + randomOffsetX,
                screenHeight / 2f - stoneSize / 2f - 750 + (i * 180) + randomOffsetY,
                stoneSize, stoneFriction, ROTATION_FACTOR
            );

            // Zufällige Startgeschwindigkeit in verschiedene Richtungen
            float randomVelocityX = (float) (Math.random() * speedFactor - speedFactor / 2);
            float randomVelocityY = (float) (Math.random() * speedFactor - speedFactor / 2);
            float randomRotation = (float) (Math.random() * rotationFactor - rotationFactor / 2);

            // Zufällige Drift hinzufügen, um gleichmäßige Muster zu verhindern
            float driftX = (float) (Math.random() * driftFactor - driftFactor / 2);
            float driftY = (float) (Math.random() * driftFactor - driftFactor / 2);

            stone.setVelocity(randomVelocityX + driftX, randomVelocityY + driftY);
            stone.setRotation(randomRotation);

            stones.add(stone);
            stonesShot.add(false);
            stoneTextures.add(textureManager.getRandomStoneTexture());
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sensorManager.update();

        float pitch = sensorManager.getPitch();
        float roll = sensorManager.getRoll();

        // Zufällige Variationen anwenden
        for (GameSensorModiStonePhysics stone : stones) {
            stone.applyRandomVariation(0.05f, 0.01f);
        }

        for (int i = 0; i < stones.size(); i++) {
            updateStoneMovement(stones.get(i), pitch, roll, delta);
            stones.get(i).update(delta, screenWidth, screenHeight, 0, 0, 0); // accelX, accelY, gyroZ werden nicht mehr direkt genutzt.
            checkStoneStoppedAndReset(i);
        }

        // Kollisionserkennung
        checkCollisions();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.setColor(0.2f, 0.2f, 0.2f, 1f);
        batch.draw(textureManager.getPixelTexture(), 0, screenHeight - 150, screenWidth, 150);
        batch.setColor(Color.WHITE);

        batch.draw(textureManager.getFieldTexture(), 0, 0, screenWidth, screenHeight - 150);
        batch.draw(targetTexture, targetX, targetY, targetRadius * 2, targetRadius * 2);

        for (int i = 0; i < stones.size(); i++) {
            batch.draw(stoneTextures.get(i), stones.get(i).getX(), stones.get(i).getY(), stoneSize, stoneSize);
        }

        font = FontLoader.getInterTightFont(44, Color.WHITE, FontLoader.FontStyle.BOLD_ITALIC);

        GlyphLayout glyphLayout = new GlyphLayout();

        glyphLayout.setText(font, "Punkte: " + score);
        float punkteWidth = glyphLayout.width;

        glyphLayout.setText(font, "Zeit: " + timeLeft);
        float zeitWidth = glyphLayout.width;

        // Modus-Text und dessen Farbe dynamisch anpassen
        glyphLayout.setText(font, "Modus: GameSensor");
        float modusWidth = glyphLayout.width;

        // Berechne die Positionen für den Text
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

        // Text rendern
        font.draw(batch, "Punkte: " + score, punkteX, textY);
        font.draw(batch, "Zeit: " + timeLeft, zeitX, textY);
        font.draw(batch, "Modus: GameSensor", modusX, textY);

        batch.end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private void updateStoneMovement(GameSensorModiStonePhysics stone, float pitch, float roll, float delta) {
        if (!stonesShot.get(stones.indexOf(stone))) {
            float velocityX = stoneSpeed * (float) Math.sin(Math.toRadians(roll));
            float velocityY = -stoneSpeed * (float) Math.sin(Math.toRadians(pitch));
            stone.setVelocity(velocityX, velocityY);
        }
    }

    private void checkCollisions() {
        float restitution = 0.9f; // Restitutionskoeffizient (Energieverlust bei Kollision)

        for (int i = 0; i < stones.size(); i++) {
            for (int j = i + 1; j < stones.size(); j++) {
                GameSensorModiStonePhysics stone1 = stones.get(i);
                GameSensorModiStonePhysics stone2 = stones.get(j);

                float distance = (float) Math.sqrt(Math.pow(stone1.getX() - stone2.getX(), 2) + Math.pow(stone1.getY() - stone2.getY(), 2));

                if (distance < stoneSize) {
                    // Kollision liegt vor, passe die Positionen und Geschwindigkeiten der Steine an
                    float overlap = stoneSize - distance;
                    float angle = (float) Math.atan2(stone2.getY() - stone1.getY(), stone2.getX() - stone1.getX());

                    // Berechne die Verschiebung für jeden Stein
                    float pushX = overlap / 2 * (float) Math.cos(angle);
                    float pushY = overlap / 2 * (float) Math.sin(angle);

                    // Verschiebe die Steine
                    stone1.setX(stone1.getX() - pushX);
                    stone1.setY(stone1.getY() - pushY);
                    stone2.setX(stone2.getX() + pushX);
                    stone2.setY(stone2.getY() + pushY);

                    // Berechne die relative Geschwindigkeit
                    float relVelX = stone1.getVelocityX() - stone2.getVelocityX();
                    float relVelY = stone1.getVelocityY() - stone2.getVelocityY();

                    // Berechne die neue Geschwindigkeit nach der Kollision (Impulserhaltung)
                    float normalX = (float) Math.cos(angle);
                    float normalY = (float) Math.sin(angle);

                    float relVelNormal = relVelX * normalX + relVelY * normalY;

                    if (relVelNormal < 0) {
                        float impulse = -(1 + restitution) * relVelNormal / 2;

                        stone1.setVelocity(stone1.getVelocityX() + impulse * normalX, stone1.getVelocityY() + impulse * normalY);
                        stone2.setVelocity(stone2.getVelocityX() - impulse * normalX, stone2.getVelocityY() - impulse * normalY);
                    }
                }
            }
        }
    }

    private void checkStoneStoppedAndReset(int stoneIndex) {
        GameSensorModiStonePhysics stone = stones.get(stoneIndex);
        float velocityMagnitude = (float) Math.sqrt(Math.pow(stone.getVelocityX(), 2) + Math.pow(stone.getVelocityY(), 2));

        // Prüfen, ob der Stein im Zielbereich ist
        boolean stoneIsInTarget = isStoneInsideTarget(stone);

        // Stein muss im Zielbereich sein UND unter der maximalen Geschwindigkeit bleiben
        if (stoneIsInTarget && velocityMagnitude < MAX_SPEED_FOR_SCORE && !stonesShot.get(stoneIndex)) {
            stonesShot.set(stoneIndex, true);
            calculateScore();  // Keine Übergabe von Parametern mehr
            resetStones();
        }
    }

    private void resetStones() {
        generateRandomStones(); // Nutze die zufällige Platzierung auch beim Reset
    }

    private boolean isStoneInsideTarget(GameSensorModiStonePhysics stone) {
        float stoneCenterX = stone.getX() + stoneSize / 2;
        float stoneCenterY = stone.getY() + stoneSize / 2;

        // Zielradius um 25% vergrößern
        float expandedTargetRadius = targetRadius * 1.25f;

        float distance = (float) Math.sqrt(Math.pow(stoneCenterX - (targetX + targetRadius), 2) +
            Math.pow(stoneCenterY - (targetY + targetRadius), 2));

        return distance < expandedTargetRadius;
    }

    /*
        Kein Punkteabzug, wenn mehrere Steine im Kreis sind
        Jeder Stein bekommt volle Punkte basierend auf seiner Distanz zur Mitte
        Je näher an der Mitte, desto mehr Punkte bekommt ein Stein
     */
    private void calculateScore() {
        int stonesInTarget = 0;  // Zählt die Anzahl der Steine im Zielkreis
        int totalScore = 0;  // Gesamtpunkte für alle Steine im Kreis

        // Gehe alle Steine durch und berechne die Punkte individuell
        for (GameSensorModiStonePhysics stone : stones) {
            float stoneCenterX = stone.getX() + stoneSize / 2;
            float stoneCenterY = stone.getY() + stoneSize / 2;

            // Berechnet die Entfernung zur Mitte des Kreises
            float distance = (float) Math.sqrt(Math.pow(stoneCenterX - (targetX + targetRadius), 2) +
                Math.pow(stoneCenterY - (targetY + targetRadius), 2));

            int accuracyBonus;
            if (distance < targetRadius * 0.5f) {
                accuracyBonus = 50;  // Höchster Bonus für exakte Mitte
            } else if (distance < targetRadius * 0.75f) {
                accuracyBonus = 30;  // Mittlerer Bonus für gute Platzierung
            } else if (distance < targetRadius) {
                accuracyBonus = 15;  // Kleiner Bonus für das Treffen des Zielbereichs
            } else {
                accuracyBonus = 0;   // Keine Punkte außerhalb des Kreises
            }

            if (accuracyBonus > 0) {
                stonesInTarget++;  // Erhöhe den Zähler für Steine im Kreis
                totalScore += accuracyBonus;  // Addiere die Punkte für diesen Stein
            }
        }

        // **Multiplikator für mehr Steine im Kreis**
        // - 1 Stein → Normale Punkte
        // - 2 Steine → 1.2x Punkte
        // - 3 Steine → 1.5x Punkte
        // - 4 Steine → 1.8x Punkte
        // - 5 Steine → 2.0x Punkte
        float multiplier = 1f + (stonesInTarget - 1) * 0.2f;
        totalScore *= multiplier;

        // **Gesamtpunkte hinzufügen**
        score += totalScore;
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
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}

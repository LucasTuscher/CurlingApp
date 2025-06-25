package com.curlingapp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.curlingapp.game.fontloader.FontLoader;
import com.curlingapp.game.gamemodes.GameMode;
import com.curlingapp.game.screens.GameSensorModiScreen;
import com.curlingapp.game.screens.GameTouchModiScreen;
import com.curlingapp.game.settings.Difficulty;
import com.curlingapp.game.settings.gamesensor.GameSensorModiSettings;
import com.curlingapp.game.settings.gametouch.GameTouchModiSettings;

public class CountdownScreen implements Screen {
    private final CurlingAppClass game;
    private Stage stage;
    private BitmapFont font;
    private int countdown = 3;
    private boolean useSensorControl;
    private GameMode gameMode;
    private Texture backgroundTexture;
    private GameTouchModiSettings gameTouchModiSettings;
    private GameSensorModiSettings gameSensorModiSettings;

    public CountdownScreen(CurlingAppClass game, boolean useSensorControl, int countdownTime, GameMode gameMode) {
        this.game = game;
        this.useSensorControl = useSensorControl;
        this.gameMode = gameMode;
        this.countdown = countdownTime;
    }

    public CountdownScreen(CurlingAppClass game, boolean useSensorControl, int countdownTime, GameMode gameMode, GameTouchModiSettings settings) {
        this.game = game;
        this.useSensorControl = useSensorControl;
        this.gameMode = gameMode;
        this.countdown = countdownTime;
        this.gameTouchModiSettings = settings;
    }
    public CountdownScreen(CurlingAppClass game, boolean useSensorControl, int countdownTime, GameMode gameMode, GameSensorModiSettings settings) {
        this.game = game;
        this.useSensorControl = useSensorControl;
        this.gameMode = gameMode;
        this.countdown = countdownTime;
        this.gameSensorModiSettings = settings;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("textures/Hintergrund.png"));

        Image backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        backgroundImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(backgroundImage);

        font = FontLoader.getInterTightFont(210, new Color(0x3E4557FF), FontLoader.FontStyle.BOLD_ITALIC);
        final Label countdownLabel = new Label(String.valueOf(countdown), new Label.LabelStyle(font, new Color(0x3E4557FF)));

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(countdownLabel);
        stage.addActor(table);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                countdown--;
                countdownLabel.setText(String.valueOf(countdown));
                if (countdown == 0) {
                    if (gameMode == GameMode.GameTouchModi) {
                        if (gameTouchModiSettings != null) {
                            game.setScreen(new GameTouchModiScreen(game, gameTouchModiSettings));
                        } else {
                            game.setScreen(new GameTouchModiScreen(game, new GameTouchModiSettings(Difficulty.NORMAL)));
                        }
                    } else if (gameMode == GameMode.GameSensorModi) {
                        if (gameSensorModiSettings != null) {
                            game.setScreen(new GameSensorModiScreen(game, gameSensorModiSettings));
                        } else {
                            game.setScreen(new GameSensorModiScreen(game, new GameSensorModiSettings(Difficulty.NORMAL)));
                        }
                    }
                    cancel();
                }
            }
        }, 1, 1, 3);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
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

    @Override
    public void dispose() {
        stage.dispose();
        FontLoader.dispose();
        backgroundTexture.dispose();
    }
}

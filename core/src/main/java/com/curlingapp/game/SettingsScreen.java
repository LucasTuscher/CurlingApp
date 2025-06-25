package com.curlingapp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.curlingapp.game.deviceidsystem.DeviceIdManager;
import com.curlingapp.game.fontloader.FontLoader;
import com.curlingapp.game.gamemodes.GameMode;
import com.curlingapp.game.settings.Difficulty;
import com.curlingapp.game.settings.gamesensor.GameSensorModiSettings;
import com.curlingapp.game.settings.gametouch.GameTouchModiSettings;
import com.curlingapp.game.wirelesscommunication.MyNetwork;

public class SettingsScreen implements Screen {
    private final CurlingAppClass game;
    private GameMode gameMode;
    private Stage stage;
    private BitmapFont font;
    private BitmapFont buttonFont;
    private Texture backgroundTexture, buttonTexture;
    private TextButton backButton;
    private TextButton startButton;
    private Label difficultyLabel;
    private Difficulty selectedDifficulty = Difficulty.NORMAL;
    private Label nameLabel;
    private Color difficultyLabelColor = new Color(0x3E4557FF); // Standardfarbe für den Schwierigkeitslabel

    public SettingsScreen(CurlingAppClass game, GameMode gameMode) {
        this.game = game;
        this.gameMode = gameMode;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("textures/Hintergrund.png"));
        buttonTexture = new Texture(Gdx.files.internal("textures/ButtonBlue.png"));

        font = FontLoader.getInterTightFont(60, Color.WHITE, FontLoader.FontStyle.BOLD);
        buttonFont = FontLoader.getInterTightFont(60, Color.WHITE, FontLoader.FontStyle.BOLD);

        setupUI();

        String deviceId = DeviceIdManager.getDeviceId();
        new MyNetwork().registerDevice(deviceId, new MyNetwork.NameCallback() {
            @Override
            public void onNameReceived(String name) {
                Gdx.app.postRunnable(() -> nameLabel.setText(name != null ? "Dein Name: " + name + "" : "Kein Name empfangen."));
            }
            @Override
            public void onError(Throwable t) {
                Gdx.app.postRunnable(() -> nameLabel.setText("Fehler: " + t.getMessage()));
            }
        });
    }

    private void setupUI() {
        Image backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        backgroundImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(backgroundImage);

        BitmapFont topLeftFont = FontLoader.getInterTightFont(43, new Color(0x3E4557FF), FontLoader.FontStyle.BOLD);
        nameLabel = new Label("Lade Name...", new Label.LabelStyle(topLeftFont, new Color(0x3E4557FF)));

        Table topLeftTable = new Table();
        topLeftTable.top().left().setFillParent(true);
        topLeftTable.add(nameLabel).align(Align.left).padLeft(50).padTop(115).row();

        BitmapFont titleFont = FontLoader.getInterTightFont(118, new Color(0x3E4557FF), FontLoader.FontStyle.EXTRA_BOLD);
        Label titleLabel = new Label("Einstellungen", new Label.LabelStyle(titleFont, new Color(0x3E4557FF)));
        topLeftTable.add(titleLabel).padTop(0).padLeft(50).row();

        stage.addActor(topLeftTable);

        TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonTexture));

        startButton = new TextButton("Spiel starten", buttonStyle);
        startButton.setSize(536, 168);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameMode == GameMode.GameTouchModi) {
                    GameTouchModiSettings touchSettings = new GameTouchModiSettings(selectedDifficulty);
                    game.setScreen(new CountdownScreen(game, false, 3, gameMode, touchSettings));
                } else if (gameMode == GameMode.GameSensorModi) {
                    GameSensorModiSettings sensorSettings = new GameSensorModiSettings(selectedDifficulty);
                    game.setScreen(new CountdownScreen(game, false, 3, gameMode, sensorSettings));
                }
            }
        });

        backButton = new TextButton("Zurück", buttonStyle);
        backButton.setSize(536, 168);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SelectionScreen(game));
            }
        });

        difficultyLabel = new Label("Schwierigkeit: " + selectedDifficulty.name() + " (klick)",
            new Label.LabelStyle(font, getColorForDifficulty(selectedDifficulty)));
        difficultyLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switch (selectedDifficulty) {
                    case EASY:
                        selectedDifficulty = Difficulty.NORMAL;
                        break;
                    case NORMAL:
                        selectedDifficulty = Difficulty.HARD;
                        break;
                    case HARD:
                        selectedDifficulty = Difficulty.EASY;
                        break;
                }
                difficultyLabel.setText("Schwierigkeit: " + selectedDifficulty.name() + " (klick)");
                difficultyLabel.getStyle().fontColor = getColorForDifficulty(selectedDifficulty);
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(new Label("Einstellungen für: " + gameMode.name(), new Label.LabelStyle(font, new Color(0x3E4557FF)))).padBottom(72).row();
        table.add(difficultyLabel).padBottom(30).row();
        table.add(startButton).size(536, 168).padBottom(30).padTop(70).row();
        table.add(backButton).size(536, 168).padBottom(30).row();

        stage.addActor(table);
    }

    private Color getColorForDifficulty(Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return Color.GREEN;
            case NORMAL:
                return Color.ORANGE;
            case HARD:
                return Color.RED;
            default:
                return Color.WHITE;
        }
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
        buttonTexture.dispose();
    }
}

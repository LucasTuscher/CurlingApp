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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.curlingapp.game.deviceidsystem.DeviceIdManager;
import com.curlingapp.game.fontloader.FontLoader;
import com.curlingapp.game.gamemodes.GameMode;
import com.curlingapp.game.variables.versionText;
import com.curlingapp.game.wirelesscommunication.MyNetwork;

public class SelectionScreen implements Screen {
    private final CurlingAppClass game;
    private Stage stage;
    private BitmapFont font;
    private BitmapFont buttonFont;
    private Texture backgroundTexture, buttonTexture;
    private Label nameLabel;

    public SelectionScreen(CurlingAppClass game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("textures/Hintergrund.png"));
        buttonTexture = new Texture(Gdx.files.internal("textures/ButtonBlue.png"));

        font = FontLoader.getInterTightFont(52, Color.WHITE, FontLoader.FontStyle.BOLD);
        buttonFont = FontLoader.getInterTightFont(60, Color.WHITE, FontLoader.FontStyle.BOLD);

        setupUI();

        // Geräte-ID abrufen und Name setzen
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

        BitmapFont topLeftFont = FontLoader.getInterTightFont(46, new Color(0x3E4557FF), FontLoader.FontStyle.BOLD);
        nameLabel = new Label("Lade Name...", new Label.LabelStyle(topLeftFont, new Color(0x3E4557FF)));

        Table topLeftTable = new Table();
        topLeftTable.top().left().setFillParent(true);
        topLeftTable.add(nameLabel).align(Align.left).padLeft(50).padTop(115).row();

        BitmapFont titleFont = FontLoader.getInterTightFont(118, new Color(0x3E4557FF), FontLoader.FontStyle.EXTRA_BOLD);
        Label titleLabel = new Label("Auswahl Menü", new Label.LabelStyle(titleFont, new Color(0x3E4557FF)));
        topLeftTable.add(titleLabel).padTop(0).padLeft(50).row();

        stage.addActor(topLeftTable);

        // Neue Tabelle für die Version, die unten rechts angezeigt wird
        Table bottomRightTable = new Table();
        bottomRightTable.bottom().right().setFillParent(true); // Unten rechts
        BitmapFont versionFont = FontLoader.getInterTightFont(42, new Color(0x3E4557FF), FontLoader.FontStyle.SEMI_BOLD);
        Label versionLabel = new Label(versionText.appVersion, new Label.LabelStyle(versionFont, new Color(0x3E4557FF)));
        bottomRightTable.add(versionLabel).padBottom(39).padRight(54); // Rechts und unten

        stage.addActor(bottomRightTable);

        // UI für die Buttons
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonTexture));

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        TextButton startSinglePlayerButton = new TextButton("GameTouch", buttonStyle);
        startSinglePlayerButton.setSize(536, 168);
        startSinglePlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game, GameMode.GameTouchModi));
            }
        });

        TextButton startMultiPlayerButton = new TextButton("GameSensor", buttonStyle);
        startMultiPlayerButton.setSize(536, 168);
        startMultiPlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game, GameMode.GameSensorModi));
            }
        });

        TextButton leaderboardButton = new TextButton("Leaderboard", buttonStyle);
        leaderboardButton.setSize(536, 168);
        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LeaderboardScreen(game));
            }
        });

        TextButton instructionButton = new TextButton("Anleitung", buttonStyle);
        instructionButton.setSize(536, 168);
        instructionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new InstructionScreen(game));
            }
        });

        TextButton backButton = new TextButton("Zurück", buttonStyle);
        backButton.setSize(536, 168);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        table.add(startSinglePlayerButton).size(536, 168).padBottom(50).row();
        table.add(startMultiPlayerButton).size(536, 168).padBottom(50).row();
        table.add(leaderboardButton).size(536, 168).padBottom(50).row();
        table.add(instructionButton).size(536, 168).padBottom(50).row();
        table.add(backButton).size(536, 168).row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
        backgroundTexture.dispose();
        buttonTexture.dispose();
    }
}

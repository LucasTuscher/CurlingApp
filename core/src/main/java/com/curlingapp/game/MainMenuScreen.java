package com.curlingapp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.curlingapp.game.deviceidsystem.DeviceIdManager;
import com.curlingapp.game.fontloader.FontLoader;
import com.curlingapp.game.variables.versionText;
import com.curlingapp.game.wirelesscommunication.MyNetwork;

class MainMenuScreen implements Screen {
    private final CurlingAppClass game;
    private Stage stage;
    private BitmapFont font;
    private BitmapFont buttonFont;
    private Texture buttonTexture, backgroundTexture;
    private Label connectionStatusLabel, nameLabel;

    public MainMenuScreen(CurlingAppClass game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("textures/Hintergrund.png"));
        buttonTexture = new Texture(Gdx.files.internal("textures/ButtonBlue.png"));

        FontLoader.dispose(); // Fonts-Cache leeren
        loadFont(); // Neu laden
        setupUI();
        setupNetwork();
    }

    private void loadFont() {
        font = FontLoader.getInterTightFont(54, Color.WHITE, FontLoader.FontStyle.BOLD);
        buttonFont = FontLoader.getInterTightFont(60, Color.WHITE, FontLoader.FontStyle.BOLD);
        if (font == null) font = new BitmapFont();
    }

    private void setupUI() {
        Image backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        backgroundImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(backgroundImage);

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonTexture));

        TextButton startButton = new TextButton("Start", buttonStyle);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SelectionScreen(game));
            }
        });

        TextButton creditsButton = new TextButton("Credits", buttonStyle);
        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CreditsScreen(game));
            }
        });

        TextButton exitButton = new TextButton("Beenden", buttonStyle);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        BitmapFont titleFont = FontLoader.getInterTightFont(152, new Color(0x3E4557FF), FontLoader.FontStyle.EXTRA_BOLD_ITALIC);
        Label titleLabel = new Label("Curling App", new Label.LabelStyle(titleFont, new Color(0x3E4557FF)));
        table.add(titleLabel).padBottom(34).row();

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, new Color(0x3E4557FF));
        nameLabel = new Label("", labelStyle);
        table.add(nameLabel).padBottom(85).row();

        table.add(startButton).size(536, 168).padBottom(52).row();
        table.add(creditsButton).size(536, 168).padBottom(52).row();
        table.add(exitButton).size(536, 168).row();

        connectionStatusLabel = new Label("Verbindung wird überprüft...", labelStyle);
        Table statusTable = new Table();
        statusTable.top().setFillParent(true);
        statusTable.add(connectionStatusLabel).padTop(32).center().row();

        BitmapFont versionFont = FontLoader.getInterTightFont(42, new Color(0x3E4557FF), FontLoader.FontStyle.SEMI_BOLD);
        Label versionLabel = new Label(versionText.appVersion, new Label.LabelStyle(versionFont, new Color(0x3E4557FF)));
        Table versionTable = new Table();
        versionTable.bottom().right().setFillParent(true);
        versionTable.add(versionLabel).padBottom(39).padRight(54);

        stage.addActor(table);
        stage.addActor(statusTable);
        stage.addActor(versionTable);
    }

    private void setupNetwork() {
        String deviceId = DeviceIdManager.getDeviceId();
        new MyNetwork().registerDevice(deviceId, new MyNetwork.NameCallback() {
            @Override
            public void onNameReceived(String name) {
                Gdx.app.postRunnable(() -> nameLabel.setText(name != null ? "Hallo, " + name + "!" : "Kein Name empfangen."));
            }
            @Override
            public void onError(Throwable t) {
                Gdx.app.postRunnable(() -> nameLabel.setText("Fehler: " + t.getMessage()));
            }
        });

        new MyNetwork().checkConnection(new MyNetwork.ConnectionCallback() {
            @Override
            public void onConnectionResult(String response) {
                Gdx.app.postRunnable(() -> connectionStatusLabel.setText(response.contains("connected") ? "Internet Verbindung erfolgreich!" : "MySQL Verbindung fehlgeschlagen!"));
            }
            @Override
            public void onError(Throwable t) {
                Gdx.app.postRunnable(() -> connectionStatusLabel.setText("Verbindungsfehler: " + t.getMessage()));
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
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
        buttonTexture.dispose();
        backgroundTexture.dispose();
        if (font != null) font.dispose();
        if (buttonFont != null) buttonFont.dispose();
        FontLoader.dispose(); // Fonts korrekt freigeben
    }
}

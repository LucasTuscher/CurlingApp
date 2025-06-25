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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.curlingapp.game.deviceidsystem.DeviceIdManager;
import com.curlingapp.game.fontloader.FontLoader;
import com.curlingapp.game.gamemodes.GameMode;
import com.curlingapp.game.wirelesscommunication.MyNetwork;

public class ResultScreen implements Screen {
    private final CurlingAppClass game;
    private int localScore;
    private int remoteScore;
    private GameMode gameMode;
    private Stage stage;
    private Texture backgroundTexture, buttonTexture;
    private BitmapFont font;
    private BitmapFont buttonFont;
    private BitmapFont scoreFont;
    private MyNetwork myNetwork;

    public ResultScreen(CurlingAppClass game, int localScore, GameMode gameMode) {
        this(game, localScore, -1, gameMode);
    }

    public ResultScreen(CurlingAppClass game, int localScore, int remoteScore, GameMode gameMode) {
        this.game = game;
        this.localScore = localScore;
        this.remoteScore = remoteScore;
        this.gameMode = gameMode;
        this.myNetwork = new MyNetwork();
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("textures/Hintergrund.png"));
        buttonTexture = new Texture(Gdx.files.internal("textures/ButtonBlue.png"));

        scoreFont = FontLoader.getInterTightFont(110, Color.WHITE, FontLoader.FontStyle.BOLD);
        font = FontLoader.getInterTightFont(82, Color.WHITE, FontLoader.FontStyle.BOLD);
        buttonFont = FontLoader.getInterTightFont(72, Color.WHITE, FontLoader.FontStyle.BOLD);

        setupUI();
        saveGameResult();
    }

    private void setupUI() {
        Image backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        backgroundImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(backgroundImage);

        TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonTexture));

        Label scoreLabel = new Label("Dein Score: " + localScore, new Label.LabelStyle(scoreFont, new Color(0x3E4557FF)));

        String resultText = (localScore >= 1) ? "Du hast gewonnen!" : "Du hast verloren!";
        Color resultColor = (localScore >= 1) ? Color.GREEN : Color.RED;
        Label resultLabel = new Label(resultText, new Label.LabelStyle(font, resultColor));

        TextButton backButton = new TextButton("Zurück", buttonStyle);
        backButton.setSize(536, 168);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SelectionScreen(game));
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(scoreLabel).padBottom(50).row();
        table.add(resultLabel).padBottom(250).row();
        table.add(backButton).size(536, 168).padBottom(30).row();

        stage.addActor(table);
    }

    private void saveGameResult() {
        String deviceId = DeviceIdManager.getDeviceId();
        myNetwork.saveGameResult(deviceId, localScore, gameMode.name(), new MyNetwork.ResultCallback() {
            @Override
            public void onResultSaved() {
                Gdx.app.log("ResultScreen", "Spielergebnis erfolgreich gespeichert.");
            }

            @Override
            public void onError(Throwable t) {
                Gdx.app.error("ResultScreen", "Fehler beim Speichern des Ergebnisses: " + t.getMessage());
            }
        });
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

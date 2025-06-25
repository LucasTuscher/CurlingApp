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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.curlingapp.game.deviceidsystem.DeviceIdManager;
import com.badlogic.gdx.utils.Align;
import com.curlingapp.game.fontloader.FontLoader;
import com.curlingapp.game.wirelesscommunication.MyNetwork;

import java.util.Map;

public class LeaderboardScreen implements Screen {
    private final CurlingAppClass game;
    private Stage stage;
    private BitmapFont font;
    private BitmapFont buttonFont;
    private Texture buttonTexture, backgroundTexture;
    private Label nameLabel;

    public LeaderboardScreen(CurlingAppClass game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        font = FontLoader.getInterTightFont(52, Color.WHITE, FontLoader.FontStyle.BOLD);
        buttonFont = FontLoader.getInterTightFont(60, Color.WHITE, FontLoader.FontStyle.BOLD);

        backgroundTexture = new Texture(Gdx.files.internal("textures/Hintergrund.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        Image backgroundImage = new Image(backgroundDrawable);
        backgroundImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(backgroundImage);

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
        BitmapFont topLeftFont = FontLoader.getInterTightFont(46, new Color(0x3E4557FF), FontLoader.FontStyle.BOLD);
        nameLabel = new Label("Lade Name...", new Label.LabelStyle(topLeftFont, new Color(0x3E4557FF)));

        Table topLeftTable = new Table();
        topLeftTable.top().left().setFillParent(true);
        topLeftTable.add(nameLabel).align(Align.left).padLeft(50).padTop(115).row();

        BitmapFont titleFont = FontLoader.getInterTightFont(118, new Color(0x3E4557FF), FontLoader.FontStyle.EXTRA_BOLD);
        Label titleLabel = new Label("Leaderboard", new Label.LabelStyle(titleFont, new Color(0x3E4557FF)));
        topLeftTable.add(titleLabel).padTop(0).padLeft(50).row();

        stage.addActor(topLeftTable);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        Table leaderboardTable = new Table();
        leaderboardTable.defaults().pad(5);

        MyNetwork network = new MyNetwork();
        network.getLeaderboard(new MyNetwork.LeaderboardCallback() {
            @Override
            public void onLeaderboardReceived(Array<Map<String, String>> leaderboard) {
                if (leaderboard != null) {
                    int platz = 1;
                    for (Map<String, String> entry : leaderboard) {
                        if (entry != null) {
                            String name = entry.get("name");
                            String score = entry.get("best_score");
                            if (name != null && score != null) {
                                Label platzLabel = new Label("Platz " + platz + ". ", new Label.LabelStyle(font, new Color(0x3E4557FF)));
                                Label nameLabel = new Label("Name: " + name, new Label.LabelStyle(font, new Color(0x3E4557FF)));
                                Label scoreLabel = new Label("  Score: " + score, new Label.LabelStyle(font, new Color(0x3E4557FF)));
                                leaderboardTable.add(platzLabel);
                                leaderboardTable.add(nameLabel);
                                leaderboardTable.add(scoreLabel).row();
                                platz++;
                            } else {
                                Gdx.app.error("LeaderboardScreen", "Entry has missing fields.");
                            }
                        } else {
                            Gdx.app.error("LeaderboardScreen", "Entry is null.");
                        }
                    }
                } else {
                    Gdx.app.error("LeaderboardScreen", "Leaderboard is null.");
                }
            }

            @Override
            public void onError(Throwable t) {
                Label errorLabel = new Label("Error loading leaderboard.", new Label.LabelStyle(font, Color.RED));
                leaderboardTable.add(errorLabel).row();
                Gdx.app.error("LeaderboardScreen", "Error loading leaderboard: " + t.getMessage());
            }
        });

        ScrollPane scrollPane = new ScrollPane(leaderboardTable);
        scrollPane.setFadeScrollBars(false);
        mainTable.add(scrollPane).expand().fill().row();

        buttonTexture = new Texture(Gdx.files.internal("textures/ButtonBlue.png"));
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = buttonDrawable;
        buttonStyle.down = buttonDrawable;

        TextButton backButton = new TextButton("Zurück", buttonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SelectionScreen(game));
            }
        });
        mainTable.add(backButton).size(536, 168).padBottom(80).row();

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 1, 1);
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
        buttonTexture.dispose();
        backgroundTexture.dispose();
    }
}

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
import com.curlingapp.game.variables.versionText;
import com.curlingapp.game.wirelesscommunication.MyNetwork;

public class InstructionScreen implements Screen {
    private final CurlingAppClass game;
    private Stage stage;
    private BitmapFont font;
    private BitmapFont buttonFont;
    private Texture backgroundTexture, buttonTexture;
    private Label nameLabel;

    public InstructionScreen(CurlingAppClass game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("textures/Hintergrund.png"));
        buttonTexture = new Texture(Gdx.files.internal("textures/ButtonBlue.png"));

        font = FontLoader.getInterTightFont(63, new Color(0x3E4557FF), FontLoader.FontStyle.BOLD_ITALIC);
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

        BitmapFont topLeftFont = FontLoader.getInterTightFont(46, new Color(0x3E4557FF), FontLoader.FontStyle.BOLD);
        nameLabel = new Label("Lade Name...", new Label.LabelStyle(topLeftFont, new Color(0x3E4557FF)));

        Table topLeftTable = new Table();
        topLeftTable.top().left().setFillParent(true);
        topLeftTable.add(nameLabel).align(Align.left).padLeft(50).padTop(115).row();

        BitmapFont titleFont = FontLoader.getInterTightFont(118, new Color(0x3E4557FF), FontLoader.FontStyle.EXTRA_BOLD);
        Label titleLabel = new Label("Anleitung", new Label.LabelStyle(titleFont, new Color(0x3E4557FF)));
        topLeftTable.add(titleLabel).padTop(0).padLeft(50).row();

        stage.addActor(topLeftTable);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonTexture));

        TextButton backButton = new TextButton("Zurück", buttonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SelectionScreen(game));
            }
        });

        // Vergrößerte Schriftgröße für die Beschreibungen
        BitmapFont descriptionFont = FontLoader.getInterTightFont(40, new Color(0x3E4557FF), FontLoader.FontStyle.MEDIUM);
        Label.LabelStyle labelStyle = new Label.LabelStyle(descriptionFont, new Color(0x3E4557FF));

        Label sensorModeLabel = new Label("Sensor-Modus:", labelStyle);
        sensorModeLabel.setStyle(new Label.LabelStyle(font, Color.WHITE));

        Label sensorModeDesc = new Label("Neige dein Gerät, um den Stein zu steuern. Je stärker die Neigung, desto schneller bewegt sich der Stein. Versuche, den Stein so nah wie möglich an die Mitte des Zielbereichs zu bringen. Punkte basieren auf der Nähe zur Mitte.", labelStyle);
        sensorModeDesc.setWrap(true);

        Label touchModeLabel = new Label("Touch-Modus:", labelStyle);
        touchModeLabel.setStyle(new Label.LabelStyle(font, Color.WHITE));

        Label touchModeDesc = new Label("Ziehe den Stein, um ihn zu werfen. Die Geschwindigkeit und Richtung des Wurfes hängen von deiner Bewegung ab. Nutze den Besen, um die Richtung des Steins während der Bewegung zu beeinflussen. Ziel ist präzises Befördern. Punkte basieren auf der Nähe zur Mitte.", labelStyle);
        touchModeDesc.setWrap(true);

        Label generalNotesLabel = new Label("Allgemeine Hinweise:", labelStyle);
        generalNotesLabel.setStyle(new Label.LabelStyle(font, Color.WHITE));

        Label generalNotesDesc = new Label("Beide Modi haben ein Zeitlimit. Je näher der Stein an der Mitte, desto mehr Punkte erhältst du. Die Schwierigkeit beeinflusst die Spielmechanik und die Punktvergabe. Achte auf die Zeit, um das Spiel erfolgreich abzuschließen.", labelStyle);
        generalNotesDesc.setWrap(true);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        BitmapFont versionFont = FontLoader.getInterTightFont(42, new Color(0x3E4557FF), FontLoader.FontStyle.SEMI_BOLD);
        Label versionLabel = new Label(versionText.appVersion, new Label.LabelStyle(versionFont, new Color(0x3E4557FF)));
        Table versionTable = new Table();
        versionTable.bottom().right().setFillParent(true);
        versionTable.add(versionLabel).padBottom(39).padRight(54);

        table.add(sensorModeLabel).padBottom(10).row();
        table.add(sensorModeDesc).width(Gdx.graphics.getWidth() * 0.8f).padBottom(35).row();
        table.add(touchModeLabel).padBottom(10).row();
        table.add(touchModeDesc).width(Gdx.graphics.getWidth() * 0.8f).padBottom(35).row();
        table.add(generalNotesLabel).padBottom(10).row();
        table.add(generalNotesDesc).width(Gdx.graphics.getWidth() * 0.8f).padBottom(130).row();
        table.add(backButton).size(536, 168).row();

        stage.addActor(table);
        stage.addActor(versionTable);
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

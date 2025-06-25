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
import com.curlingapp.game.fontloader.FontLoader;
import com.curlingapp.game.variables.versionText;

public class CreditsScreen implements Screen {
    private final CurlingAppClass game;
    private Stage stage;
    private BitmapFont font;
    private BitmapFont buttonFont;
    private Texture backgroundTexture, buttonTexture;

    public CreditsScreen(CurlingAppClass game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Lade die Hintergrund- und Button-Texturen
        backgroundTexture = new Texture(Gdx.files.internal("textures/Hintergrund.png"));
        buttonTexture = new Texture(Gdx.files.internal("textures/ButtonBlue.png"));

        // Lade die Schriftart für die Labels (Credits-Namen)
        font = FontLoader.getInterTightFont(72, new Color(0x3E4557FF), FontLoader.FontStyle.BOLD_ITALIC);
        buttonFont = FontLoader.getInterTightFont(50, Color.WHITE, FontLoader.FontStyle.BOLD);

        setupUI();
    }

    private void setupUI() {
        // Hintergrundbild hinzufügen
        Image backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        backgroundImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(backgroundImage);

        // Button Style für den Zurück-Button
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonTexture));

        // Zurück-Button
        TextButton backButton = new TextButton("Zurück", buttonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        // Erstelle Labels für die Credits-Namen
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label creditsLabel1 = new Label("Andreas Lucas Tuscher", labelStyle);
        Label creditsLabel2 = new Label("Marcel Deckert", labelStyle);
        Label creditsLabel3 = new Label("Anas Abazid", labelStyle);

        // UI-Elemente in einer Tabelle anordnen
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Titel-Label für Credits-Seite
        BitmapFont titleFont = FontLoader.getInterTightFont(140, new Color(0x3E4557FF), FontLoader.FontStyle.EXTRA_BOLD_ITALIC);
        Label titleLabel = new Label("Credits", new Label.LabelStyle(titleFont, new Color(0x3E4557FF)));
        table.add(titleLabel).padBottom(90).row(); // Abstand zwischen Titel und erstem Namen

        // Version Label unten rechts
        BitmapFont versionFont = FontLoader.getInterTightFont(42, new Color(0x3E4557FF), FontLoader.FontStyle.SEMI_BOLD);
        Label versionLabel = new Label(versionText.appVersion, new Label.LabelStyle(versionFont, new Color(0x3E4557FF)));
        Table versionTable = new Table();
        versionTable.bottom().right().setFillParent(true);
        versionTable.add(versionLabel).padBottom(39).padRight(54);

        // Füge die Labels und den Zurück-Button zur Tabelle hinzu
        table.add(creditsLabel1).padBottom(30).row(); // Erhöhter Abstand
        table.add(creditsLabel2).padBottom(30).row(); // Erhöhter Abstand
        table.add(creditsLabel3).padBottom(115).row(); // Deutlich erhöhter Abstand
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

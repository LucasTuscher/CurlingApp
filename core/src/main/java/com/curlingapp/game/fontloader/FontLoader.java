package com.curlingapp.game.fontloader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import java.util.HashMap;

public class FontLoader {
    public enum FontStyle {
        BLACK, BLACK_ITALIC, BOLD, BOLD_ITALIC, EXTRA_BOLD, EXTRA_BOLD_ITALIC,
        EXTRA_LIGHT, EXTRA_LIGHT_ITALIC, ITALIC, LIGHT, LIGHT_ITALIC, MEDIUM,
        MEDIUM_ITALIC, REGULAR, SEMI_BOLD, SEMI_BOLD_ITALIC, THIN, THIN_ITALIC
    }

    private static final HashMap<String, BitmapFont> fonts = new HashMap<>();

    public static BitmapFont getInterTightFont(int size, Color color, FontStyle style) {
        String key = style.name() + "_" + size + "_" + color.toString();

        if (fonts.containsKey(key)) {
            return fonts.get(key);
        }

        String fontPath = getFontPath(style);

        if (!Gdx.files.internal(fontPath).exists()) {
            Gdx.app.log("FontLoader", "Schriftart nicht gefunden: " + fontPath);
            return new BitmapFont();
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = color;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        fonts.put(key, font);
        return font;
    }

    private static String getFontPath(FontStyle style) {
        switch (style) {
            case BLACK: return "fonts/InterTight-Black.ttf";
            case BLACK_ITALIC: return "fonts/InterTight-BlackItalic.ttf";
            case BOLD: return "fonts/InterTight-Bold.ttf";
            case BOLD_ITALIC: return "fonts/InterTight-BoldItalic.ttf";
            case EXTRA_BOLD: return "fonts/InterTight-ExtraBold.ttf";
            case EXTRA_BOLD_ITALIC: return "fonts/InterTight-ExtraBoldItalic.ttf";
            case EXTRA_LIGHT: return "fonts/InterTight-ExtraLight.ttf";
            case EXTRA_LIGHT_ITALIC: return "fonts/InterTight-ExtraLightItalic.ttf";
            case ITALIC: return "fonts/InterTight-Italic.ttf";
            case LIGHT: return "fonts/InterTight-Light.ttf";
            case LIGHT_ITALIC: return "fonts/InterTight-LightItalic.ttf";
            case MEDIUM: return "fonts/InterTight-Medium.ttf";
            case MEDIUM_ITALIC: return "fonts/InterTight-MediumItalic.ttf";
            case REGULAR: return "fonts/InterTight-Regular.ttf";
            case SEMI_BOLD: return "fonts/InterTight-SemiBold.ttf";
            case SEMI_BOLD_ITALIC: return "fonts/InterTight-SemiBoldItalic.ttf";
            case THIN: return "fonts/InterTight-Thin.ttf";
            case THIN_ITALIC: return "fonts/InterTight-ThinItalic.ttf";
            default: return "fonts/InterTight-Regular.ttf";
        }
    }

    public static void dispose() {
        for (BitmapFont font : fonts.values()) {
            font.dispose();
        }
        fonts.clear();
    }
}

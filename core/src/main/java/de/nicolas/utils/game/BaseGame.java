package de.nicolas.utils.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import de.nicolas.utils.screens.BaseScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.graphics.Texture;

/**
 * wird erstellt beim Start des Programms.
 * Steuert den Wechsel zwischen den Screens
 */
public abstract class BaseGame extends Game {

    /**
     * speichert die Referenz des aktuellen Game
     */
    private static BaseGame game;

    /**
     * statische Variable für die Darstellung von Text auf Labels
     */
    public static LabelStyle labelStyle;

    /**
     * statische variable für die Darstellung eines TextButtons
     */
    public static TextButtonStyle textButtonStyle;

    public BaseGame(){
        game = this;
    }

    @Override
    public void create() {
        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor(im);

        FreeTypeFontGenerator fontGenerator = new
            FreeTypeFontGenerator(Gdx.files.internal("assets/OpenSans.ttf"));

        FreeTypeFontParameter fontParameters =new FreeTypeFontParameter();
        fontParameters.size = 48;
        fontParameters.color = Color.WHITE;
        fontParameters.borderWidth = 2;
        fontParameters.borderColor = Color.BLACK;
        fontParameters.borderStraight = true;
        fontParameters.minFilter = TextureFilter.Linear;
        fontParameters.magFilter = TextureFilter.Linear;

        BitmapFont customFont = fontGenerator.generateFont(fontParameters);

        labelStyle = new LabelStyle();
        labelStyle.font = customFont;

        textButtonStyle = new TextButtonStyle();
        Texture buttonTexture = new Texture(Gdx.files.internal("assets/button.png"));
        NinePatch buttonPatch = new NinePatch(buttonTexture, 24, 24, 24, 24);
        textButtonStyle.up = new NinePatchDrawable(buttonPatch);
        textButtonStyle.font = customFont;
        textButtonStyle.fontColor = Color.GRAY;
    }

    /**
     * wird benutzt, um zwischen den Screens zu wechseln
     * @param screen
     */
    public static void setActiveScreen(BaseScreen screen){
        game.setScreen(screen);
    }
}

package de.nicolas.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import de.nicolas.StarfishGame;
import de.nicolas.actors.*;
import de.nicolas.utils.actors.BaseActor;
import de.nicolas.utils.actors.DialogBox;
import de.nicolas.utils.actors.TileMapActor;
import de.nicolas.utils.game.BaseGame;
import de.nicolas.utils.screens.BaseScreen;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import sun.jvm.hotspot.utilities.RobustOopDeterminator;

public class LevelScreen extends BaseScreen {

    private Turtle turtle;
    private boolean win;

    private Label starfishLabel;

    private DialogBox dialogBox;

    private float audioVolume;
    private float musicVolume;
    private Sound waterDrop;
    private Music instrumental;
    private Music oceanSurf;

    @Override
    public void initialize() {

        TileMapActor tma = new TileMapActor("assets/mymap.tmx", mainStage);

        for (MapObject object : tma.getTileList("Starfish")){
            MapProperties properties = object.getProperties();
            new Starfish((float)properties.get("x"), (float)properties.get("y"), mainStage);
        }

        for (MapObject object : tma.getTileList("Rock")){
            MapProperties properties = object.getProperties();
            new Rock((float)properties.get("x"), (float)properties.get("y"), mainStage);
        }

        for (MapObject object : tma.getTileList("Sign")){
            MapProperties properties = object.getProperties();
            Sign sign =new Sign((float)properties.get("x"), (float)properties.get("y"), mainStage);
            sign.setText((String)properties.get("message"));
        }

        MapObject startPoint = tma.getRectangleList("start").get(0);
        MapProperties properties = startPoint.getProperties();
        turtle = new Turtle((float) properties.get("x"), (float) properties.get("y"), mainStage);

        win = false;

        starfishLabel = new Label("Starfish Left:", BaseGame.labelStyle);
        starfishLabel.setColor(Color.CYAN);

        ButtonStyle buttonStyle = new ButtonStyle();

        Texture buttonTex = new Texture(Gdx.files.internal("assets/undo.png"));
        TextureRegion buttonRegion = new TextureRegion(buttonTex);
        buttonStyle.up = new TextureRegionDrawable(buttonRegion);

        Button restartButton = new Button(buttonStyle);
        restartButton.setColor(Color.CYAN);

        restartButton.addListener(
            (Event e) ->{
                if (!isTouchDownEvent(e)){
                    return false;
                }
                instrumental.dispose();
                oceanSurf.dispose();

                StarfishGame.setActiveScreen(new LevelScreen());
                return false;
            }
        );

        ButtonStyle buttonStyle2 = new ButtonStyle();

        Texture buttonTex2 = new Texture(Gdx.files.internal("assets/audio.png"));
        TextureRegion buttonRegion2 = new TextureRegion(buttonTex2);
        buttonStyle2.up = new TextureRegionDrawable(buttonRegion2);

        Button muteButton = new Button(buttonStyle2);
        muteButton.setColor(Color.CYAN);

        muteButton.addListener(
            (Event e) ->{
                if (!isTouchDownEvent(e)){
                    return false;
                }
                audioVolume = 1 - audioVolume;
                instrumental.setVolume(audioVolume);
                oceanSurf.setVolume(audioVolume);

                return false;
            }
        );

        dialogBox = new DialogBox(0, 0, uiStage);
        dialogBox.setBackgroundColor(Color.TAN);
        dialogBox.setFontColor(Color.BROWN);
        dialogBox.setDialogSize(600, 100);
        dialogBox.setFontScale(0.8F);
        dialogBox.alignCenter();
        dialogBox.setVisible(false);

        uiTable.pad(10);
        uiTable.add(starfishLabel).top();
        uiTable.add().expandX().expandY();
        uiTable.add(muteButton).top();
        uiTable.add(restartButton).top();

        uiTable.row();
        uiTable.add(dialogBox).colspan(4);

        waterDrop = Gdx.audio.newSound(Gdx.files.internal("assets/Water_Drop.ogg"));
        instrumental = Gdx.audio.newMusic(Gdx.files.internal("assets/Master_of_the_Feast.ogg"));
        oceanSurf = Gdx.audio.newMusic(Gdx.files.internal("assets/Ocean_Waves.ogg"));

        audioVolume = 0.3F;
        musicVolume = 0.2f;
        instrumental.setLooping(true);
        instrumental.setVolume(musicVolume);
        instrumental.play();
        oceanSurf.setLooping(true);
        oceanSurf.setVolume(audioVolume);
        oceanSurf.play();
    }

    @Override
    public void update(float delta) {
        for (BaseActor rockActor : BaseActor.getList(mainStage, "de.nicolas.actors.Rock"))
            turtle.preventOverlap(rockActor);

        for (BaseActor starfishActor : BaseActor.getList(mainStage, "de.nicolas.actors.Starfish"))
        {
            Starfish starfish = (Starfish)starfishActor;
            if ( turtle.overlaps(starfish) && !starfish.isCollected())
            {
                starfish.setCollected(true);
                waterDrop.play(audioVolume);
                starfish.clearActions();
                starfish.addAction( Actions.fadeOut(1) );
                starfish.addAction( Actions.after( Actions.removeActor() ) );

                Whirlpool whirl = new Whirlpool(0,0, mainStage);
                whirl.centerAtActor( starfish );
                whirl.setOpacity(0.25f);
            }
        }

        if ( BaseActor.count(mainStage, "de.nicolas.actors.Starfish") == 0 && !win )
        {
            win = true;
            BaseActor youWinMessage = new BaseActor(0,0,uiStage);
            youWinMessage.loadTexture("assets/you-win.png");
            youWinMessage.centerAtPosition(400,300);
            youWinMessage.setOpacity(0);
            youWinMessage.addAction( Actions.delay(1) );
            youWinMessage.addAction( Actions.after( Actions.fadeIn(1) ) );
        }

        starfishLabel.setText("Starfish Left: " + BaseActor.count(mainStage, "de.nicolas.actors.Starfish"));

        for (BaseActor signActor : BaseActor.getList(mainStage,"de.nicolas.actors.Sign" )){
            Sign sign = (Sign)signActor;
            turtle.preventOverlap(sign);
            boolean nearby = turtle.isWithinDistance(4, sign);

            if (nearby && !sign.isViewing()){
                dialogBox.setText(sign.getText());
                dialogBox.setVisible(true);
                sign.setViewing(true);
            }
            if (sign.isViewing() && !nearby){
                dialogBox.setText(" ");
                dialogBox.setVisible(false);
                sign.setViewing(false);
            }
        }
    }

    @Override
    public boolean keyDown(int i) {return false; }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) { return false; }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) { return false; }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}

package de.nicolas.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import de.nicolas.utils.actions.Scene;
import de.nicolas.utils.actors.BaseActor;
import de.nicolas.utils.actors.DialogBox;
import de.nicolas.utils.screens.BaseScreen;

public class StoryScreen extends BaseScreen {

    private Scene scene;
    private BaseActor continueKey;

    @Override
    public void initialize() {
        BaseActor background = new BaseActor(0, 0, mainStage);
        background.loadTexture("assets/oceanside.png");
        background.setSize(800, 600);
        BaseActor.setWorldBounds(background);

        BaseActor turtle = new BaseActor(0, 0, mainStage);
        turtle.loadTexture("assets/turtle-big.png");
        turtle.setPosition(-turtle.getWidth(), 0);

        DialogBox dialogBox = new DialogBox(0, 0, uiStage);
        dialogBox.setSize(600, 200);
        dialogBox.setBackgroundColor(new Color(0.6f, 0.6f, 0.8f, 1f));
        dialogBox.setFontScale(0.75f);
        dialogBox.setVisible(false);

        uiTable.add(dialogBox). expandX().expandY().bottom();

        continueKey = new BaseActor(0, 0, uiStage);
        continueKey.loadTexture("assets/key-C.png");
        continueKey.setSize(32, 32);
        continueKey.setVisible(false);

        dialogBox.addActor(continueKey);
        continueKey.setPosition(dialogBox.getWidth(), - continueKey.getWidth(), 0);
        
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.C && continueKey.isVisible()){
            scene.loadNextSegment();
        }
        return false;
    }
}

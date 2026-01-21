package de.nicolas.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import de.nicolas.utils.actors.BaseActor;
import de.nicolas.StarfishGame;
import de.nicolas.utils.game.BaseGame;
import de.nicolas.utils.screens.BaseScreen;

public class MenuScreen extends BaseScreen {
    @Override
    public void initialize() {
        BaseActor ocean = new BaseActor(0, 0, mainStage);
        ocean.loadTexture("assets/water.jpg");
        ocean.setSize(800, 600);

        BaseActor title = new BaseActor(0, 0, mainStage);
        title.loadTexture("assets/starfish-collector.png");

        TextButton startButton = new TextButton("Start", BaseGame.textButtonStyle);
        startButton.addListener(
            (Event e) -> {
                if (!(e instanceof InputEvent) ||
                    !((InputEvent)e).getType().equals(InputEvent.Type.touchDown)){
                    return false;
                }
                StarfishGame.setActiveScreen(new LevelScreen());
                return false;
            }
        );
        TextButton quitButton = new TextButton("Quit", BaseGame.textButtonStyle);
        quitButton.addListener(
            (Event e) -> {
                if (!(e instanceof InputEvent) ||
                    !((InputEvent)e).getType().equals(InputEvent.Type.touchDown)){
                    return false;
                }
                Gdx.app.exit();
                return false;
            }
        );

        uiTable.add(title).colspan(2);
        uiTable.row();
        uiTable.add(startButton);
        uiTable.add(quitButton);
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            StarfishGame.setActiveScreen(new LevelScreen());
        }
    }

    @Override
    public boolean keyDown(int keyCode) {
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)){
            StarfishGame.setActiveScreen(new LevelScreen());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

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
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}

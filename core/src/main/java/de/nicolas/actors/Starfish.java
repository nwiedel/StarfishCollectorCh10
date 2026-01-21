package de.nicolas.actors;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import de.nicolas.utils.actors.BaseActor;

public class Starfish extends BaseActor {

    private boolean collected;

    public Starfish(float x, float y, Stage stage) {
        super(x, y, stage);
        collected = false;

        loadTexture("assets/starfish.png");

        setBoundaryPolygon(8);

        Action spin = Actions.rotateBy(30, 1f);
        this.addAction(Actions.forever(spin));
    }

    public boolean isCollected(){
        return collected;
    }

    public void setCollected(boolean collected){
        this.collected = collected;
    }

    public void collect(){
        collected = true;
        clearActions();
        addAction(Actions.fadeOut(1));
        addAction(Actions.after(Actions.removeActor()));
    }
}

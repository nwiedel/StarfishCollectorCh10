package de.nicolas.utils.actors;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class DropTargetActor extends BaseActor {

    private boolean targetable;

    public DropTargetActor(float x, float y, Stage stage) {
        super(x, y, stage);
        targetable = true;
    }

    public void setTargetable(boolean targetable){
        this.targetable = targetable;
    }

    public boolean isTargetable() {
        return targetable;
    }
}

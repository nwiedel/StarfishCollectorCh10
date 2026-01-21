package de.nicolas.actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import de.nicolas.utils.actors.BaseActor;

public class Sign extends BaseActor {

    private String text;
    private boolean viewing;

    public Sign(float x, float y, Stage stage) {
        super(x, y, stage);
        loadTexture("assets/sign.png");
        text = " ";
        viewing = false;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setViewing(boolean viewing) {
        this.viewing = viewing;
    }

    public boolean isViewing() {
        return viewing;
    }
}

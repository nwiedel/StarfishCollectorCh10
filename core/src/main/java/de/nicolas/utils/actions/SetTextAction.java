package de.nicolas.utils.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import de.nicolas.utils.actors.DialogBox;

public class SetTextAction extends Action {

    protected String textToDisplay;

    public SetTextAction(String textToDisplay) {
        this.textToDisplay = textToDisplay;
    }

    @Override
    public boolean act(float delta) {
        DialogBox dialogBox = (DialogBox)target;
        dialogBox.setText(textToDisplay);
        return true;
    }
}

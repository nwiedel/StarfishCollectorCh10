package de.nicolas.actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import de.nicolas.utils.actors.BaseActor;

public class Rock extends BaseActor {

    public Rock(float x, float y, Stage stage) {
        super(x, y, stage);
        loadTexture("assets/rock.png");
        setBoundaryPolygon(8);
    }
}

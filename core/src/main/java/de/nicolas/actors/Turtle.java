package de.nicolas.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.nicolas.utils.actors.BaseActor;

public class Turtle extends BaseActor {

    public Turtle(float x, float y, Stage stage) {
        super(x, y, stage);

        String[] fileNames =
            {"assets/turtle-1.png", "assets/turtle-2.png", "assets/turtle-3.png",
                "assets/turtle-4.png", "assets/turtle-5.png", "assets/turtle-6.png"};

        loadAnimationFromFiles(fileNames, 0.1f, true);

        setBoundaryPolygon(8);

        setAcceleration(400);
        setMaxSpeed(100);
        setDeceleration(400);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            accelerateAtAngle(180);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            accelerateAtAngle(0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            accelerateAtAngle(90);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            accelerateAtAngle(270);
        }

        applyPhysics(delta);

        setAnimationPaused(!isMoving());

        if (getSpeed() > 0) {
            setRotation(getMotionAngle());
        }

        boundToWorld();

        alignCamera();
    }
}

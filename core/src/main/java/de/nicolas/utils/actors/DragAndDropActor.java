package de.nicolas.utils.actors;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

/**
 * Realisiert Drag-and-Drop Funktionalität
 */
public class DragAndDropActor extends BaseActor {

    private DragAndDropActor self;

    private DropTargetActor dropTarget;

    private float grabOffsetX;
    private Float grabOffsetY;

    private boolean draggable;

    private float startPositionX;
    private float startPositionY;

    public DragAndDropActor(float x, float y, Stage stage) {
        super(x, y, stage);

        self = this;
        draggable = true;

        addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float offsetX, float offsetY, int pointer, int button) {
                if(!self.isDraggable()){
                    return false;
                }
                self.grabOffsetX = offsetX;
                self.grabOffsetY = offsetY;
                self.startPositionX = self.getX();
                self.startPositionY = self.getY();
                self.toFront();
                self.addAction(Actions.scaleTo(1.1f, 1.1f, 0.25f));
                self.onDragStart();
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float offsetX, float offsetY, int pointer) {
                float deltaX = offsetX - self.grabOffsetX;
                float deltaY = offsetY - self.grabOffsetY;
                self.moveBy(deltaX, deltaY);
            }

            @Override
            public void touchUp(InputEvent event, float offsetX, float offsetY, int pointer, int button) {
                // wird später hinzugefügt
                self.setDropTarget(null);

                float closestDistance = Float.MAX_VALUE;

                for (BaseActor actor : BaseActor.getList(self.getStage(), "de.nicolas.utils.actors.DropTargetActor")){
                    DropTargetActor target = (DropTargetActor) actor;
                    if (target.isTargetable() && self.overlaps(target)){
                        float currentDistance = Vector2.dst(self.getX(), self.getY(), target.getX(), target.getY());

                        if(currentDistance < closestDistance){
                            self.setDropTarget(target);
                            closestDistance = currentDistance;
                        }
                    }
                }
                self.addAction(Actions.scaleTo(1.0f, 1.0f, 0.25f));
                self.onDrop();
            }
        });
    }

    public void moveToActor(BaseActor other){
        float x = other.getX() + (other.getWidth() - this.getWidth()) / 2;
        float y = other.getY() + (other.getHeight() - this.getHeight()) / 2;
        addAction(Actions.moveTo(x, y, 0.5f, Interpolation.pow3));
    }

    public void moveToStart(){
        addAction(Actions.moveTo(startPositionX, startPositionY,0.5f, Interpolation.pow3));
    }

    public boolean hasDropTarget(){
        return (dropTarget != null);
    }

    public void setDropTarget(DropTargetActor dropTarget){
        this.dropTarget = dropTarget;
    }

    public void onDragStart(){

    }

    public void onDrop(){

    }

    public DropTargetActor getDropTarget(){
        return dropTarget;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public boolean isDraggable() {
        return draggable;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}

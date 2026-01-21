package de.nicolas.utils.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

/**
 * Erweitert Funktionalität der Actor Klasse von LibGDX
 * Support für Animation, Kollision mit Polygonen, Bewegung,
 * Weltgrenzen, Kamerabewegung
 */
public class BaseActor extends Group {

    private Animation<TextureRegion> animation;
    private float elapsedTime;
    private boolean animationPaused;

    private Vector2 velocityVec;
    private Vector2 accelerationVec;
    private float acceleration;
    private float maxSpeed;
    private float deceleration;

    private Polygon boundaryPolygon;

    public static Rectangle worldBounds;

    public BaseActor(float x, float y, Stage stage) {
        super();

        setPosition(x, y);
        stage.addActor(this);

        animation = null;
        elapsedTime = 0;
        animationPaused = false;

        velocityVec = new Vector2(0, 0);
        accelerationVec = new Vector2(0, 0);
        acceleration = 0;
        maxSpeed = 1000f;
    }

    /**
     * Ausrichtung der Actors
     * @param x Koordinaten
     * @param y Koordinaten
     */
    public void centerAtPosition(float x, float y){
        setPosition(x - getWidth() / 2, y - getHeight() / 2);
    }

    public void centerAtActor(BaseActor other){
        centerAtPosition(other.getX() + other.getWidth() / 2,
            other.getY() + other.getHeight() / 2);
    }

    // ----------------------------------------------------------------
    // Animationsmethoden
    // ----------------------------------------------------------------

    /**
     * setzt die Animation für den gegebenen Actor
     * @param animation - für den Actor
     */
    public void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
        TextureRegion region = animation.getKeyFrame(0);
        float width = region.getRegionWidth();
        float height = region.getRegionHeight();
        setSize(width, height);
        setOrigin(width / 2, height / 2);

        if (boundaryPolygon == null) {
            setBoundaryRectangle();
        }
    }

    /**
     * erstellt eine Animation aus mehreren verschiedenen Bildern
     * @param fileNames - ein Array von Bildern
     * @param frameDuration - wie lange wird jedes Bild gezeichnet
     * @param loop - wird die Animation wiederholt?
     * @return - die entsprechende Animation
     */
    public Animation<TextureRegion> loadAnimationFromFiles(
        String[] fileNames, float frameDuration, boolean loop) {
        int fileCount = fileNames.length;
        Array<TextureRegion> textureRegionArray = new Array<>();

        for (int i = 0; i < fileCount; i++) {
            String fileName = fileNames[i];
            Texture texture = new Texture(Gdx.files.internal(fileName));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textureRegionArray.add(new TextureRegion(texture));
        }

        Animation<TextureRegion> anim = new Animation<>(frameDuration, textureRegionArray);

        if (loop) {
            anim.setPlayMode(Animation.PlayMode.LOOP);
        } else {
            animation.setPlayMode(Animation.PlayMode.NORMAL);
        }

        if (animation == null) {
            setAnimation(anim);
        }
        return anim;
    }

    /**
     * erstellt eine Animation aus einemSpritesheet
     * @param fileName - name des Spritesheet
     * @param rows - Reihenanzahl
     * @param cols - Spaltenanzahl
     * @param frameDuration - wie lange wird jedes Bild gezeichnet
     * @param loop- wird die Animation wiederholt?
     * @return - die entsprechende Animation
     */
    public Animation<TextureRegion> loadAnimationFromSheet(
        String fileName, int rows, int cols, float frameDuration, boolean loop) {
        Texture texture = new Texture(Gdx.files.internal(fileName), true);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        int frameWidth = texture.getWidth() / cols;
        int frameHeight = texture.getHeight() / rows;

        TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight);

        Array<TextureRegion> textureArray = new Array<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                textureArray.add(temp[r][c]);
            }
        }

        Animation<TextureRegion> anim = new Animation<>(frameDuration, textureArray);

        if (loop) {
            anim.setPlayMode(Animation.PlayMode.LOOP);
        } else {
            anim.setPlayMode(Animation.PlayMode.NORMAL);
        }

        if (animation == null) {
            setAnimation(anim);
        }

        return anim;
    }

    /**
     * single Texture Animation aus einem Bild
     * @param fileName - Name des Bildes
     * @return - die entsprechende "Animation"
     */
    public Animation<TextureRegion> loadTexture(String fileName) {
        String[] fileNames = new String[1];
        fileNames[0] = fileName;
        return loadAnimationFromFiles(fileNames, 1, true);
    }

    /**
     * pausiert die Animation
     * @param pause
     */
    public void setAnimationPaused(boolean pause) {
        animationPaused = pause;
    }

    /**
     * überprüft, ob die Animation beendet ist
     * @return
     */
    public boolean isAnimationFinished() {
        return animation.isAnimationFinished(elapsedTime);
    }

    /**
     * setzt die Durchsichtigkeit
     * @param opacity
     */
    public void setOpacity(float opacity){
        this.getColor().a = opacity;
    }

    // ----------------------------------------------------------------
    // Physikalische und Bewegungsmethoden
    // ----------------------------------------------------------------

    /**
     * setzt die Beschleunigung
     * @param acceleration
     */
    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * setzt die Bremsung
     * @param deceleration
     */
    public void setDeceleration(float deceleration) {
        this.deceleration = deceleration;
    }

    /**
     * setzt die maimale Geschwindigleit
     * @param maxSpeed
     */
    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     * setzt die aktuelle Geschwindigkeit, Pixel pro Sekunde
     * @param speed
     */
    public void setSpeed(float speed) {

        if (velocityVec.len() == 0) {
            velocityVec.set(speed, 0);
        } else {
            velocityVec.setLength(speed);
        }
    }

    /**
     * berechnet die aktuelle Geschwindigkeit
     * @return
     */
    public float getSpeed() {
        return velocityVec.len();
    }

    /**
     * bestimmt, ob der Actor sich bewegt
     * @return
     */
    public boolean isMoving() {
        return (getSpeed() > 0);
    }

    /**
     * setzt den Bewegungswinkel
     * @param angle
     */
    public void setMotionAngle(float angle) {
        velocityVec.setAngleDeg(angle);
    }

    /**
     * gibt den Bewegungswinkel an
     * @return
     */
    public float getMotionAngle() {
        return velocityVec.angleDeg();
    }

    /**
     * erneuert den Beschleunigungswinkel
     * @param angle
     */
    public void accelerateAtAngle(float angle) {
        accelerationVec.add(new Vector2(acceleration, 0).setAngleDeg(angle));
    }

    /**
     * Einbeziehung der Rotation
     */
    public void accelerationForward() {
        accelerateAtAngle(getRotation());
    }

    /**
     * passt die Geschwindigkeit an
     * @param delta
     */
    public void applyPhysics(float delta) {
        // Beschleunigung anwenden
        velocityVec.add(accelerationVec.x * delta, accelerationVec.y * delta);

        float speed = getSpeed();

        // Geschwindigkeit verringern, wenn nicht beschleunigt wird
        if (accelerationVec.len() == 0) {
            speed -= deceleration * delta;
        }

        // Geschwindigkeit in ihren Grenzen halten
        speed = MathUtils.clamp(speed, 0, maxSpeed);

        // Geschwindigkeit anpasseen
        setSpeed(speed);

        // Geschwindigkeit anwenden
        moveBy(velocityVec.x * delta, velocityVec.y * delta);

        // Beschleunigung zurücksetzen
        accelerationVec.set(0, 0);
    }

    /**
     * Die Methode sorgt dafür, dass der Actor, wenn er
     * den sichtbaren Bereich verlässt, auf der anderen Seite wieder
     * erscheint
     */
    public void wrapAroundWorld(){
        if (getX() + getWidth() < 0){
            setX(worldBounds.width);
        }
        if (getX() > worldBounds.width){
            setX(-getWidth());
        }
        if (getY() +getHeight() < 0){
            setY(worldBounds.height);
        }
        if (getY() > worldBounds.height){
            setY(-getHeight());
        }
    }

    // -------------------------------------------------------------
    // Kollisions Methoden
    // -------------------------------------------------------------

    /**
     * setzt ein Rechteck um den Actor
     */
    public void setBoundaryRectangle() {
        float w = getWidth();
        float h = getHeight();

        float[] vertices = {0, 0, w, 0, w, h, 0, h};
        boundaryPolygon = new Polygon(vertices);
    }

    /**
     * setzt ein Polygon mit bestimmten Seiten um den Actor
     * @param numSides - Seitenzahl
     */
    public void setBoundaryPolygon(int numSides)
    {
        float w = getWidth();
        float h = getHeight();
        float[] vertices = new float[2*numSides];
        for (int i = 0; i < numSides; i++)
        {
            float angle = i * 6.28f / numSides;
            // x-coordinate
            vertices[2*i] = w/2 * MathUtils.cos(angle) + w/2;
            // y-coordinate
            vertices[2*i+1] = h/2 * MathUtils.sin(angle) + h/2;
        }
        boundaryPolygon = new Polygon(vertices);
    }

    /**
     * gibt das Polygon um den Actor zurück
     * @return
     */
    public Polygon getBoundaryPolygon()
    {
        boundaryPolygon.setPosition( getX(), getY() );
        boundaryPolygon.setOrigin( getOriginX(), getOriginY() );
        boundaryPolygon.setRotation ( getRotation() );
        boundaryPolygon.setScale( getScaleX(), getScaleY() );
        return boundaryPolygon;
    }

    /**
     * bestimmt sie Kollision verschiedenen Actors
     * @param other
     * @return
     */
    public boolean overlaps(BaseActor other)
    {
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();
        // initial test to improve performance
        if ( !poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()) )
            return false;
        return Intersector.overlapConvexPolygons( poly1, poly2 );
    }

    /**
     * das Zurücksetzen der Actors nach der Kollision
     * @param other
     * @return
     */
    public Vector2 preventOverlap(BaseActor other){
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        if(!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())){
            return null;
        }

        MinimumTranslationVector mtv = new MinimumTranslationVector();
        boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

        if(!polygonOverlap){
            return null;
        }

        this.moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth);
        return mtv.normal;
    }

    /**
     * setzt die Weltgrenzen
     * @param width
     * @param height
     */
    public static void setWorldBounds(float width, float height){
        worldBounds = new Rectangle(0, 0, width, height);
    }

    /**
     * setzt die Weltgrenzen anhand bspw. eine Hintergrundbildes
     * @param baseActor
     */
    public static void setWorldBounds(BaseActor baseActor){
        setWorldBounds(baseActor.getWidth(), baseActor.getHeight());
    }

    /**
     * gibt die Weltgrenzen zurück
     */
    public static Rectangle getWorldBounds(){
        return worldBounds;
    }

    /**
     * begrenzt die Welt, verhindert, das Actors die Welt verlassen
     */
    public void boundToWorld(){
        // links
        if(getX() < 0){
            setX(0);
        }
        // rechts
        if (getX() + getWidth() > worldBounds.getWidth()){
            setX(worldBounds.width - getWidth());
        }
        // unten
        if (getY() < 0){
            setY(0);
        }
        // oben
        if (getY() + getHeight() > worldBounds.height){
            setY(worldBounds.height - getHeight());
        }
    }

    /**
     * mittel die Kamera und verhindert, dass sie über die
     * Weltgrenzen hinausschaut
     */
    public void alignCamera(){
        Camera camera = this.getStage().getCamera();
        Viewport viewport = this.getStage().getViewport();

        // Kamera am Spieler zentrieren
        camera.position.set(this.getX() + this.getOriginX(), this.getY() + this.getOriginY(), 0);

        // Kamera an die Weltgrenzen binden
        camera.position.x = MathUtils.clamp(
            camera.position.x,
            camera.viewportWidth / 2,
            worldBounds.width - camera.viewportWidth / 2);
        camera.position.y = MathUtils.clamp(
            camera.position.y,
            camera.viewportHeight / 2,
            worldBounds.height - camera.viewportHeight / 2);

        camera.update();
    }

    /**
     * Erweitert die Kollisionbox, um festzustellen, ob ein Actor
     * sich in der Nähe befindet.
     * @param distance
     * @param other
     * @return
     */
    public boolean isWithinDistance(float distance, BaseActor other){
        Polygon poly1 = this.getBoundaryPolygon();
        float scaleX = (this.getWidth() + 2 * distance) / this.getWidth();
        float scaleY = (this.getHeight() + 2 * distance) / this.getHeight();
        poly1.setScale(scaleX, scaleY);

        Polygon poly2 = other.getBoundaryPolygon();

        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())){
            return false;
        }

        return Intersector.overlapConvexPolygons(poly1, poly2);
    }

    // --------------------------------------------------------------------------
    // List Methoden
    // --------------------------------------------------------------------------

    /**
     * Lister der in einer Stage vorhandenen Actors einer bestimmten Klasse
     * @param stage
     * @param className
     * @return
     */
    public static ArrayList<BaseActor> getList(Stage stage, String className){
        ArrayList<BaseActor> list = new ArrayList<>();

        Class theClass = null;
        try {
            theClass = Class.forName(className);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Actor actor : stage.getActors()){
            if(theClass.isInstance(actor)){
                list.add((BaseActor) actor);
            }
        }
        return list;
    }

    /**
     * gibt die Anzahl der Instanzen einer bestimmten Klasse zurück
     * @param stage
     * @param className
     * @return
     */
    public static int count(Stage stage, String className){
        return getList(stage, className).size();
    }


    // ------------------------------------------------------------------
    // Actor Methoden act und draw
    // ------------------------------------------------------------------

    /**
     * führt die Aktionen der Stage aus
     * @param delta
     */
    @Override
    public void act(float delta) {
        super.act(delta);

        if (!animationPaused) {
            elapsedTime += delta;
        }
    }

    /**
     * zeichnet den aktuellen Frame
     * @param batch
     * @param parentAlpha
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a);

        if (animation != null) {
            batch.draw(animation.getKeyFrame(elapsedTime),
                getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                getRotation());
        }

        super.draw(batch, parentAlpha);
    }
}

package no.sandramoen.commanderqueen.actors.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;
import no.sandramoen.commanderqueen.utils.pathFinding.TileGraph;

public class Enemy extends BaseActor3D {
    public final int ID = MathUtils.random(1_000, 9_999);
    public boolean isDead;
    public boolean isActive;
    public Tile goalTile;
    public GraphPath<Tile> tilePath;
    public Color originalColor = new Color(.4f, .4f, .4f, 1f);

    protected boolean isAttacking;
    protected boolean isReadyToAttack = true;
    protected int health = 1;
    protected int tilePathCounter;

    protected boolean isForcedToMove;
    protected float forceTime;
    protected final float SECONDS_FORCED_TO_MOVE = .25f;
    protected Vector2 forceMove = new Vector2(8f, 8f);

    protected float totalTime = 0;
    protected float movementSpeed;
    protected float angleTowardPlayer;
    protected final float VISIBILITY_RANGE = 100;
    protected Player player;
    protected BaseActor3D sprite;
    protected Array<BaseActor3D> shootable = new Array();

    protected enum Directions {FRONT, LEFT_FRONT, RIGHT_FRONT, LEFT_SIDE, RIGHT_SIDE, LEFT_BACK, RIGHT_BACK, BACK}

    protected Directions direction;

    protected enum State {IDLE, WALKING, HURT}

    protected State state = State.IDLE;

    protected Animation<TextureRegion> currentAnimation;
    protected Animation<TextureRegion> idleFrontAnimation;
    protected Animation<TextureRegion> idleFrontSideLeftAnimation;
    protected Animation<TextureRegion> idleFrontSideRightAnimation;
    protected Animation<TextureRegion> idleSideLeftAnimation;
    protected Animation<TextureRegion> idleSideRightAnimation;
    protected Animation<TextureRegion> idleBackSideLeftAnimation;
    protected Animation<TextureRegion> idleBackSideRightAnimation;
    protected Animation<TextureRegion> idleBackAnimation;
    protected Animation<TextureRegion> walkFrontAnimation;
    protected Animation<TextureRegion> walkFrontSideLeftAnimation;
    protected Animation<TextureRegion> walkFrontSideRightAnimation;
    protected Animation<TextureRegion> walkSideLeftAnimation;
    protected Animation<TextureRegion> walkSideRightAnimation;
    protected Animation<TextureRegion> walkBackSideLeftAnimation;
    protected Animation<TextureRegion> walkBackSideRightAnimation;
    protected Animation<TextureRegion> walkBackAnimation;
    protected Animation<TextureRegion> hurtAnimation;
    protected Animation<TextureRegion> dieAnimation;

    private boolean isPlayerVisible;
    private boolean isPlayerLastPositionKnown;

    private TileGraph tileGraph;
    private Array<Tile> floorTiles;

    public Enemy(float y, float z, Stage3D stage3D, Player player, Float rotation, TileGraph tileGraph, Array<Tile> floorTiles) {
        super(0, y, z, stage3D);
        this.player = player;
        this.tileGraph = tileGraph;
        this.floorTiles = floorTiles;

        float size = 3;
        buildModel(size, size, size, true);
        initializeSprite(size);
        turnBy(-180 + rotation);
        setPosition(GameUtils.getPositionRelativeToFloor(size), y, z);
        setBaseRectangle();
        isVisible = false;
        setDirection();
    }

    @Override
    public void act(float dt) {
        super.act(dt);

        totalTime += dt;
        handleSprite();

        if (isDead) return;

        /*System.out.println("active: " + isActive + ", state: " + state + ", direction: " + direction);*/

        setDirection();

        if (state == State.HURT && !stage3D.intervalFlag)
            state = State.WALKING; // TODO
        else if (state != State.HURT && stage3D.intervalFlag)
            setDirectionalSprites();

        attackIfPlayerIsVisible();
        setPathToLastKnownPlayerPosition();

        if (isActive && tilePath != null) {
            isAttacking = false;
            state = State.WALKING;
            walkTilePath();
        }
    }

    private void setPathToLastKnownPlayerPosition() {
        if (!isPlayerVisible && isPlayerLastPositionKnown) {
            tilePath = getPathTo(player);
            isPlayerLastPositionKnown = false;
        }
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        sprite.setColor(color);
    }

    public void die() {
        if (isDead) return;
        isDead = true;
        totalTime = 0f;
        isCollisionEnabled = false;
        currentAnimation = dieAnimation;
    }

    public boolean isDeadAfterTakingDamage(int amount) {
        health -= amount;
        if (health <= 0) {
            die();
            return true;
        }
        currentAnimation = hurtAnimation;
        state = State.HURT;

        setTurnAngle(angleTowardPlayer);
        setDirection();
        if (isPlayerVisible())
            setNewAIPath(player);

        return false;
    }

    public void forceMoveAwayFrom(BaseActor3D source) {
        isForcedToMove = true;
        if (position.y - source.position.y < 1) forceMove.x *= -1;
        if (position.z - source.position.z < 1) forceMove.y *= -1;
        forceTime = totalTime + SECONDS_FORCED_TO_MOVE;
    }

    public void setShootableList(Array<BaseActor3D> shootable) {
        this.shootable.clear();
        for (BaseActor3D baseActor3D : shootable)
            if (baseActor3D != this)
                this.shootable.add(baseActor3D);
        this.shootable.add(player);
    }

    public void activate(BaseActor3D source) {
        if (!isActive) {
            isActive = true;
            playActivateSound();
            setNewAIPath(source);
        }
    }

    protected boolean isPlayerVisible() {
        if (isDirectionFrontOrSides() && isWithinDistance(VISIBILITY_RANGE, player)) {
            int index = GameUtils.getRayPickedListIndex(position, player.position.cpy().sub(position), shootable);
            if (index > -1 && shootable.get(index).getClass().getSimpleName().equalsIgnoreCase("player"))
                return true;
        }
        return false;
    }

    protected void forceMove(float dt) {
        if (totalTime <= forceTime)
            moveBy(0f, forceMove.x * dt, forceMove.y * dt);
        else
            isForcedToMove = false;
    }

    protected void walkTilePath() {
        if (tilePath != null && tilePathCounter < tilePath.getCount()) {
            if (!isOnCenter(tilePath.get(tilePathCounter))) {
                moveToward(tilePath.get(tilePathCounter));
            } else if (isOnCenter(tilePath.get(tilePathCounter))) {
                if (tilePathCounter < tilePath.getCount())
                    tilePathCounter++;
            }
        } else if (tilePathCounter >= tilePath.getCount()) {
            isActive = false;
            tilePath = null;
        }
    }

    protected void moveToward(BaseActor3D baseActor3D) {
        setTurnAngle(GameUtils.getAngleTowardsBaseActor3D(this, baseActor3D));
        moveForward(movementSpeed);
    }

    protected void moveToward(float angle) {
        setTurnAngle(angle);
        moveForward(movementSpeed);
    }

    protected void setDirectionalSprites() {
        if (state == State.WALKING) {
            if (direction == Directions.FRONT)
                currentAnimation = walkFrontAnimation;
            else if (direction == Directions.LEFT_FRONT)
                currentAnimation = walkFrontSideLeftAnimation;
            else if (direction == Directions.LEFT_SIDE)
                currentAnimation = walkSideLeftAnimation;
            else if (direction == Directions.LEFT_BACK)
                currentAnimation = walkBackSideLeftAnimation;
            else if (direction == Directions.BACK)
                currentAnimation = walkBackAnimation;
            else if (direction == Directions.RIGHT_BACK)
                currentAnimation = walkBackSideRightAnimation;
            else if (direction == Directions.RIGHT_SIDE)
                currentAnimation = walkSideRightAnimation;
            else if (direction == Directions.RIGHT_FRONT)
                currentAnimation = walkFrontSideRightAnimation;
        } else if (state == State.IDLE) {
            if (direction == Directions.FRONT)
                currentAnimation = idleFrontAnimation;
            else if (direction == Directions.LEFT_FRONT)
                currentAnimation = idleFrontSideLeftAnimation;
            else if (direction == Directions.LEFT_SIDE)
                currentAnimation = idleSideLeftAnimation;
            else if (direction == Directions.LEFT_BACK)
                currentAnimation = idleBackSideLeftAnimation;
            else if (direction == Directions.BACK)
                currentAnimation = idleBackAnimation;
            else if (direction == Directions.RIGHT_BACK)
                currentAnimation = idleBackSideRightAnimation;
            else if (direction == Directions.RIGHT_SIDE)
                currentAnimation = idleSideRightAnimation;
            else if (direction == Directions.RIGHT_FRONT)
                currentAnimation = idleFrontSideRightAnimation;
        }
    }

    private boolean isDirectionFrontOrSides() {
        return direction == Directions.LEFT_FRONT ||
                direction == Directions.RIGHT_FRONT ||
                direction == Directions.FRONT ||
                direction == Directions.RIGHT_SIDE ||
                direction == Directions.LEFT_SIDE;
    }

    private void attackIfPlayerIsVisible() {
        if (stage3D.intervalFlag) {
            isPlayerVisible = isPlayerVisible();
            if (isPlayerVisible)
                attack();
        }
    }

    private void attack() {
        isActive = true;
        isAttacking = true;
        state = State.WALKING;
        isPlayerLastPositionKnown = true;
        resetAIPath();
    }

    private void setNewAIPath(BaseActor3D source) {
        tilePath = getPathTo(source);
        tilePathCounter = 0;
    }

    private void resetAIPath() {
        tilePath = null;
        tilePathCounter = 0;
    }

    private void playActivateSound() {
        if (thisEnemyIsA("menig"))
            GameUtils.playSoundRelativeToDistance(BaseGame.menigActiveSound, distanceBetween(player) * 1.2f, VOCAL_RANGE);
    }

    private boolean thisEnemyIsA(String name) {
        return getClass().getSimpleName().equalsIgnoreCase(name);
    }

    private void handleSprite() {
        sprite.setPosition(position);
        angleTowardPlayer = GameUtils.getAngleTowardsBaseActor3D(this, player);
        sprite.setTurnAngle(angleTowardPlayer);
    }

    private void setDirection() {
        float temp = angleTowardPlayer - getTurnAngle();
        while (temp < 0)
            temp += 360;

        if (temp <= 22.5 || temp >= 337.5)
            direction = Directions.FRONT;
        else if (temp < 337.5 && temp > 292.5f)
            direction = Directions.RIGHT_FRONT;
        else if (temp <= 292.5 && temp >= 247.5)
            direction = Directions.RIGHT_SIDE;
        else if (temp < 247.5 && temp > 202.5f)
            direction = Directions.RIGHT_BACK;
        else if (temp <= 202.5f && temp >= 157.5f)
            direction = Directions.BACK;
        else if (temp < 157.5 && temp > 112.5f)
            direction = Directions.LEFT_BACK;
        else if (temp <= 112.5 && temp >= 67.5f)
            direction = Directions.LEFT_SIDE;
        else if (temp < 67.5 && temp > 22.5f)
            direction = Directions.LEFT_FRONT;
    }

    private GraphPath<Tile> getPathTo(BaseActor3D source) {
        Tile startTile = getTileActorIsOn(this, floorTiles);
        goalTile = getTileActorIsOn(source, floorTiles);
        return tileGraph.findPath(startTile, goalTile);
    }

    private Tile getTileActorIsOn(BaseActor3D actor, Array<Tile> tiles) {
        for (Tile tile : tiles)
            if (actor.overlaps(tile))
                return tile;
        Gdx.app.error(getClass().getSimpleName(), "Error: could not find the tile the " + actor.getClass().getSimpleName() + " is standing on!");
        return null;
    }

    private void initializeSprite(float size) {
        sprite = new BaseActor3D(0, 0, 0, stage3D);
        sprite.buildModel(size, size, .001f, true);
        sprite.setColor(originalColor);
    }
}

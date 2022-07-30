package no.sandramoen.commanderqueen.actors.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
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
    protected boolean intervalFlag;
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
    protected Array<BaseActor3D> shootable;

    protected enum Directions {FRONT, LEFT_FRONT, RIGHT_FRONT, LEFT_SIDE, RIGHT_SIDE, LEFT_BACK, RIGHT_BACK, BACK}
    protected Directions direction;

    private boolean isPlayerVisible;
    private boolean isPlayerLastPositionKnown;

    private float intervalCounter;
    private final float INTERVAL_COUNTER_FREQUENCY = .9f;

    private TileGraph tileGraph;
    private Array<Tile> floorTiles;

    public Enemy(float y, float z, Stage3D stage3D, Player player, Float rotation, TileGraph tileGraph, Array<Tile> floorTiles) {
        super(0, y, z, stage3D);
        this.player = player;
        this.tileGraph = tileGraph;
        this.floorTiles = floorTiles;

        float size = 3;
        buildModel(size, size, size, true);
        setBaseRectangle();
        setPosition(GameUtils.getPositionRelativeToFloor(size), y, z);
        isVisible = false;
        shootable = new Array();

        sprite = new BaseActor3D(0, 0, 0, stage3D);
        sprite.buildModel(size, size, .001f, true);
        sprite.setColor(originalColor);
        turnBy(-180 + rotation);
        tilePath = null;
    }

    @Override
    public void act(float dt) {
        super.act(dt);

        totalTime += dt;
        setIntervalFlag(dt);
        handleSprite();

        if (isDead) return;

        setDirection();

        if (intervalFlag) {
            isPlayerVisible = isPlayerVisible();
            if (isPlayerVisible) {
                isActive = true;
                isAttacking = true;
                tilePathCounter = 0;
                isPlayerLastPositionKnown = true;
                tilePath = null;
            }
        }

        if (!isPlayerVisible && isPlayerLastPositionKnown) {
            tilePath = getPathTo(tileGraph, this, player, floorTiles);
            isPlayerLastPositionKnown = false;
        }

        if (isActive && tilePath != null) {
            isAttacking = false;
            walkTilePath();
        }
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        sprite.setColor(color);
    }

    public void die() {
        if (isDead) return;
    }

    public void decrementHealth(int amount) {
        health -= amount;
        if (health < 0)
            die();
    }

    public void forceMoveAwayFrom(BaseActor3D source) {
        isForcedToMove = true;
        if (position.y - source.position.y < 1) forceMove.x *= -1;
        if (position.z - source.position.z < 1) forceMove.y *= -1;
        forceTime = totalTime + SECONDS_FORCED_TO_MOVE;
    }

    public void setShootable(Array<BaseActor3D> shootable) {
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
            tilePath = getPathTo(tileGraph, this, source, floorTiles);
            tilePathCounter = 0;
        }
    }

    public BaseActor3D tellWhereToGo() {
        if (goalTile != null)
            return goalTile;
        else return this;
    }

    protected boolean isPlayerVisible() {
        if (
                (
                        direction == Directions.LEFT_FRONT ||
                                direction == Directions.RIGHT_FRONT ||
                                direction == Directions.FRONT ||
                                direction == Directions.RIGHT_SIDE ||
                                direction == Directions.LEFT_SIDE
                ) &&
                        isWithinDistance(VISIBILITY_RANGE, player)
        ) {
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
                setTurnAngle(GameUtils.getAngleTowardsBaseActor3D(this, tilePath.get(tilePathCounter)));
                moveForward(movementSpeed);
            } else if (isOnCenter(tilePath.get(tilePathCounter))) {
                if (tilePathCounter < tilePath.getCount())
                    tilePathCounter++;
            }
        } else if (tilePathCounter >= tilePath.getCount()) {
            isActive = false;
        }
    }

    private void playActivateSound() {
        if (thisEnemyIsA("Ghoul"))
            GameUtils.playSoundRelativeToDistance(BaseGame.ghoulDeathSound, distanceBetween(player), VOCAL_RANGE, .75f);
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

    private void setIntervalFlag(float dt) {
        if (intervalCounter > INTERVAL_COUNTER_FREQUENCY) {
            intervalFlag = true;
            intervalCounter = 0;
        } else {
            intervalFlag = false;
            intervalCounter += dt;
        }
    }

    private GraphPath<Tile> getPathTo(TileGraph tileGraph, BaseActor3D actor, BaseActor3D source, Array<Tile> tiles) {
        Tile startTile = getTileActorIsOn(actor, tiles);
        goalTile = getTileActorIsOn(source, tiles);
        return tileGraph.findPath(startTile, goalTile);
    }

    private Tile getTileActorIsOn(BaseActor3D baseActor3D, Array<Tile> tiles) {
        for (Tile tile : tiles)
            if (baseActor3D.overlaps(tile))
                return tile;
        Gdx.app.error(getClass().getSimpleName(), "Error: could not find the tile the " + baseActor3D.getClass().getSimpleName() + " is standing on!");
        return null;
    }
}

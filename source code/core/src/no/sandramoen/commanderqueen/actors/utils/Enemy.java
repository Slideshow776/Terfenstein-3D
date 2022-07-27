package no.sandramoen.commanderqueen.actors.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Enemy extends BaseActor3D {
    public boolean isActive = false;
    protected Array<BaseActor3D> shootable;
    public boolean isDead = false;
    public Color originalColor = new Color(.4f, .4f, .4f, 1f);
    public boolean isReadyToAttack = true;
    public int health = 1;

    protected float totalTime = 0;
    protected Player player;
    protected float movementSpeed;
    protected boolean isForcedToMove;
    protected Vector2 forceMove = new Vector2(8f, 8f);
    protected float forceTime;
    protected final float SECONDS_FORCED_TO_MOVE = .25f;
    protected final float VISIBILITY_RANGE = 100;
    protected BaseActor3D sprite;
    protected float angleTowardPlayer;
    protected enum Directions {FRONT, LEFT_FRONT, RIGHT_FRONT, LEFT_SIDE, RIGHT_SIDE, LEFT_BACK, RIGHT_BACK, BACK}
    protected Directions direction;

    public Enemy(float y, float z, Stage3D stage3D, Player player) {
        super(0, y, z, stage3D);
        this.player = player;
        float size = 3;
        buildModel(size, size, size, true);
        setBaseRectangle();
        setPosition(GameUtils.getPositionRelativeToFloor(size), y, z);
        isVisible = false;
        shootable = new Array();

        sprite = new BaseActor3D(0, 0, 0, stage3D);
        sprite.buildModel(size, size, .001f, true);
        sprite.setColor(originalColor);
        turnBy(-180);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        totalTime += dt;
        handleSprite();
        if (isDead) return;

        setDirection();
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

    public void activate() {
    }

    public boolean isPlayerVisible() {
        if (
                (direction == Directions.LEFT_FRONT || direction == Directions.RIGHT_FRONT || direction == Directions.FRONT) &&
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

    private void handleSprite() {
        sprite.setPosition(position);
        angleTowardPlayer = GameUtils.getAngleTowardsPlayer(this, player);
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
}

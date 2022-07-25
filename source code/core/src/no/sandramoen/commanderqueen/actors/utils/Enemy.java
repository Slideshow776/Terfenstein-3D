package no.sandramoen.commanderqueen.actors.utils;

import com.badlogic.gdx.graphics.Color;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Enemy extends BaseActor3D {
    protected float totalTime = 0;
    protected Player player;
    protected float movementSpeed;
    protected boolean isForcedToMove;
    protected float forceMoveY = 8;
    protected float forceMoveZ = 8;
    protected float forceTime;
    protected float secondsForcedToMove = .25f;

    public boolean isDead = false;
    public Color originalColor = new Color(.4f, .4f, .4f, 1f);
    public boolean isReadyToAttack = true;
    public int health = 1;

    public Enemy(float y, float z, Stage3D s, Player player) {
        super(0, y, z, s);
        this.player = player;
        buildModel(3, 3, 3f);
        setBaseRectangle();
        setPosition(GameUtils.getPositionRelativeToFloor(3), y, z);
        setColor(originalColor);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        totalTime += dt;
        setTurnAngle(GameUtils.getAngleTowardsPlayer(this, player));
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
        if (position.y - source.position.y < 1) forceMoveY *= -1;
        if (position.z - source.position.z < 1) forceMoveZ *= -1;
        forceTime = totalTime + secondsForcedToMove;
    }

    protected void forceMove(float dt) {
        if (totalTime <= forceTime)
            moveBy(0f, forceMoveY * dt, forceMoveZ * dt);
        else
            isForcedToMove = false;
    }
}

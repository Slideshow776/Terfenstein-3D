package no.sandramoen.commanderqueen.actors.utils;

import com.badlogic.gdx.graphics.Color;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Enemy extends BaseActor3D {
    protected Player player;

    public boolean isDead = false;
    public Color originalColor = new Color(.4f, .4f, .4f, 1f);
    public int health = 1;

    public Enemy(float y, float z, Stage3D s, Player player) {
        super(0, y, z, s);
        this.player = player;
        buildModel(3, 3, .1f);
        setBaseRectangle();
        setPosition(GameUtils.getPositionRelativeToFloor(3), y, z);
        setColor(originalColor);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
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
}

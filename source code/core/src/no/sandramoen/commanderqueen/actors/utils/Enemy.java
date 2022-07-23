package no.sandramoen.commanderqueen.actors.utils;

import com.badlogic.gdx.graphics.Color;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Enemy extends BaseActor3D {
    protected boolean dead = false;
    private Player player;
    public Color originalColor = new Color(.4f, .4f, .4f, 1f);

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
        if (dead) return;
    }
}

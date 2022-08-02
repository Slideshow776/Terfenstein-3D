package no.sandramoen.commanderqueen.actors.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;
import no.sandramoen.commanderqueen.utils.pathFinding.TileGraph;

public class Enemy extends BaseActor3D {
    public final int ID = MathUtils.random(1_000, 9_999);
    public boolean isDead;
    public boolean isActive;
    public Color originalColor = new Color(.4f, .4f, .4f, 1f);
    public boolean isRanged = true;
    public int score = 0;
    public int damage = 0;

    protected Player player;
    protected float movementSpeed;

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
    protected Animation<TextureRegion> shootAnimation;
    protected Animation<TextureRegion> meleeAnimation;
    protected Animation<TextureRegion> hurtAnimation;
    protected Animation<TextureRegion> dieAnimation;

    protected int health = 1;
    protected float shootImageDelay;
    private float shootFrequency = 1f;

    private boolean isForcedToMove;
    private int tilePathCounter;
    private float forceTime;
    private float totalTime = 0;
    private float angleTowardPlayer;
    private final float SECONDS_FORCED_TO_MOVE = .25f;

    private enum Directions {FRONT, LEFT_FRONT, RIGHT_FRONT, LEFT_SIDE, RIGHT_SIDE, LEFT_BACK, RIGHT_BACK, BACK}

    private Directions direction;

    private enum State {IDLE, WALKING, HURT, ATTACKING}

    private State state = State.IDLE;
    private float shootCounter = 0;
    private final float VISIBILITY_RANGE = 100;
    private boolean isAttacking;

    private float dogdeDirectionFrequency = 3;
    private float dogdeDirectionCounter = 0;
    private float dodgeDirectionAngle = 45;

    private float atackStateChangeFrequency = 1.5f;
    private float attackStateChangeCounter = 0;

    private boolean isAttackDodging;
    private boolean isPlayerVisible;
    private boolean isPlayerLastPositionKnown;

    private HUD hud;
    private Stage stage;
    private Tile goalTile;
    private BaseActor3D sprite;
    private TileGraph tileGraph;
    private Array<Tile> floorTiles;
    private GraphPath<Tile> tilePath;
    private Array<BaseActor3D> shootable = new Array();
    private Vector2 forceMove = new Vector2(8f, 8f);

    public Enemy(float y, float z, Stage3D stage3D, Player player, Float rotation, TileGraph tileGraph, Array<Tile> floorTiles, Stage stage, HUD hud) {
        super(0, y, z, stage3D);
        this.player = player;
        this.tileGraph = tileGraph;
        this.floorTiles = floorTiles;
        this.stage = stage;
        this.hud = hud;

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
        if (isPause) return;

        totalTime += dt;
        handleSprite();
        if (isForcedToMove) forceMove(dt);

        if (isDead || state == State.HURT) return;

        setDirection();
        setDirectionalSprites();
        attackIfPlayerIsVisible();

        if (!isActive) return;

        setPathToLastKnownPlayerPosition();

        if (isAttacking && isPlayerVisible) {
            attacking(dt);
        } else if (tilePath != null) {
            isAttacking = false;
            state = State.WALKING;
            walkTilePath();
        }
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        sprite.setColor(color);
    }

    @Override
    public void draw(ModelBatch batch, Environment env) {
        sprite.loadImage(currentAnimation.getKeyFrame(totalTime).toString());
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
        if (health <= 0)
            return true;
        setTemporaryHurtState();
        findPlayer();
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
        }
        setNewAIPath(source);
    }

    private boolean isPlayerVisible() {
        if (isActive && isWithinDistance(VISIBILITY_RANGE, player)) {
            int index = GameUtils.getRayPickedListIndex(position, player.position.cpy().sub(position), shootable);
            if (index > -1 && shootable.get(index).getClass().getSimpleName().equalsIgnoreCase("player"))
                return true;
        } else if (isDirectionFrontOrSides() && isWithinDistance(VISIBILITY_RANGE, player)) {
            int index = GameUtils.getRayPickedListIndex(position, player.position.cpy().sub(position), shootable);
            if (index > -1 && shootable.get(index).getClass().getSimpleName().equalsIgnoreCase("player"))
                return true;
        }
        return false;
    }

    protected void moveToward(BaseActor3D baseActor3D) {
        setTurnAngle(GameUtils.getAngleTowardsBaseActor3D(this, baseActor3D));
        moveForward(movementSpeed);
    }

    protected void moveToward(float angle) {
        setTurnAngle(angle);
        moveForward(movementSpeed);
    }

    protected void meleeWeapon() {
    }

    protected void shootWeapon() {
        new BaseActor(0, 0, stage).addAction(Actions.sequence(
                Actions.delay(shootImageDelay),
                Actions.run(() -> {
                    hud.decrementHealth(damage, this);
                    stage3D.lightManager.addMuzzleLight(position);
                })
        ));
    }

    protected void playActivateSound() {
    }

    private void walkTilePath() {
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

    private void attacking(float dt) {
        checkAttackStateChange(dt);
        if (isAttackDodging) {
            moveInZigZag(dt);
            if (state != State.HURT)
                state = State.WALKING;
        } else {
            attack(dt);
        }
    }

    private void checkAttackStateChange(float dt) {
        if (attackStateChangeCounter > atackStateChangeFrequency) {
            attackStateChangeCounter = 0;
            float temp = MathUtils.random(0f, 1f);
            if ((temp < distanceBetween(player) / 45) || (!isRanged && !isWithinDistance(5f, player))) {
                isAttackDodging = true;
            } else {
                isAttackDodging = false;
                shootCounter = shootFrequency;
            }
        } else {
            attackStateChangeCounter += dt;
        }
    }

    private void moveInZigZag(float dt) {
        checkIfHitAWallAndShouldGoStraight();
        if (dogdeDirectionCounter > dogdeDirectionFrequency) {
            dodgeDirectionAngle *= -1;
            dogdeDirectionCounter = 0;
        } else
            dogdeDirectionCounter += dt;
        moveToward(angleTowardPlayer + dodgeDirectionAngle);
    }

    private void checkIfHitAWallAndShouldGoStraight() {
        for (BaseActor3D baseActor3D : shootable) {
            if (baseActor3D.getClass().getSimpleName().equalsIgnoreCase("tile")) {
                Tile temp = (Tile) baseActor3D;
                if (temp.type == "walls" && overlaps(temp) && dodgeDirectionAngle != 0) {
                    dodgeDirectionAngle = 0;
                    resetDodgeDirectionAngleAfterDelay();
                    break;
                }
            }
        }
    }

    private void resetDodgeDirectionAngleAfterDelay() {
        new BaseActor(0, 0, stage).addAction(Actions.sequence(
                Actions.delay(3),
                Actions.run(() -> dodgeDirectionAngle = 45)
        ));
    }

    private void attack(float dt) {
        if (shootCounter > shootFrequency) {
            shootOrMelee();
            shootCounter = 0;
        } else {
            shootCounter += dt;
        }
    }

    private void shootOrMelee() {
        if (isWithinDistance(5f, player))
            melee();
        else if (isRanged)
            shoot();
    }

    private void shoot() {
        state = State.ATTACKING;
        currentAnimation = shootAnimation;
        totalTime = 0;
        shootWeapon();
    }

    private void melee() {
        state = State.ATTACKING;
        currentAnimation = meleeAnimation;
        totalTime = 0;
        hud.decrementHealth(damage, this);
        meleeWeapon();
    }

    private void setTemporaryHurtState() {
        State temp = state;
        state = State.HURT;
        new BaseActor(0, 0, stage).addAction(Actions.sequence(
                Actions.delay(.4f),
                Actions.run(() -> state = temp)
        ));
        currentAnimation = hurtAnimation;
    }

    private void forceMove(float dt) {
        if (totalTime <= forceTime)
            moveBy(0f, forceMove.x * dt, forceMove.y * dt);
        else
            isForcedToMove = false;
    }

    private void attackIfPlayerIsVisible() {
        if (stage3D.intervalFlag) {
            isPlayerVisible = isPlayerVisible();
            if (isPlayerVisible)
                setAttackState();
        }
    }

    private void findPlayer() {
        setTurnAngle(angleTowardPlayer);
        setDirection();
        if (isPlayerVisible())
            setNewAIPath(player);
    }

    private void setAttackState() {
        if (!isActive)
            playActivateSound();
        isActive = true;
        isAttacking = true;
        isPlayerLastPositionKnown = true;
        attackStateChangeCounter = atackStateChangeFrequency / 2;
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

    private void setPathToLastKnownPlayerPosition() {
        if (!isPlayerVisible && isPlayerLastPositionKnown) {
            tilePath = getPathTo(player);
            isPlayerLastPositionKnown = false;
        }
    }

    private void setNewAIPath(BaseActor3D source) {
        try {
            tilePath = getPathTo(source);
            tilePathCounter = 0;
        } catch (Exception ex) {
        }
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

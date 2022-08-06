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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Barrel;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;
import no.sandramoen.commanderqueen.utils.pathFinding.TileGraph;

public class Enemy extends BaseActor3D {
    public final int ID = MathUtils.random(1_000, 9_999);
    public int health = 1;
    public boolean isDead;
    public boolean isActive;
    public Color darkColor = new Color(.4f, .4f, .4f, 1f);
    public boolean isRanged = true;
    public int score = 0;

    protected Player player;
    protected float movementSpeed;
    protected float painChance;

    protected int minDamage;
    protected int maxDamage;

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
    protected Animation<TextureRegion> gibAnimation;
    protected Animation<TextureRegion> dieAnimation;

    private int gibThreshold;
    private boolean isGibs;

    protected float shootImageDelay;
    private float shootFrequency = 1f;

    private boolean isForcedToMove;
    private int tilePathCounter;
    private float forceTime;
    private float totalTime = 0;
    private float angleTowardPlayer;
    private final float SECONDS_FORCED_TO_MOVE = .3f;

    private enum Directions {FRONT, LEFT_FRONT, RIGHT_FRONT, LEFT_SIDE, RIGHT_SIDE, LEFT_BACK, RIGHT_BACK, BACK}

    private Directions direction;

    private enum State {IDLE, WALKING, HURT, ATTACKING}

    private State state = State.IDLE;
    private float shootCounter = 0;
    private final float VISIBILITY_RANGE = 100;
    private boolean isAttacking;

    private float dodgeDirectionFrequency = 3;
    private float dodgeDirectionCounter = 0;
    private float dodgeDirectionAngle = 45;

    protected float attackStateChangeFrequency;
    private float attackStateChangeCounter = 0;

    private boolean isAttackDodging;
    private boolean isPlayerVisible;
    private boolean isPlayerLastPositionKnown;

    private BaseActor3D goingTo;
    private HUD hud;
    private Stage stage;
    private Tile goalTile;
    private BaseActor3D sprite;
    private TileGraph tileGraph;
    private Array<Tile> floorTiles;
    private GraphPath<Tile> tilePath;
    private Array<BaseActor3D> shootable = new Array();
    private Array<Enemy> enemies = new Array();
    private Vector2 forceMove = new Vector2(8f, 8f);
    private BaseActor attackDelayActor;

    public Enemy(float y, float z, Stage3D stage3D, Player player, float rotation, TileGraph tileGraph, Array<Tile> floorTiles, Stage stage, HUD hud) {
        super(0, y, z, stage3D);
        this.player = player;
        this.tileGraph = tileGraph;
        this.floorTiles = floorTiles;
        this.stage = stage;
        this.hud = hud;

        float size = 3;
        buildModel(1.5f, size, 1.5f, true);
        initializeSprite(size);
        turnBy(-180 + rotation);
        setPosition(GameUtils.getPositionRelativeToFloor(size), y, z);
        setBaseRectangle();
        isVisible = false;
        setDirection();
        attackDelayActor = new BaseActor(0, 0, stage);
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

        try {
            setPathToLastKnownPlayerPosition();
        } catch (Exception exception) {
            Gdx.app.error(getClass().getSimpleName(), "setPathToLastKnownPlayerPosition() failed => " + exception);
        }

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
        attackDelayActor.clearActions();
        isCollisionEnabled = false;
        if (isGibs) {
            currentAnimation = gibAnimation;
            BaseGame.wetSplashSound.play(BaseGame.soundVolume);
        } else {
            currentAnimation = dieAnimation;
        }
    }

    public void decrementHealth(int amount) {
        health -= amount;
        if (health <= gibThreshold)
            isGibs = true;
        if (health > 0 && amount > 0) {
            if (MathUtils.random(0f, 1f) > (1 - painChance))
                setTemporaryHurtState();
            findPlayer();
        }
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

    public void setEnemiesList(Array<Enemy> enemies) {
        this.enemies.clear();
        for (Enemy enemy : enemies)
            if (enemy != this)
                this.enemies.add(enemy);
    }

    public void activate(BaseActor3D source) {
        if (!isActive) {
            isActive = true;
            playActivateSound();
        }
        if (goingTo == null || source == player)
            setNewAIPath(source);
    }

    protected void moveToward(BaseActor3D baseActor3D) {
        setTurnAngle(GameUtils.getAngleTowardsBaseActor3D(this, baseActor3D));
        moveForward(movementSpeed);
    }

    protected void moveToward(float angle) {
        setTurnAngle(angle);
        moveForward(movementSpeed);
    }

    protected void meleeSound() {
    }

    protected void shootWeapon() {
        if (attackDelayActor.getActions().size == 0)
            attackDelayActor.addAction(Actions.sequence(
                    Actions.delay(shootImageDelay),
                    Actions.run(() -> {
                        shootSound();
                        checkIfShotPlayerOrBarrel();
                        activateNearByEnemies();
                        stage3D.lightManager.addMuzzleLight(position);
                        setColor(new Color(1, 1, .9f, 1));
                    })
            ));
    }

    protected void shootSound() {
    }

    protected void playActivateSound() {
    }

    protected void setHealth(int health) {
        this.health = health;
        gibThreshold = -health - 1;
    }

    private void activateNearByEnemies() {
        for (Enemy enemy : enemies)
            if (enemy.isWithinDistance(60f, this))
                enemy.activate(player);
    }

    private int getDamage() {
        return MathUtils.random(minDamage, maxDamage);
    }

    private void checkIfShotPlayerOrBarrel() {
        Vector3 temp = player.position.cpy();
        float maxSpread = 1.75f;
        temp.y += MathUtils.random(-maxSpread, maxSpread);
        temp.z += MathUtils.random(-maxSpread, maxSpread);
        int i = GameUtils.getRayPickedListIndex(position, temp.cpy().sub(position), shootable);
        if (i > -1 && GameUtils.isActor(shootable.get(i), "barrel")) {
            Barrel barrel = (Barrel) shootable.get(i);
            barrel.decrementHealth(getDamage(), distanceBetween(barrel));
        } else if (i > -1 && GameUtils.isActor(shootable.get(i), "player"))
            hud.decrementHealth(getDamage(), this);
    }

    private boolean isPlayerVisible() {
        if (isWithinDistance(VISIBILITY_RANGE, player) && (isActive || isDirectionFrontOrSides())) {
            int i = GameUtils.getRayPickedListIndex(position, player.position.cpy().sub(position), shootable);
            if (i > -1) {
                if (!GameUtils.isActor(shootable.get(i), "player") && !GameUtils.isActor(shootable.get(i), "barrel"))
                    return false;
                else
                    return true;
            }
        }
        return false;
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
            state = State.IDLE;
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
        if (attackStateChangeCounter > attackStateChangeFrequency) {
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
        if (dodgeDirectionCounter > dodgeDirectionFrequency) {
            dodgeDirectionAngle *= -1;
            dodgeDirectionCounter = 0;
        } else
            dodgeDirectionCounter += dt;
        moveToward(angleTowardPlayer + dodgeDirectionAngle);
    }

    private void checkIfHitAWallAndShouldGoStraight() {
        for (BaseActor3D baseActor3D : shootable) {
            if (GameUtils.isActor(baseActor3D, "tile")) {
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
        useMeleeWeapon();
    }

    private void useMeleeWeapon() {
        if (attackDelayActor.getActions().size == 0)
            attackDelayActor.addAction(Actions.sequence(
                    Actions.delay(shootImageDelay),
                    Actions.run(() -> {
                        hud.decrementHealth(getDamage(), this);
                        meleeSound();
                    })
            ));
    }

    private void setTemporaryHurtState() {
        state = State.HURT;
        attackDelayActor.clearActions();
        setStateToIdleAfterDelay(.4f);
        currentAnimation = hurtAnimation;
    }

    private void setStateToIdleAfterDelay(float delay) {
        new BaseActor(0, 0, stage).addAction(Actions.sequence(
                Actions.delay(delay),
                Actions.run(() -> state = State.ATTACKING)
        ));
    }

    private void forceMove(float dt) {
        if (totalTime <= SECONDS_FORCED_TO_MOVE)
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
        attackStateChangeCounter = attackStateChangeFrequency / 2;
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
            goingTo = source;
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
        sprite.setColor(darkColor);
    }
}

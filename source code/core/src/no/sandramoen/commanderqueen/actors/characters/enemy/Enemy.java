package no.sandramoen.commanderqueen.actors.characters.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Barrel;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.decals.BulletDecals;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;
import no.sandramoen.commanderqueen.utils.pathFinding.TileGraph;

public class Enemy extends BaseActor3D {
    public static float activationRange = 30;
    public final int ID = MathUtils.random(1_000, 9_999);
    public int health = 1;
    public boolean isDead;
    public boolean isActive;
    public boolean isRanged = true;
    public int score = 0;
    public Array<Tile> patrol = new Array();
    private int patrolIndex = 0;

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
    protected float shootFrequency = 1.5f;
    protected float rangeThreshold = 45f;
    protected int numShots = 1;

    public boolean isForcedToMove;
    private int tilePathCounter;
    private float forceTime;
    private float totalTime = 0;
    private float angleTowardPlayer;
    private final float SECONDS_FORCED_TO_MOVE = .02f;
    private final float SHOOT_SPREAD_ANGLE = 5.5f / 2;
    protected BaseActor3D sprite;

    private enum Direction {FRONT, LEFT_FRONT, RIGHT_FRONT, LEFT_SIDE, RIGHT_SIDE, LEFT_BACK, RIGHT_BACK, BACK}

    private Direction direction;

    enum State {IDLE, WALKING, HURT, ATTACKING}

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
    private TileGraph tileGraph;
    private Array<Tile> floorTiles;
    private GraphPath<Tile> tilePath;
    private Array<BaseActor3D> shootable = new Array();
    private Array<Enemy> enemies = new Array();
    private Vector2 forceMove = new Vector2(3f, 3f);
    private BaseActor attackDelayActor;
    private DecalBatch decalBatch;
    private BulletDecals bulletDecals;
    private Tile startingPosition;

    public Enemy(float y, float z, Stage3D stage3D, Player player, float rotation, TileGraph tileGraph, Array<Tile> floorTiles, Stage stage, HUD hud, DecalBatch batch) {
        super(0, y, z, stage3D);
        this.player = player;
        this.tileGraph = tileGraph;
        this.floorTiles = floorTiles;
        this.stage = stage;
        this.hud = hud;
        this.decalBatch = batch;

        float size = 3;
        buildModel(1.5f, size, 1.5f, false);
        initializeSprite(size);
        turnBy(-180 + rotation);
        setPosition(GameUtils.getPositionRelativeToFloor(size), y, z);
        setBaseRectangle();

        setDirection();
        bulletDecals = new BulletDecals(stage3D.camera, decalBatch);
        attackDelayActor = new BaseActor(0, 0, stage);
        checkIllumination();

        startingPosition = getTileActorIsOn(this, floorTiles);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        handleSprite();
        if (isPause) return;

        totalTime += dt;
        if (isForcedToMove) forceMove(dt);

        if (isDead || state == State.HURT) return;

        angleTowardPlayer = GameUtils.getAngleTowardsBaseActor3D(this, player);
        setDirection();
        setDirectionalAnimation();
        attackIfPlayerIsVisible();

        if (!isActive) return;

        bulletDecals.render(dt);
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
    public void draw(ModelBatch batch, Environment env) {
        sprite.loadImage(currentAnimation.getKeyFrame(totalTime).toString());
        // batch.render(modelData, env);
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
        attackDelayActor.clearActions();
        checkIllumination();
        isCollisionEnabled = false;
        if (isGibs) {
            currentAnimation = gibAnimation;
            BaseGame.wetSplashSound.play(BaseGame.soundVolume);
        } else {
            currentAnimation = dieAnimation;
        }
    }

    public void activate(BaseActor3D source) {
        if (!isActive) {
            isActive = true;
            playActivateSound();
        }
        if (goingTo == null || source == player)
            setNewAIPath(source);
    }

    public void decrementHealth(int amount) {
        health -= amount;
        if (health <= gibThreshold)
            isGibs = true;
        if (health > 0 && amount > 0) {
            if (MathUtils.random(0f, 1f) > (1 - painChance))
                setTemporaryHurtState();
            findPlayer();
        } else if (health <= 0)
            die();
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

    public void setPatrol(Array<Tile> path) {
        patrol = path;
        setNewAIPath(patrol.get(getPatrolIndex()));
        isActive = true;
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

    protected void checkIllumination() {
        attackDelayActor.clearActions();
        for (Tile tile : floorTiles) {
            if (overlaps(tile)) {
                GameUtils.illuminateBaseActor(this, tile);
                break;
            }
        }
    }

    protected void initializeSprite(float size) {
        sprite = new BaseActor3D(0, 0, 0, stage3D);
        sprite.buildModel(size, size, .001f, true);
    }

    private void handleSprite() {
        sprite.setPosition(position);
        angleTowardPlayer = GameUtils.getAngleTowardsBaseActor3D(this, player);
        sprite.setTurnAngle(angleTowardPlayer);
    }


    private void activateNearByEnemies() {
        for (Enemy enemy : enemies)
            if (enemy.isWithinDistance(60f, this))
                enemy.activate(player);
    }

    private int getDamage() {
        return MathUtils.random(minDamage, maxDamage);
    }

    private boolean isPlayerVisible() {
        if (isWithinDistance(VISIBILITY_RANGE, player) && (isActive || isDirectionFrontOrSides() || getClass().getSimpleName().equalsIgnoreCase("hund"))) {
            int i = GameUtils.getRayPickedListIndex(position, player.position.cpy().sub(position), shootable);
            if (i > -1) {
                if (!(shootable.get(i) instanceof Player) && !(shootable.get(i) instanceof Barrel))
                    return false;
                else
                    return true;
            }
        }
        return false;
    }

    private void forceMove(float dt) {
        if (totalTime <= forceTime)
            moveBy(0f, forceMove.x * dt, forceMove.y * dt);
        else
            isForcedToMove = false;
    }


    private void checkIfShotPlayerOrBarrel() {
        for (int i = 0; i < numShots; i++) {
            Vector3 playerPosition = player.position.cpy();
            playerPosition.y += MathUtils.random(-SHOOT_SPREAD_ANGLE, SHOOT_SPREAD_ANGLE);
            playerPosition.z += MathUtils.random(-SHOOT_SPREAD_ANGLE, SHOOT_SPREAD_ANGLE);

            Ray ray = new Ray(position, playerPosition.sub(position));
            consequencesOfPick(ray, GameUtils.getClosestListIndex(ray, shootable));
        }
    }

    private void consequencesOfPick(Ray ray, int i) {
        if (i > -1 && shootable.get(i) instanceof Barrel) {
            Barrel barrel = (Barrel) shootable.get(i);
            barrel.decrementHealth(getDamage(), distanceBetween(barrel));
        } else if (i > -1 && shootable.get(i) instanceof Player) {
            hud.decrementHealth(getDamage(), this);
        } else if (i > -1 && shootable.get(i) instanceof Tile && (((Tile) shootable.get(i)).type.equalsIgnoreCase("walls"))) {
            Vector3 temp = new Vector3().set(ray.direction).scl(distanceBetween(shootable.get(i)) - (Tile.diagonalLength / 2)).add(ray.origin);
            bulletDecals.addDecal(temp.x, temp.y, temp.z);
        }
    }

    private void attacking(float dt) {
        checkAttackStateChange(dt);
        if (isAttackDodging) {
            moveInZigZag(dt);
            if (state != State.HURT && state != State.WALKING) {
                state = State.WALKING;
                checkIllumination();
            }
        } else {
            attack(dt);
        }
    }

    private void checkAttackStateChange(float dt) {
        if (attackStateChangeCounter > attackStateChangeFrequency) {
            attackStateChangeCounter = 0;
            float temp = MathUtils.random(0f, 1f);
            if ((temp < distanceBetween(player) / rangeThreshold) || (!isRanged && !isWithinDistance(5f, player))) {
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
            if (baseActor3D instanceof Tile) {
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
        checkIllumination();
        setStateToIdleAfterDelay(.4f);
        currentAnimation = hurtAnimation;
    }

    private void setStateToIdleAfterDelay(float delay) {
        new BaseActor(0, 0, stage).addAction(Actions.sequence(
                Actions.delay(delay),
                Actions.run(() -> state = State.ATTACKING)
        ));
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


    private void setDirection() {
        angleTowardPlayer = GameUtils.getAngleTowardsBaseActor3D(this, player);
        float temp = angleTowardPlayer - getTurnAngle();
        while (temp < 0) temp += 360;

        if (temp <= 22.5 || temp >= 337.5) direction = Direction.FRONT;
        else if (temp < 337.5 && temp > 292.5f) direction = Direction.RIGHT_FRONT;
        else if (temp <= 292.5 && temp >= 247.5) direction = Direction.RIGHT_SIDE;
        else if (temp < 247.5 && temp > 202.5f) direction = Direction.RIGHT_BACK;
        else if (temp <= 202.5f && temp >= 157.5f) direction = Direction.BACK;
        else if (temp < 157.5 && temp > 112.5f) direction = Direction.LEFT_BACK;
        else if (temp <= 112.5 && temp >= 67.5f) direction = Direction.LEFT_SIDE;
        else if (temp < 67.5 && temp > 22.5f) direction = Direction.LEFT_FRONT;
    }

    protected void setDirectionalAnimation() {
        if (state == State.WALKING) {
            if (direction == Direction.FRONT)
                currentAnimation = walkFrontAnimation;
            else if (direction == Direction.LEFT_FRONT)
                currentAnimation = walkFrontSideLeftAnimation;
            else if (direction == Direction.LEFT_SIDE)
                currentAnimation = walkSideLeftAnimation;
            else if (direction == Direction.LEFT_BACK)
                currentAnimation = walkBackSideLeftAnimation;
            else if (direction == Direction.BACK)
                currentAnimation = walkBackAnimation;
            else if (direction == Direction.RIGHT_BACK)
                currentAnimation = walkBackSideRightAnimation;
            else if (direction == Direction.RIGHT_SIDE)
                currentAnimation = walkSideRightAnimation;
            else if (direction == Direction.RIGHT_FRONT)
                currentAnimation = walkFrontSideRightAnimation;
        } else if (state == State.IDLE) {
            if (direction == Direction.FRONT)
                currentAnimation = idleFrontAnimation;
            else if (direction == Direction.LEFT_FRONT)
                currentAnimation = idleFrontSideLeftAnimation;
            else if (direction == Direction.LEFT_SIDE)
                currentAnimation = idleSideLeftAnimation;
            else if (direction == Direction.LEFT_BACK)
                currentAnimation = idleBackSideLeftAnimation;
            else if (direction == Direction.BACK)
                currentAnimation = idleBackAnimation;
            else if (direction == Direction.RIGHT_BACK)
                currentAnimation = idleBackSideRightAnimation;
            else if (direction == Direction.RIGHT_SIDE)
                currentAnimation = idleSideRightAnimation;
            else if (direction == Direction.RIGHT_FRONT)
                currentAnimation = idleFrontSideRightAnimation;
        }
    }

    private boolean isDirectionFrontOrSides() {
        return direction == Direction.LEFT_FRONT || direction == Direction.RIGHT_FRONT ||
                direction == Direction.FRONT || direction == Direction.RIGHT_SIDE ||
                direction == Direction.LEFT_SIDE;
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
            if (getTileActorIsOn(this, floorTiles) != startingPosition) {
                isActive = false;
                state = State.IDLE;
                tilePath = null;
                new BaseActor(0, 0, stage).addAction(Actions.sequence(
                        Actions.delay(5f),
                        Actions.run(() -> {
                            if (patrol.size > 0)
                                setNewAIPath(patrol.get(getPatrolIndex()));
                            else
                                setNewAIPath(startingPosition);
                            isActive = true;
                            state = State.WALKING;
                        })
                ));
            }
        }
    }

    private int getPatrolIndex() {
        patrolIndex++;
        if (patrolIndex >= patrol.size)
            patrolIndex = 0;
        return patrolIndex;
    }

    private void setPathToLastKnownPlayerPosition() {
        try {
            if (!isPlayerVisible && isPlayerLastPositionKnown) {
                tilePath = getPathTo(player);
                isPlayerLastPositionKnown = false;
            }
        } catch (Exception exception) {
            Gdx.app.error(getClass().getSimpleName(), "setPathToLastKnownPlayerPosition() failed => " + exception);
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
}

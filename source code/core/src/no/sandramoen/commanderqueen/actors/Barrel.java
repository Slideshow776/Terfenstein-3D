package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Barrel extends BaseActor3D {
    public int health = 20;
    public final float BLAST_RANGE = 15;

    private boolean explode;
    private float totalTime;
    private Stage3D stage3D;
    private Animation<TextureRegion> explodeAnimation;
    private final float BLAST_DAMAGE_MODIFIER = 8.5f;
    private Decal decal;
    private DecalBatch decalBatch;

    public Barrel(float y, float z, Stage3D stage3D, DecalBatch decalBatch, Array<Tile> tiles) {
        super(0, y, z, stage3D);
        this.stage3D = stage3D;
        this.decalBatch = decalBatch;

        buildModel(2.8f, 3.5f, 2.8f, true);
        setPosition(GameUtils.getPositionRelativeToFloor(3.5f), y, z);
        setBaseRectangle();
        isVisible = false;

        initializeDecal(y, z);
        initializeExplosionAnimation();
        checkIfIlluminated(tiles);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        totalTime += Gdx.graphics.getDeltaTime();
        GameUtils.lookAtCameraIn2D(decal, stage3D.camera);
        if (explode)
            decal.setTextureRegion(explodeAnimation.getKeyFrame(totalTime));
        decalBatch.add(decal);
    }

    public void explode() {
        if (!explode) {
            explode = true;
            isPreventOverlapEnabled = false;
            health = 0;
            totalTime = 0;
            BaseGame.explosionSound.play(BaseGame.soundVolume);
            stage3D.lightManager.addSmallExplosion(position);
        }
    }

    public void decrementHealth(int amount, float distance) {
        if (!explode) {
            health -= amount;
            if (health > 0 && amount > 0)
                GameUtils.playSoundRelativeToDistance(BaseGame.metalSound, distance, 15f, MathUtils.random(.7f, 1.4f));
        }
    }

    public int getBlastDamage(float range) {
        if (explode)
            return 0;

        float damage = BLAST_RANGE - range;
        if (damage < 0)
            damage = 0;

        return (int) (damage * BLAST_DAMAGE_MODIFIER);
    }

    private void checkIfIlluminated(Array<Tile> tiles) {
        for (Tile tile : tiles) {
            if (overlaps(tile)) {
                GameUtils.illuminateDecal(decal, tile);
                break;
            }
        }
    }

    private void initializeDecal(float y, float z) {
        decal = Decal.newDecal(BaseGame.textureAtlas.findRegion("barrel/barrel"), true);
        decal.setDimensions(2.8f, 3.5f);
        decal.setPosition(GameUtils.getPositionRelativeToFloor(3.5f), y, z);
    }

    private void initializeExplosionAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 1; i <= 8; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("barrel/barrel explode " + i));
        explodeAnimation = new Animation(.2f, animationImages, Animation.PlayMode.NORMAL);
        animationImages.clear();
    }
}

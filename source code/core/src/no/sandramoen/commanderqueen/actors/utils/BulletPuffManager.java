package no.sandramoen.commanderqueen.actors.utils;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.utils.BaseGame;

public class BulletPuffManager {
    private PerspectiveCamera camera;
    private DecalBatch batch;
    private Array<MyDecal> decals = new Array();

    public BulletPuffManager(PerspectiveCamera camera, DecalBatch batch) {
        this.camera = camera;
        this.batch = batch;
    }

    public void render(float dt) {
        for (MyDecal decal : decals) {
            decal.totalTime += dt;
            if (decal.animation.isAnimationFinished(decal.totalTime)) {
                decals.removeValue(decal, false);
            } else {
                decal.decal.setTextureRegion((TextureRegion) decal.animation.getKeyFrame(decal.totalTime));
                decal.decal.lookAt(camera.position, camera.up);
                batch.add(decal.decal);
            }
        }
    }

    public void addNewBulletPuff(float x, float y, float z) {
        MyDecal decal = new MyDecal(x, y, z);
        decals.add(decal);
    }

    private class MyDecal {
        public float totalTime;
        public Decal decal;
        public Animation animation;

        public MyDecal(float x, float y, float z) {
            initializeDecal(x, y, z);
            initializeAnimation();
        }

        private void initializeDecal(float x, float y, float z) {
            decal = Decal.newDecal(BaseGame.textureAtlas.findRegion("bullet puff/bullet puff 1"), true);
            decal.setDimensions(MathUtils.random(.8f, 1.2f), MathUtils.random(.8f, 1.2f));
            decal.setPosition(x, y, z);
        }

        private void initializeAnimation() {
            Array<TextureAtlas.AtlasRegion> animationImages = new Array();
            for (int i = 1; i < 5; i++)
                animationImages.add(BaseGame.textureAtlas.findRegion("bullet puff/bullet puff " + i));
            animation = new Animation(MathUtils.random(.1f, .3f), animationImages, Animation.PlayMode.NORMAL);
        }
    }
}

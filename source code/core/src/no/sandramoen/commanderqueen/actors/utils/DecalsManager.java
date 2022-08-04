package no.sandramoen.commanderqueen.actors.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.utils.BaseGame;

public class DecalsManager {
    protected Array<String> imagePaths;
    protected float minAnimationSpeed;
    protected float maxAnimationSpeed;

    private PerspectiveCamera camera;
    private DecalBatch batch;
    private Array<MyDecal> decals = new Array();

    public DecalsManager(PerspectiveCamera camera, DecalBatch batch) {
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

    public void addDecal(float x, float y, float z) {
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
            decal = Decal.newDecal(BaseGame.textureAtlas.findRegion(imagePaths.get(0)), true);
            decal.setDimensions(MathUtils.random(.8f, 1.2f), MathUtils.random(.8f, 1.2f));
            decal.setPosition(x, y, z);/*
            decal.setScale(2);*/
        }

        private void initializeAnimation() {
            if (imagePaths.size == 0)
                Gdx.app.error(getClass().getSimpleName(), "Image path array was empty");

            Array<TextureAtlas.AtlasRegion> animationImages = new Array();
            for (int i = 0; i < imagePaths.size; i++)
                animationImages.add(BaseGame.textureAtlas.findRegion(imagePaths.get(i)));
            animation = new Animation(MathUtils.random(minAnimationSpeed, maxAnimationSpeed), animationImages, Animation.PlayMode.NORMAL);
        }
    }
}

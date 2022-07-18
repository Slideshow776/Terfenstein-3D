package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.utils.Enemy;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Ghoul extends Enemy {
    private Animation<TextureRegion> walkAnimation;
    private float totalTime = 0f;

    public Ghoul(float y, float z, Stage3D s, Player player) {
        super(y, z, s, player);
        loadImage("enemies/ghoul walk 1");

        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/ghoul walk 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/ghoul walk 2"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/ghoul walk 3"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/ghoul walk 4"));
        walkAnimation = new Animation(.15f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();
    }

    @Override
    public void draw(ModelBatch batch, Environment env) {
        super.draw(batch, env);
        totalTime += Gdx.graphics.getDeltaTime();
        loadImage(walkAnimation.getKeyFrame(totalTime).toString());
    }
}

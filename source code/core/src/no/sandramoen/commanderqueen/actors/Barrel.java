package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Barrel extends BaseActor3D {
    private Player player;
    private float totalTime = 0;
    private boolean explode = false;
    private Animation<TextureRegion> explodeAnimation;

    public Barrel(float y, float z, Stage3D s, Player player) {
        super(0, y, z, s);
        this.player = player;
        buildModel(1f, 3f, .001f);
        setPosition(GameUtils.getPositionRelativeToFloor(3f), y, z);
        setBaseRectangle();
        loadImage("barrel");

        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("barrel explode 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("barrel explode 2"));
        animationImages.add(BaseGame.textureAtlas.findRegion("barrel explode 3"));
        explodeAnimation = new Animation(.15f, animationImages, Animation.PlayMode.NORMAL);
        animationImages.clear();
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        setTurnAngle(GameUtils.getAngleTowardsPlayer(this, player));
    }

    @Override
    public void draw(ModelBatch batch, Environment env) {
        super.draw(batch, env);
        if (explode) {
            totalTime += Gdx.graphics.getDeltaTime();
            loadImage(explodeAnimation.getKeyFrame(totalTime).toString());

            if (totalTime >= .45f)
                remove();
        }
    }

    public void explode() {
        explode = true;
        BaseGame.explosionSound.play(BaseGame.soundVolume);
    }
}

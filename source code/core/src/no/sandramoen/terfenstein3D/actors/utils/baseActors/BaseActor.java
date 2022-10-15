package no.sandramoen.terfenstein3D.actors.utils.baseActors;

import static java.lang.Math.abs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.terfenstein3D.utils.BaseGame;

public class BaseActor extends Group {
    public boolean pause = false;
    public boolean isFacingRight = true;
    public float animationWidth = getWidth();
    public float animationHeight = getWidth();

    private Animation<TextureRegion> animation;
    private float animationTime;
    private boolean animationPaused;

    public BaseActor(float x, float y, Stage stage) {
        super();

        setPosition(x, y);
        stage.addActor(this);

        animation = null;
        animationTime = 0;
        animationPaused = false;
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        setAnimationSize(width, height);
    }

    @Override
    public void act(float delta) {
        if (!pause)
            super.act(delta);

        if (!animationPaused)
            animationTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a);

        if (animation != null && isVisible()) {
            if (isFacingRight)
                batch.draw(
                        animation.getKeyFrame(animationTime),
                        getX() + abs(getWidth() - animationWidth) / 2,
                        getY() + abs(getHeight() - animationHeight) / 2,
                        getOriginX(),
                        getOriginY(),
                        animationWidth,
                        animationHeight,
                        getScaleX(),
                        getScaleY(),
                        getRotation()
                );
            else
                batch.draw(
                        animation.getKeyFrame(animationTime),
                        getX() + getWidth(),
                        getY(),
                        getOriginX() - getWidth(),
                        getOriginY(),
                        -getWidth(),
                        getHeight(),
                        getScaleX(),
                        getScaleY(),
                        getRotation()
                );
        }
        super.draw(batch, parentAlpha);
    }

    private void setAnimationSize(Float width, Float height) {
        animationWidth = width;
        animationHeight = height;
    }

    public void flip() {
        isFacingRight = !isFacingRight;
    }

    public void setAnimationPaused(Boolean pause) {
        animationPaused = pause;
    }

    public Boolean isAnimationFinished() {
        return animation.isAnimationFinished(animationTime);
    }

    public Animation<TextureRegion> loadTexture(String fileName) {
        if (fileName.isEmpty())
            Gdx.app.error(getClass().getSimpleName(), "Error: Texture path is invalid: " + fileName);
        Array<String> fileNames = new Array(1);
        fileNames.add(fileName);
        return loadAnimationFromFiles(fileNames, 1f, true);
    }

    private Animation<TextureRegion> loadAnimationFromFiles(Array<String> fileNames, Float frameDuration, Boolean loop) {
        Array<TextureRegion> textureArray = new Array();

        for (int i = 0; i < fileNames.size; i++) {
            Texture texture = new Texture(Gdx.files.internal(fileNames.get(i)));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textureArray.add(new TextureRegion(texture));
        }

        Animation<TextureRegion> anim = new Animation(frameDuration, textureArray);

        if (loop)
            anim.setPlayMode(Animation.PlayMode.LOOP);
        else
            anim.setPlayMode(Animation.PlayMode.NORMAL);

        if (animation == null)
            setAnimation(anim);

        return anim;
    }

    public void setAnimation(Animation<TextureRegion> anim) {
        animation = anim;
        TextureRegion tr = animation.getKeyFrame(0);
        float w = tr.getRegionWidth();
        float h = tr.getRegionHeight();
        setSize(w, h);
        setOrigin(w / 2, h / 2);
    }

    public void loadImage(String name) {
        TextureRegion region = BaseGame.textureAtlas.findRegion(name);
        if (region == null)
            Gdx.app.error(getClass().getSimpleName(), "Error: region is null. Are you sure the image '" + name + "' exists?");
        setAnimation(new Animation(1f, region));
    }

    // miscellaneous -------------------------------------------------------------------------------------------
    public void centerAtPosition(Float x, Float y) {
        setPosition(x - getWidth() / 2, y - getHeight() / 2);
    }

    public void centerAtActor(BaseActor baseActor) {
        centerAtPosition(baseActor.getX() + baseActor.getWidth() / 2, baseActor.getY() + baseActor.getHeight() / 2);
    }

    public void setOpacity(Float opacity) {
        this.getColor().a = opacity;
    }
}

package no.sandramoen.commanderqueen.actors.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.particles.BlueKeyEffect;
import no.sandramoen.commanderqueen.actors.particles.GreenKeyEffect;
import no.sandramoen.commanderqueen.actors.particles.ParticleActor;
import no.sandramoen.commanderqueen.actors.particles.RedKeyEffect;
import no.sandramoen.commanderqueen.actors.pickups.Key;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor;
import no.sandramoen.commanderqueen.utils.BaseGame;

public class Keys extends Table {
    private Array<Key> keys;

    private BaseActor redKey;
    private BaseActor blueKey;
    private BaseActor greenKey;

    private Stage stage;

    public Keys(float x, float y, float width, float height, Stage stage) {
        this.stage = stage;
        keys = new Array();

        redKey = initializeKey(BaseGame.redColor, width, height);
        redKey.setVisible(false);
        blueKey = initializeKey(BaseGame.blueColor, width, height);
        blueKey.setVisible(false);
        greenKey = initializeKey(BaseGame.greenColor, width, height);
        greenKey.setVisible(false);

        defaults().spaceTop(height * .18f);
        add(redKey).row();
        add(blueKey).row();
        add(greenKey).row();
        /*setDebug(true);*/

        setWidth(Gdx.graphics.getWidth() * HUD.WIDTH * .04f);
        setSize(getWidth(), getWidth() / (5 / 1f));
        setPosition(width * .598f, height / 2);
    }

    public void addKey(Key key) {
        keys.add(key);

        if (key.color.equalsIgnoreCase("red")) {
            redKey.setVisible(true);
            redKey.addAction(wiggle());
            addEffect(new RedKeyEffect(), redKey);
        } else if (key.color.equalsIgnoreCase("blue")) {
            blueKey.setVisible(true);
            blueKey.addAction(wiggle());
            addEffect(new BlueKeyEffect(), blueKey);
        } else if (key.color.equalsIgnoreCase("green")) {
            greenKey.setVisible(true);
            greenKey.addAction(wiggle());
            addEffect(new GreenKeyEffect(), greenKey);
        } else {
            Gdx.app.error(getClass().getSimpleName(), "Error: Couldn't add key: " + key);
        }
    }

    public Array<Key> getKeys() {
        return keys;
    }

    private void addEffect(ParticleActor effect, BaseActor key) {
        effect.setScale(.5f);
        effect.centerAtActor(key);
        addActor(effect);
        effect.start();
    }

    private SequenceAction wiggle() {
        float amount = 10;
        float delay = .1f;
        return Actions.sequence(
                Actions.rotateTo(amount, delay),
                Actions.rotateTo(-amount, 2 * delay),
                Actions.rotateTo(amount, delay),
                Actions.rotateTo(-amount, 2 * delay),
                Actions.rotateTo(amount, delay),
                Actions.rotateTo(-amount, 2 * delay),
                Actions.rotateTo(amount, delay),
                Actions.rotateTo(-amount, 2 * delay),
                Actions.rotateTo(amount, delay),
                Actions.rotateTo(-amount, 2 * delay),
                Actions.rotateTo(amount, delay),
                Actions.rotateTo(-amount, 2 * delay),
                Actions.rotateTo(amount, delay),
                Actions.rotateTo(-amount, 2 * delay),
                Actions.rotateTo(0, delay)
        );
    }

    private BaseActor initializeKey(Color color, float width, float height) {
        BaseActor key = new BaseActor(0, 0, stage);
        key.loadImage("hud/key");
        key.setSize(width * .04f, height * .1f);
        key.setColor(color);
        key.setOrigin(Align.center);
        return key;
    }
}

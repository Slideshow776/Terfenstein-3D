package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Door extends BaseActor3D {

    public boolean isLocked;
    private boolean isOpening;
    private boolean isClosing;
    private float speed = .04f;
    private float openHeight = Tile.height * .9f;

    private BaseActor openActor;
    private BaseActor closeActor;

    private Player player;

    public Door(float y, float z, Stage3D stage3D, Stage stage, float rotation, Player player) {
        super(0, y, z, stage3D);
        this.player = player;

        buildModel(4, 4, .25f, false);
        loadImage("door0");
        turnBy(-180 + rotation);
        setBaseRectangle();

        openActor = new BaseActor(0, 0, stage);
        closeActor = new BaseActor(0, 0, stage);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        checkIfShouldOpenDoor();
        checkIfShouldCloseDoor();
    }

    public void open() {
        if (isLocked) return;
        if (!isOpening)
            GameUtils.playSoundRelativeToDistance(BaseGame.door0OpeningSound, distanceBetween(player), 16f, MathUtils.random(.8f, 1.2f));
        isOpening = true;

        if (isClosing)
            BaseGame.door0ClosingSound.stop();
        isClosing = false;

        openActor.clearActions();
        openActor.addAction(Actions.sequence(
                Actions.delay(.7f),
                Actions.run(() -> isPreventOverlapEnabled = false)
        ));
    }

    public void close() {
        if (!isClosing)
            GameUtils.playSoundRelativeToDistance(BaseGame.door0ClosingSound, distanceBetween(player), 16f, MathUtils.random(.8f, 1.2f));
        isClosing = true;

        if (isOpening)
            BaseGame.door0OpeningSound.stop();
        isOpening = false;

        isPreventOverlapEnabled = true;
    }

    public void openAndClose() {
        if (isLocked || getPosition().x >= openHeight) return;
        open();
        closeActor.clearActions();
        closeActor.addAction(Actions.sequence(
                Actions.delay(5f),
                Actions.run(() -> close())
        ));
    }

    private void checkIfShouldOpenDoor() {
        if (isOpening && getPosition().x < openHeight)
            setPosition(getPosition().x + speed, getPosition().y, getPosition().z);
        else
            isOpening = false;
    }

    private void checkIfShouldCloseDoor() {
        if (isClosing && getPosition().x > 0)
            setPosition(getPosition().x - speed, getPosition().y, getPosition().z);
        else
            isClosing = false;
    }
}

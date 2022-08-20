package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.math.Vector3;

import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Tile extends BaseActor3D {
    public static float height = 4;
    public static float diagonalLength = (float) Math.sqrt(2 * Math.pow(height, 2));

    public int index;
    public String type;
    public boolean illuminated = false;
    public boolean isOpeningSecret;

    public String secretMovementDirection;
    private float speed = .04f;
    private long secretSoundID;
    private Vector3 originalPosition;
    private int secretLength;

    public Tile(float y, float z, float width, float height, float depth, String type, String texture, Stage3D stage3D, float rotation, String secretMovementDirection, int secretLength) {
        super(0, y, z, stage3D);
        this.type = type;
        this.secretMovementDirection = secretMovementDirection;
        this.secretLength = secretLength;

        buildModel(width, height, depth, false);
        setBaseRectangle();
        loadImage("tiles/" + texture);
        turnBy(-180 + rotation);
        if (texture.split(" ", 2)[0].equals("light"))
            illuminated = true;

        if (type == "ceilings") {
            position.x = Tile.height;
            isCollisionEnabled = false;
        } else if (type == "floors") {
            position.x = -Tile.height;
        }
        originalPosition = getPosition().cpy();
    }

    @Override
    public void act(float dt) {
        super.act(dt);

        if (isOpeningSecret) {
            openSecret();
        }
    }

    public boolean isSecretTriggered() {
        if (isOpeningSecret)
            return true;
        isOpeningSecret = true;
        secretSoundID = BaseGame.secretWallSound.play(BaseGame.soundVolume);
        return false;
    }

    private void openSecret() {
        if (secretMovementDirection.equalsIgnoreCase("up"))
            openSecretThatGoesUp();
        else if (secretMovementDirection.equalsIgnoreCase("down"))
            openSecretThatGoesDown();
        else if (secretMovementDirection.equalsIgnoreCase("north"))
            openSecretThatGoesNorth();
        else if (secretMovementDirection.equalsIgnoreCase("east"))
            openSecretThatGoesEast();
        else if (secretMovementDirection.equalsIgnoreCase("south"))
            openSecretThatGoesSouth();
        else if (secretMovementDirection.equalsIgnoreCase("west"))
            openSecretThatGoesWest();
    }

    private void openSecretThatGoesUp() {
        if (isOpeningSecret && getPosition().x < height * secretLength)
            setPosition(getPosition().x + speed, getPosition().y, getPosition().z);
        else if (isOpeningSecret) {
            isCollisionEnabled = false;
            isVisible = false;
            BaseGame.secretWallSound.stop(secretSoundID);
        }
    }

    private void openSecretThatGoesDown() {
        if (isOpeningSecret && getPosition().x > -height * secretLength)
            setPosition(getPosition().x - speed, getPosition().y, getPosition().z);
        else if (isOpeningSecret) {
            isCollisionEnabled = false;
            isVisible = false;
            BaseGame.secretWallSound.stop(secretSoundID);
        }
    }

    private void openSecretThatGoesNorth() {
        if (isOpeningSecret && getPosition().z < originalPosition.z + height * secretLength)
            setPosition(getPosition().x, getPosition().y, getPosition().z + speed);
        else if (isOpeningSecret) {
            isCollisionEnabled = false;
            isVisible = false;
            BaseGame.secretWallSound.stop(secretSoundID);
        }
    }

    private void openSecretThatGoesSouth() {
        if (isOpeningSecret && getPosition().z > originalPosition.z - height * secretLength)
            setPosition(getPosition().x, getPosition().y, getPosition().z - speed);
        else if (isOpeningSecret) {
            isCollisionEnabled = false;
            isVisible = false;
            BaseGame.secretWallSound.stop(secretSoundID);
        }
    }

    private void openSecretThatGoesWest() {
        if (isOpeningSecret && getPosition().y > originalPosition.y - height * secretLength)
            setPosition(getPosition().x, getPosition().y - speed, getPosition().z);
        else if (isOpeningSecret) {
            isCollisionEnabled = false;
            isVisible = false;
            BaseGame.secretWallSound.stop(secretSoundID);
        }
    }

    private void openSecretThatGoesEast() {
        if (isOpeningSecret && getPosition().y < originalPosition.y + height * secretLength)
            setPosition(getPosition().x, getPosition().y + speed, getPosition().z);
        else if (isOpeningSecret) {
            isCollisionEnabled = false;
            isVisible = false;
            BaseGame.secretWallSound.stop(secretSoundID);
        }
    }
}

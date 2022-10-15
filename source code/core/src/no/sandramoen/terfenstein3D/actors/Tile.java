package no.sandramoen.terfenstein3D.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.terfenstein3D.utils.BaseGame;
import no.sandramoen.terfenstein3D.utils.Stage3D;

public class Tile extends BaseActor3D {
    public static float height = 4;
    public static float diagonalLength = (float) Math.sqrt(2 * Math.pow(height, 2));

    public int index;
    public String type;
    public boolean isAIpath;

    public String secretMovementDirection;
    private boolean isOpeningSecret;
    private float speed = .04f;
    private long secretSoundID;
    private Vector3 originalPosition;
    private int secretLength;
    private String texture;

    public Tile(float y, float z, float width, float height, float depth, String type, String texture, Stage3D stage3D, float rotation, String secretMovementDirection, int secretLength, boolean isAIpath) {
        super(0, y, z, stage3D);
        this.type = type;
        this.secretMovementDirection = secretMovementDirection;
        this.secretLength = secretLength;
        this.isAIpath = isAIpath;
        this.texture = texture;

        if (texture.equalsIgnoreCase("blank"))
            buildModel(width, height, depth, true);
        else
            buildModel(width, height, depth, false);
        setBaseRectangle();
        loadImage("tiles/" + texture);
        turnBy(-180 + rotation);

        if (type == "4th floor")
            position.x = Tile.height * 3;
        else if (type == "3rd floor")
            position.x = Tile.height * 2;
        else if (type == "2nd floor")
            position.x = Tile.height;
        else if (type == "U1")
            position.x = -Tile.height;

        originalPosition = getPosition().cpy();
        // checkSecretMovementDirection();
    }

    @Override
    public void act(float dt) {
        super.act(dt);

        if (isOpeningSecret)
            openSecret();
    }

    public boolean isSecretTriggered() {
        if (isOpeningSecret)
            return true;
        isOpeningSecret = true;
        secretSoundID = BaseGame.secretWallSound.play(BaseGame.soundVolume * 1.5f);
        return false;
    }

    public void setBloody() {
        loadImage("tiles/bloody/" + texture);
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

    private void checkSecretMovementDirection() { // TODO: this is not working as intended, gives error for all tiles...
        if (!secretMovementDirection.equalsIgnoreCase("up") ||
                !secretMovementDirection.equalsIgnoreCase("down") ||
                !secretMovementDirection.equalsIgnoreCase("north") ||
                !secretMovementDirection.equalsIgnoreCase("east") ||
                !secretMovementDirection.equalsIgnoreCase("south") ||
                !secretMovementDirection.equalsIgnoreCase("west"))
            Gdx.app.error(getClass().getSimpleName(), "Error: Door's secret movement direction is invalid " + secretMovementDirection + ".");
    }
}

package no.sandramoen.commanderqueen.utils;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;

import no.sandramoen.commanderqueen.actors.Player;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;

public class GameUtils {
    public static float getAngleTowardsPlayer(BaseActor3D baseActor3D, Player player) {
        float angleTowardsPlayer = MathUtils.atan(
                Math.abs(baseActor3D.position.z - player.position.z) /
                        Math.abs(baseActor3D.position.y - player.position.y)
        ) * MathUtils.radiansToDegrees - 90;

        if (baseActor3D.position.y - player.position.y > 0) {
            angleTowardsPlayer *= -1;
        }

        if (baseActor3D.position.z - player.position.z > 0) {
            angleTowardsPlayer *= -1;
            angleTowardsPlayer += 180;
        }

        return angleTowardsPlayer;
    }

    public static float getPositionRelativeToFloor(float height) {
        return (Tile.height - height) / -2;
    }

    public static void playLoopingMusic(Music music) {
        music.setVolume(BaseGame.musicVolume);
        music.setLooping(true);
        music.play();
    }
}

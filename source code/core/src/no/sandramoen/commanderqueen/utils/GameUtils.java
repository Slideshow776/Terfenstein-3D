package no.sandramoen.commanderqueen.utils;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;

public class GameUtils {
    public static float getAngleTowardsPlayer(BaseActor3D baseActor3D, Player player) {
        float angleTowardsPlayer = MathUtils.atan(
                Math.abs(baseActor3D.position.z - player.position.z) /
                        Math.abs(baseActor3D.position.y - player.position.y)
        ) * MathUtils.radiansToDegrees - 90;

        if (baseActor3D.position.y - player.position.y > 0)
            angleTowardsPlayer *= -1;

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

    public static void playLoopingMusic(Music music, float volume) {
        music.setVolume(volume);
        music.setLooping(true);
        music.play();
    }

    public static int getRayPickedListIndex(Vector3 origin, Vector3 direction, Array<BaseActor3D> list) {
        Ray ray = new Ray(origin, direction);
        return getClosestListItem(ray, list);
    }

    public static int getRayPickedListIndex(int screenX, int screenY, Array<BaseActor3D> list, PerspectiveCamera camera) {
        Ray ray = camera.getPickRay(screenX, screenY);
        return getClosestListItem(ray, list);
    }

    private static int getClosestListItem(Ray ray, Array<BaseActor3D> list) {
        int result = -1;
        float distance = -1;
        for (int i = 0; i < list.size; ++i) {
            final float dist2 = list.get(i).modelData.intersects(ray);
            if (dist2 >= 0f && (distance < 0f || dist2 <= distance)) {
                result = i;
                distance = dist2;
            }
        }
        return result;
    }
}

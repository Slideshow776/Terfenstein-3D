package no.sandramoen.commanderqueen.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;

public class GameUtils {

    public static float getAngleTowardsBaseActor3D(BaseActor3D actorA, BaseActor3D actorB) {
        float angle = MathUtils.atan(
                Math.abs(actorA.position.z - actorB.position.z) /
                        Math.abs(actorA.position.y - actorB.position.y)
        ) * MathUtils.radiansToDegrees - 90;

        if (actorA.position.y - actorB.position.y > 0)
            angle *= -1;

        if (actorA.position.z - actorB.position.z > 0) {
            angle *= -1;
            angle += 180;
        }

        return angle;
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

    public static float normalizeValue(float value, float min, float max) {
        return (value - min) / (max - min);
    }

    public static void playSoundRelativeToDistance(Sound sound, float distance, float vocalRange) {
        sound.play(BaseGame.soundVolume / GameUtils.normalizeValue(distance, 0f, vocalRange));
    }

    public static void playSoundRelativeToDistance(Sound sound, Float distance, Float vocalRange, Float pitch) {
        sound.play(BaseGame.soundVolume / GameUtils.normalizeValue(distance, 0f, vocalRange), pitch, 0);
    }

    public static void printLoadingTime(String tag, long startTime) {
        long endTime = System.currentTimeMillis();
        Gdx.app.log(tag, "took " + (endTime - startTime) + " ms to load.");
    }

    public static int getRayPickedListIndex(Vector3 origin, Vector3 direction, Array<BaseActor3D> list) {
        Ray ray = new Ray(origin, direction);
        return getClosestListIndex(ray, list);
    }

    public static int getRayPickedListIndex(int screenX, int screenY, Array<BaseActor3D> list, PerspectiveCamera camera) {
        Ray ray = camera.getPickRay(screenX, screenY);
        return getClosestListIndex(ray, list);
    }

    public static int getClosestListIndex(Ray ray, Array<BaseActor3D> list) {
        int index = -1;
        float distance = -1;
        for (int i = 0; i < list.size; ++i) {
            final float dist2 = list.get(i).modelData.intersects(ray);
            if (dist2 >= 0f && (distance < 0f || dist2 <= distance)) {
                index = i;
                distance = dist2;
            }
        }
        return index;
    }
}

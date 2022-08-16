package no.sandramoen.commanderqueen.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.pickups.Pickup;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;

public class GameUtils {

    public static void saveGameState() {
        BaseGame.preferences.putBoolean("loadPersonalParameters", true);
        BaseGame.preferences.putFloat("musicVolume", BaseGame.musicVolume);
        BaseGame.preferences.putFloat("soundVolume", BaseGame.soundVolume);
        BaseGame.preferences.putFloat("voiceVolume", BaseGame.voiceVolume);
        BaseGame.preferences.putFloat("mouseMovementSensitivity", BaseGame.mouseMovementSensitivity);
        BaseGame.preferences.flush();
    }

    public static void loadGameState() {
        BaseGame.preferences = Gdx.app.getPreferences("Terfenstein3DGameState");
        BaseGame.loadPersonalParameters = BaseGame.preferences.getBoolean("loadPersonalParameters");
        BaseGame.musicVolume = BaseGame.preferences.getFloat("musicVolume");
        BaseGame.soundVolume = BaseGame.preferences.getFloat("soundVolume");
        BaseGame.voiceVolume = BaseGame.preferences.getFloat("voiceVolume");
        BaseGame.mouseMovementSensitivity = BaseGame.preferences.getFloat("mouseMovementSensitivity");
    }

    public static void setWidgetHoverColor(Widget widget) {
        widget.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                widget.setColor(BaseGame.redColor);
                BaseGame.hoverOverEnterSound.play(BaseGame.soundVolume);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                widget.setColor(BaseGame.whiteColor);
            }
        });
    }

    public static void lookAtCameraIn2D(Decal decal, PerspectiveCamera camera) {
        Vector3 temp = camera.position.cpy();
        temp.x = decal.getX();
        decal.lookAt(temp, camera.up);
    }

    public static float getAngleTowardsBaseActor3D(BaseActor3D actorA, BaseActor3D actorB) { // TODO: still using this?
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

    public static void illuminateBaseActor(BaseActor3D baseActor3D, Tile tile) {
        if (tile.type == "floors" && tile.illuminated)
            baseActor3D.setColor(Color.WHITE);
        else if (tile.type == "floors")
            baseActor3D.setColor(BaseGame.darkColor);
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

    public static boolean isTouchDownEvent(Event event) {
        return event instanceof InputEvent && ((InputEvent) event).getType() == InputEvent.Type.touchDown;
    }

    public static ShaderProgram initShaderProgram(String vertexShader, String fragmentShader) {
        ShaderProgram.pedantic = false;
        ShaderProgram shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        if (!shaderProgram.isCompiled())
            Gdx.app.error(GameUtils.class.getSimpleName(), "Error: Couldn't compile shader => " + shaderProgram.getLog());
        return shaderProgram;
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

package no.sandramoen.commanderqueen.actors.characters.enemy;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;

import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;

public class Sprite {

    public static com.badlogic.gdx.graphics.g3d.decals.Decal init(float size) {
        com.badlogic.gdx.graphics.g3d.decals.Decal decal = com.badlogic.gdx.graphics.g3d.decals.Decal.newDecal(BaseGame.textureAtlas.findRegion("whitePixel"), true);
        decal.setDimensions(size, size);
        decal.setColor(BaseGame.darkColor);
        return decal;
    }

    public static void update(com.badlogic.gdx.graphics.g3d.decals.Decal decal, Vector3 position, PerspectiveCamera camera, DecalBatch decalBatch) {
        decal.setPosition(position);
        GameUtils.lookAtCameraIn2D(decal, camera);
        decalBatch.add(decal);
    }
}

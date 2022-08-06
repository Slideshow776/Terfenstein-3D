package no.sandramoen.commanderqueen.actors.decals;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.utils.DecalsManager;

public class BloodDecals extends DecalsManager {
    public BloodDecals(PerspectiveCamera camera, DecalBatch batch) {
        super(camera, batch);

        Array<String> temp = new Array();
        for (int i = 1; i < 5; i++)
            temp.add("decals/blood decal " + i);
        imagePaths = temp;
        minAnimationSpeed = .1f;
        maxAnimationSpeed = .1f;
    }
}

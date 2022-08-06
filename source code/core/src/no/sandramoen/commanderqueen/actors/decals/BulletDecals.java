package no.sandramoen.commanderqueen.actors.decals;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.utils.Array;

public class BulletDecals extends DecalsManager {
    public BulletDecals(PerspectiveCamera camera, DecalBatch batch) {
        super(camera, batch);

        Array<String> temp = new Array();
        for (int i = 1; i < 5; i++)
            temp.add("decals/bullet puff " + i);
        imagePaths = temp;
        minAnimationSpeed = .1f;
        maxAnimationSpeed = .3f;
    }
}

package no.sandramoen.commanderqueen.utils.level;

import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.MathUtils;

public class MyPointLight extends PointLight {
    protected int id = MathUtils.random(1_000, 9_999);
    protected float duration;
    protected float brightnessFalloff;
    protected float counter = 0;
    protected float peakIntensity;
}

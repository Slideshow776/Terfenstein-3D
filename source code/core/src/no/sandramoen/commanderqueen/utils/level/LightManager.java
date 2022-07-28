package no.sandramoen.commanderqueen.utils.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class LightManager {
    private Environment environment;
    private Array<MyPointLight> pointLights;

    public LightManager(Environment environment) {
        this.environment = environment;
        pointLights = new Array();

        /*DirectionalLight dLight = new DirectionalLight();
        Color lightColor = new Color(0.0f, 0.0f, 0.9f, 1);
        Vector3 lightVector = new Vector3(Tile.height / 2, 28.25f, 10.35f);
        dLight.set(lightColor, lightVector);
        environment.add(dLight);*/
    }

    public void update(float dt) {
        updatePointLights(dt);
    }

    public void addPointLight(Vector3 position, float r, float g, float b, float intensity, float duration, float brightnessFalloff) {
        if (duration <= brightnessFalloff)
            Gdx.app.error(getClass().getSimpleName(), "Warning: a light was added where duration is less than or equal to the brightness falloff!");
        MyPointLight pointLight = new MyPointLight();
        pointLight.set(new Color(r, g, b, 1f), position, 0);
        pointLight.duration = duration;
        pointLight.brightnessFalloff = brightnessFalloff;
        pointLight.peakIntensity = intensity;
        environment.add(pointLight);
        pointLights.add(pointLight);
    }

    public void setAmbient() {
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .5f, 1f));
    }

    private void updatePointLights(float dt) {
        for (MyPointLight light : pointLights) {
            light.counter += dt;
            fadeInAndOutWithLinearInterpolation(light);
            checkToRemovePointLight(light);
        }
    }

    private void fadeInAndOutWithLinearInterpolation(MyPointLight light) {
        if (light.counter <= light.brightnessFalloff) {
            light.intensity += light.peakIntensity / (light.brightnessFalloff * Gdx.graphics.getFramesPerSecond());
        } else if (light.counter >= light.duration - light.brightnessFalloff) {
            light.intensity -= light.peakIntensity / (light.brightnessFalloff * Gdx.graphics.getFramesPerSecond());
        }
    }

    private void checkToRemovePointLight(MyPointLight light) {
        if (light.counter > light.duration) {
            environment.remove(light);
            pointLights.removeValue(light, false);
        }
    }
}

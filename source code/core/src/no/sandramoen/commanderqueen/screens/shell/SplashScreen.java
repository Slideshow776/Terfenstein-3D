package no.sandramoen.commanderqueen.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import no.sandramoen.commanderqueen.actors.ShockwaveBackground;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor;
import no.sandramoen.commanderqueen.screens.gameplay.LevelScreen;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen;

public class SplashScreen extends BaseScreen {
    private ShockwaveBackground shockwaveBackground;
    private BaseActor blackOverlay;

    public void initialize() {
        shockwaveBackground = new ShockwaveBackground("images/excluded/splash.jpg", uiStage);
        blackOverlayAnimation();
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void dispose() {
        super.dispose();
        shockwaveBackground.shaderProgram.dispose();
        shockwaveBackground.remove();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.BACK || keycode == Keys.ESCAPE || keycode == Keys.BACKSPACE || keycode == Keys.Q) {
            BaseGame.pistolShotSound.play(BaseGame.soundVolume);
            blackOverlay.clearActions();
            blackOverlay.addAction(Actions.sequence(
                    Actions.fadeIn(.2f),
                    Actions.delay(.05f),
                    Actions.run(() -> {
                        super.dispose();
                        Gdx.app.exit();
                    })));
        }
        return super.keyDown(keycode);
    }

    private void blackOverlayAnimation() {
        blackOverlayInitialization();
        blackOverlayFadeInAndOut();
        disposeAndSetActiveScreen();
    }

    private void blackOverlayInitialization() {
        blackOverlay = new BaseActor(0f, 0f, uiStage);
        blackOverlay.loadImage("whitePixel");
        blackOverlay.setColor(Color.BLACK);
        blackOverlay.setTouchable(Touchable.childrenOnly);
        blackOverlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void blackOverlayFadeInAndOut() {
        float totalDurationInSeconds = 4;
        blackOverlay.addAction(
                Actions.sequence(
                        Actions.fadeOut(totalDurationInSeconds / 3),
                        /*Actions.run { googlePlayServicesSignIn() },*/
                        Actions.delay(totalDurationInSeconds / 3),
                        Actions.fadeIn(totalDurationInSeconds / 3)
                )
        );
    }

    /*private void googlePlayServicesSignIn() {
        if (
            Gdx.app.type == Application.ApplicationType.Android &&
            BaseGame.isGPS &&
            BaseGame.gps != null
        )
            BaseGame.gps!!.signIn();
    }*/

    private void disposeAndSetActiveScreen() {
        blackOverlay.addAction(Actions.after(Actions.run(() -> {
            dispose();
            BaseGame.setActiveScreen(new LevelScreen());
        })));
    }
}

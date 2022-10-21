package no.sandramoen.terfenstein3D.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.terfenstein3D.screens.gameplay.LevelScreen;
import no.sandramoen.terfenstein3D.ui.MadeByLabel;
import no.sandramoen.terfenstein3D.utils.BaseGame;
import no.sandramoen.terfenstein3D.utils.BaseScreen;
import no.sandramoen.terfenstein3D.utils.GameUtils;

public class MenuScreen extends BaseScreen {
    @Override
    public void initialize() {
        /*TypingLabel titleLabel = new TypingLabel("Terfenstein 3D", new Label.LabelStyle(BaseGame.mySkin.get("arcade64", BitmapFont.class), null));
        titleLabel.setColor(BaseGame.redColor);
        uiTable.add(titleLabel)
                .padBottom(Gdx.graphics.getHeight() * .09f)
                .row();*/

        Image featureGraphics = new Image(BaseGame.textureAtlas.findRegion("feature graphics"));
        featureGraphics.scaleBy(2.25f);
        featureGraphics.setOrigin(Align.center);
        featureGraphics.addAction(Actions.sequence(Actions.fadeOut(0), Actions.fadeIn(.5f)));
        uiTable.add(featureGraphics)
                .padBottom(Gdx.graphics.getHeight() * .09f)
                .row();

        addTextButtons();

        uiTable.add(new MadeByLabel())
                .fillX()
                .padTop(Gdx.graphics.getHeight() * .09f);

        /*uiTable.setDebug(true);*/

        if (Gdx.input.isCursorCatched())
            Gdx.input.setCursorCatched(false);

        BaseGame.levelFinishMusic.stop();
        BaseGame.menuMusic.setVolume(BaseGame.musicVolume);
        BaseGame.menuMusic.play();
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q && !uiTable.hasActions())
            uiTable.addAction(exitGameWithSoundAndDelay());
        else if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER || keycode == Keys.SPACE)
            startLevel1();
        return super.keyDown(keycode);
    }

    private void addTextButtons() {
        uiTable.defaults()
                .width(Gdx.graphics.getWidth() * .125f)
                .height(Gdx.graphics.getHeight() * .075f)
                .spaceTop(Gdx.graphics.getHeight() * .01f);

        if (BaseGame.levelScreen != null)
            uiTable.add(resumeButton()).row();
        uiTable.add(startButton()).row();
        uiTable.add(optionsButton()).row();
        uiTable.add(exitButton()).row();
        uiTable.defaults().reset();
    }

    private TextButton resumeButton() {
        TextButton button = new TextButton("Resume", BaseGame.mySkin);
        button.setColor(BaseGame.blueColor);
        button.addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event))
                        BaseGame.setActiveScreen(BaseGame.levelScreen);
                    return false;
                }
        );
        return button;
    }

    private TextButton startButton() {
        TextButton button = new TextButton("Start", BaseGame.mySkin);
        button.addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event))
                        startLevel1();
                    return false;
                }
        );
        return button;
    }

    private TextButton optionsButton() {
        TextButton button = new TextButton("Options", BaseGame.mySkin);
        button.addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event))
                        BaseGame.setActiveScreen(new OptionsScreen());
                    return false;
                }
        );
        return button;
    }

    private TextButton exitButton() {
        TextButton button = new TextButton("Exit", BaseGame.mySkin);
        button.addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event) && !button.hasActions())
                        button.addAction(exitGameWithSoundAndDelay());
                    return false;
                }
        );
        return button;
    }

    private void startLevel1() {
        BaseGame.setActiveScreen(new LevelScreen(20, BaseGame.level1Map, "level 1", 100, 0, 20, 0, 0, null));
    }

    private SequenceAction exitGameWithSoundAndDelay() {
        return Actions.sequence(
                Actions.run(() -> playRandomSound()),
                Actions.delay(1),
                Actions.run(() -> Gdx.app.exit())
        );
    }

    private void playRandomSound() {
        Array sounds = new Array();
        sounds.add(BaseGame.pistolShotSound);
        sounds.add(BaseGame.menigActiveSound);
        sounds.add(BaseGame.menigPainSound);
        sounds.add(BaseGame.menigDeathSound);
        sounds.add(BaseGame.menigMeleeSound);
        sounds.add(BaseGame.ammoPickupSound);
        sounds.add(BaseGame.armorPickupSound);
        sounds.add(BaseGame.healthPickupSound);
        sounds.add(BaseGame.explosionSound);
        sounds.add(BaseGame.outOfAmmoSound);
        sounds.add(BaseGame.invulnerableSound);
        sounds.add(BaseGame.vulnerableSound);
        sounds.add(BaseGame.metalSound);
        sounds.add(BaseGame.wetSplashSound);
        sounds.add(BaseGame.bootAttackSound);
        sounds.add(BaseGame.bootMissSound);
        sounds.add(BaseGame.hundMeleeSound);
        sounds.add(BaseGame.hundDieSound);
        sounds.add(BaseGame.hundActivateSound);
        sounds.add(BaseGame.shotgunSound);
        sounds.add(BaseGame.door0OpeningSound);
        sounds.add(BaseGame.door0ClosingSound);
        sounds.add(BaseGame.elevatorSound);
        sounds.add(BaseGame.click1Sound);
        sounds.add(BaseGame.hoverOverEnterSound);
        sounds.add(BaseGame.playerUgh);
        sounds.add(BaseGame.secretWallSound);
        sounds.add(BaseGame.holyBallSpawnSound);
        sounds.add(BaseGame.holyBallExplosionSound);
        sounds.add(BaseGame.caseDroppingSound);
        sounds.add(BaseGame.chaingunPowerDownSound);
        sounds.add(BaseGame.keySound);
        sounds.add(BaseGame.doorUnlockedSound);
        sounds.add(BaseGame.doorLockedSound);
        sounds.add(BaseGame.weaponPickupSound);
        sounds.add(BaseGame.rocketLaunchSound);
        sounds.add(BaseGame.chainSawAttackingMusic);
        sounds.add(BaseGame.chainSawAttackingMusic);
        sounds.add(BaseGame.prestDeathSound);
        sounds.add(BaseGame.prestActiveSound);
        sounds.add(BaseGame.prestPainSound);
        sounds.add(BaseGame.sersjantActiveSound);
        sounds.add(BaseGame.sersjantDeath1Sound);
        sounds.add(BaseGame.sersjantDeath2Sound);
        sounds.add(BaseGame.sersjantPainSound);
        sounds.add(BaseGame.fenrikActiveSound);
        sounds.add(BaseGame.fenrikPainSound);
        sounds.add(BaseGame.fenrikDeathSound);

        try {
            Sound sound = (Sound) sounds.get(MathUtils.random(0, sounds.size - 1));
            sound.play(BaseGame.soundVolume);
        } catch (Throwable throwable) {
            try {
                Music music = (Music) sounds.get(MathUtils.random(0, sounds.size - 1));
                music.setVolume(BaseGame.soundVolume);
                music.play();
            } catch (Throwable throwable1) {
            }
        }
    }
}

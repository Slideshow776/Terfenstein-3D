package no.sandramoen.commanderqueen.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.commanderqueen.actors.weapon.weapons.Weapon;
import no.sandramoen.commanderqueen.screens.gameplay.LevelScreen;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen;
import no.sandramoen.commanderqueen.utils.GameUtils;

public class LevelFinishScreen extends BaseScreen {
    private boolean isEnteringState;

    private Image mapImage = new Image(BaseGame.textureAtlas.findRegion("level finished/map0"));
    private Image explosionImage0 = new Image(BaseGame.textureAtlas.findRegion("level finished/explosion"));
    private Image explosionImage1 = new Image(BaseGame.textureAtlas.findRegion("level finished/explosion"));
    private Image explosionImage2 = new Image(BaseGame.textureAtlas.findRegion("level finished/explosion"));
    private Image explosionImage3 = new Image(BaseGame.textureAtlas.findRegion("level finished/explosion"));
    private Image explosionImage4 = new Image(BaseGame.textureAtlas.findRegion("level finished/explosion"));
    private Image youAreHereImage = new Image(BaseGame.textureAtlas.findRegion("level finished/you are here"));

    private TypingLabel nameLabel;
    private TypingLabel statusLabel;
    private TypingLabel killNameLabel;
    private Label killCountLabel;
    private TypingLabel itemsNameLabel;
    private Label itemsCountLabel;
    private TypingLabel secretsNameLabel;
    private Label secretsCountLabel;
    private Label timeLabel;
    private Label parLabel;

    private float count;
    private final float COUNT_FREQUENCY = .01f;

    private final float SOUND_COUNT_FREQUENCY = .04f;
    private float soundCount = SOUND_COUNT_FREQUENCY;
    private long pistolSoundID;

    private int kills;
    private int killCount = -1;
    private int items;
    private int itemCount = -1;
    private int secrets;
    private int secretCount = -1;
    private float time;
    private float timeCount = -1;
    private float par;
    private float parCount = -1;

    private int health;
    private int armor;
    private int bullets;
    private int shells;
    private Array<Weapon> weapons;
    private String numLevel;
    private String levelName;

    public LevelFinishScreen(Array args, String numLevel, int health, int armor, int bullets, int shells, Array<Weapon> weapons) {
        this.numLevel = numLevel;
        this.health = health;
        this.armor = armor;
        this.bullets = bullets;
        this.shells = shells;
        this.weapons = weapons;

        if (args.size != 7)
            Gdx.app.error(getClass().getSimpleName(), "Error: Missing level data, size is " + args.size);
        levelName = (String) args.get(6);

        initializeLabels(args);
        initializeImages();
        GameUtils.playLoopingMusic(BaseGame.levelFinishMusic);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void update(float dt) {
        if (!isEnteringState && count > COUNT_FREQUENCY) {
            count = 0;
            countUpAllLabelsInOrder(dt);
        } else {
            count += dt;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        inputReaction();
        return super.keyDown(keycode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        inputReaction();
        return super.touchDown(screenX, screenY, pointer, button);
    }

    private void countUpAllLabelsInOrder(float dt) {
        if (killCount < kills)
            killCount = countUp(killCount, kills, killCountLabel, dt);
        else if (itemCount < items)
            itemCount = countUp(itemCount, items, itemsCountLabel, dt);
        else if (secretCount < secrets)
            secretCount = countUp(secretCount, secrets, secretsCountLabel, dt);
        else if (timeCount < time)
            timeCount = countUp(timeCount, time, "Time", timeLabel, dt);
        else if (parCount < par)
            parCount = countUp(parCount, par, "Par", parLabel, dt);
    }

    private int countUp(int counter, int goal, Label label, float dt) {
        label.setText(counter + 1 + "%");
        playCountingUpSound(counter + 1 >= goal, dt);
        return counter + 1;
    }

    private float countUp(float counter, float goal, String text, Label label, float dt) {
        label.setText(text + " " + formatTime(counter + 1));
        playCountingUpSound(counter + 1 >= goal, dt);
        return counter + 1;
    }

    private void playCountingUpSound(boolean finished, float dt) {
        if (finished) {
            BaseGame.pistolShotSound.stop(pistolSoundID);
            BaseGame.pistolShotSound.play(BaseGame.soundVolume * .5f, .5f, 0);
            count = -.5f;
        } else if (soundCount >= SOUND_COUNT_FREQUENCY) {
            soundCount = 0;
            pistolSoundID = BaseGame.pistolShotSound.play(BaseGame.soundVolume * .25f, 1.5f, 0);
        } else {
            soundCount += dt;
        }
    }

    private void inputReaction() {
        if (killCount < kills || itemCount < items || secretCount < secrets || timeCount < time || parCount < par) {
            setAllArgsInstantlyVisible();
        } else if (!isEnteringState) {
            if (numLevel.equalsIgnoreCase("level 5")) {
                BaseGame.levelScreen = null;
                BaseGame.setActiveScreen(new MenuScreen());
            }
            showEnteringState();
        } else {
            setNewScreen();
        }
    }

    private TypingLabel initializeTypingLabel(String string) {
        TypingLabel label = new TypingLabel(string, new Label.LabelStyle(BaseGame.mySkin.get("arcade64", BitmapFont.class), null));
        label.setColor(BaseGame.redColor);
        label.font.scaleTo(Gdx.graphics.getWidth() * .027f, Gdx.graphics.getHeight() * .027f);
        return label;
    }

    private Label initializeLabel(String string) {
        Label label = new Label(string, new Label.LabelStyle(BaseGame.mySkin.get("arcade64", BitmapFont.class), null));
        label.setColor(BaseGame.redColor);
        label.setFontScale(Gdx.graphics.getWidth() * .0004f, Gdx.graphics.getHeight() * .0004f);
        return label;
    }

    private void setAllArgsInstantlyVisible() {
        killCount = kills;
        itemCount = items;
        secretCount = secrets;
        timeCount = time;
        parCount = par;

        killCountLabel.setText(kills + "%");
        itemsCountLabel.setText(items + "%");
        secretsCountLabel.setText(secrets + "%");
        timeLabel.setText("Time " + formatTime(time));
        parLabel.setText("Par " + formatTime(par));

        BaseGame.pistolShotSound.stop(pistolSoundID);
        pistolSoundID = BaseGame.pistolShotSound.play(BaseGame.soundVolume, .5f, 0);
    }

    private void showEnteringState() {
        isEnteringState = true;
        nameLabel.setText("Entering");
        nameLabel.setColor(BaseGame.whiteColor);
        statusLabel.setText(levelName);
        statusLabel.setColor(BaseGame.redColor);

        killNameLabel.setText("");
        killCountLabel.setText("");
        itemsNameLabel.setText("");
        itemsCountLabel.setText("");
        secretsNameLabel.setText("");
        secretsCountLabel.setText("");
        timeLabel.setText("");
        parLabel.setText("");

        youAreHereImage.setVisible(true);
        youAreHereImage.addAction(Actions.forever(Actions.sequence(
                Actions.alpha(.8f, .5f),
                Actions.alpha(1, .5f)

        )));
        explosionImage0.setVisible(true);
        pistolSoundID = BaseGame.pistolShotSound.play(BaseGame.soundVolume, .5f, 0);
    }

    private void setNewScreen() {
        BaseGame.pistolShotSound.play(BaseGame.soundVolume, .5f, 0);

        if (numLevel.equalsIgnoreCase("test"))
            BaseGame.setActiveScreen(new LevelScreen(65, BaseGame.testMap, "test", health, armor, bullets, shells, weapons));
        else if (numLevel.equalsIgnoreCase("level 1"))
            BaseGame.setActiveScreen(new LevelScreen(95, BaseGame.level2Map, "level 2", health, armor, bullets, shells, weapons));
        else if (numLevel.equalsIgnoreCase("level 2"))
            BaseGame.setActiveScreen(new LevelScreen(95, BaseGame.level3Map, "level 3", health, armor, bullets, shells, weapons));
        else if (numLevel.equalsIgnoreCase("level 3"))
            BaseGame.setActiveScreen(new LevelScreen(95, BaseGame.level4Map, "level 4", health, armor, bullets, shells, weapons));
        else if (numLevel.equalsIgnoreCase("level 4"))
            BaseGame.setActiveScreen(new LevelScreen(95, BaseGame.level5Map, "level 5", health, armor, bullets, shells, weapons));
    }

    private String formatTime(float time) {
        int hours = (int) (time / 3600);
        int remainder = (int) time - hours * 3600;
        int minutes = remainder / 60;
        remainder = remainder - minutes * 60;
        int seconds = remainder;

        String temp0 = String.valueOf(hours);
        String temp1 = String.valueOf(minutes);
        String temp2 = String.valueOf(seconds);

        if (hours <= 9)
            temp0 = "0" + hours;
        else if (hours > 99 || hours < 0)
            temp0 = "99";

        if (minutes <= 9 && minutes >= 0)
            temp1 = "0" + minutes;
        else if (minutes > 99 || minutes < 0)
            temp1 = "99";

        if (seconds <= 9 && seconds >= 0)
            temp2 = "0" + seconds;
        else if (seconds > 99 || seconds < 0)
            temp2 = "99";

        return temp0 + ":" + temp1 + ":" + temp2;
    }

    private void initializeLabels(Array args) {
        nameLabel = initializeTypingLabel(args.get(0).toString());
        nameLabel.setColor(BaseGame.whiteColor);
        statusLabel = initializeTypingLabel("Finished");

        killNameLabel = initializeTypingLabel("Kills");
        kills = (int) args.get(1);
        killCountLabel = initializeLabel("%");

        itemsNameLabel = initializeTypingLabel("Items");
        items = (int) args.get(2);
        itemsCountLabel = initializeLabel("");

        secretsNameLabel = initializeTypingLabel("Secrets");
        secrets = (int) args.get(3);
        secretsCountLabel = initializeLabel("");

        timeLabel = initializeLabel("Time");
        time = (float) args.get(4);

        parLabel = initializeLabel("Par         ");
        par = (float) args.get(5);

        uiTable.add(nameLabel).colspan(2).center().row();
        uiTable.add(statusLabel).colspan(2).center().spaceTop(Gdx.graphics.getHeight() * .025f).padBottom(Gdx.graphics.getHeight() * .05f).row();
        uiTable.defaults().spaceTop(Gdx.graphics.getHeight() * .1f).expandX();
        uiTable.add(killNameLabel).left();
        uiTable.add(killCountLabel).right().row();
        uiTable.add(itemsNameLabel).left();
        uiTable.add(itemsCountLabel).right().row();
        uiTable.add(secretsNameLabel).left();
        uiTable.add(secretsCountLabel).right().row();
        uiTable.add(timeLabel).left().spaceTop(Gdx.graphics.getHeight() * .15f);
        uiTable.add(parLabel).right().spaceTop(Gdx.graphics.getHeight() * .15f);

        uiTable.padRight(Gdx.graphics.getWidth() * .1f).padLeft(Gdx.graphics.getWidth() * .1f);
        uiTable.padTop(Gdx.graphics.getHeight() * .01f).padBottom(Gdx.graphics.getHeight() * .01f);
        /*uiTable.setDebug(true);*/
    }

    private void initializeImages() {
        mapImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        mainStage.addActor(mapImage);

        youAreHereImage.setVisible(false);
        youAreHereImage.setSize(Gdx.graphics.getWidth() * .1f, Gdx.graphics.getHeight() * .05f);
        youAreHereImage.setPosition(Gdx.graphics.getWidth() * .51f, Gdx.graphics.getHeight() * .35f);
        mainStage.addActor(youAreHereImage);

        imageSetup(explosionImage0);
        imageSetup(explosionImage1);
        imageSetup(explosionImage2);
        imageSetup(explosionImage3);
        imageSetup(explosionImage4);
    }

    private void imageSetup(Image image) {
        image.setVisible(false);
        image.setSize(Gdx.graphics.getWidth() * .05f, Gdx.graphics.getHeight() * .05f);
        image.setPosition(Gdx.graphics.getWidth() * .48f, Gdx.graphics.getHeight() * .54f);
        mainStage.addActor(image);
    }
}

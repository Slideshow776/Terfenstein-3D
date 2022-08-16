package no.sandramoen.commanderqueen.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen;

public class LevelFinishScreen extends BaseScreen {

    private Image mapimage = new Image(BaseGame.textureAtlas.findRegion("map0"));

    private TypingLabel nameLabel;
    private TypingLabel statusLabel;
    private TypingLabel killNameLabel;
    private TypingLabel killCountLabel;
    private TypingLabel itemsNameLabel;
    private TypingLabel itemsCountLabel;
    private TypingLabel secretsNameLabel;
    private TypingLabel secretsCountLabel;
    private TypingLabel timeLabel;
    private TypingLabel parLabel;

    public LevelFinishScreen(Array args) {
        if (args.size != 6)
            Gdx.app.error(getClass().getSimpleName(), "Error: Missing level data, size is " + args.size);

        nameLabel = initializeLabel(args.get(0).toString());
        nameLabel.setColor(BaseGame.whiteColor);
        statusLabel = initializeLabel("Finished");
        killNameLabel = initializeLabel("Kills");
        killCountLabel = initializeLabel(args.get(1).toString() + "%");
        itemsNameLabel = initializeLabel("Items");
        itemsCountLabel = initializeLabel(args.get(2).toString() + "%");
        secretsNameLabel = initializeLabel("Secrets");
        secretsCountLabel = initializeLabel(args.get(3).toString() + "%");
        timeLabel = initializeLabel("Time " + formatTime((float) args.get(4)));
        parLabel = initializeLabel("Par " + formatTime((float) args.get(5)));

        Table table = new Table();
        table.add(nameLabel).colspan(2).center().row();
        table.add(statusLabel).colspan(2).center().spaceTop(Gdx.graphics.getHeight() * .025f).row();
        table.defaults().spaceTop(Gdx.graphics.getHeight() * .1f).expandX();
        table.add(killNameLabel).left();
        table.add(killCountLabel).right().row();
        table.add(itemsNameLabel).left();
        table.add(itemsCountLabel).right().row();
        table.add(secretsNameLabel).left();
        table.add(secretsCountLabel).right().row();
        table.add(timeLabel).left();
        table.add(parLabel).right();

        /*table.setDebug(true);*/
        table.padRight(Gdx.graphics.getWidth() * .1f).padLeft(Gdx.graphics.getWidth() * .1f);
        table.padTop(Gdx.graphics.getHeight() * .01f).padBottom(Gdx.graphics.getHeight() * .01f);

        mapimage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Stack stack = new Stack();
        stack.add(mapimage);
        stack.add(table);

        uiTable.add(stack).width(Gdx.graphics.getWidth()).height(Gdx.graphics.getHeight());
        /*uiTable.setDebug(true);*/
    }


    @Override
    public void initialize() {
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.Q)
            BaseGame.setActiveScreen(new MenuScreen());
        return super.keyDown(keycode);
    }

    private TypingLabel initializeLabel(String string) {
        TypingLabel label = new TypingLabel(string, new Label.LabelStyle(BaseGame.mySkin.get("arcade64", BitmapFont.class), null));
        label.setColor(BaseGame.redColor);
        label.font.scaleTo(Gdx.graphics.getWidth() * .03f, Gdx.graphics.getHeight() * .03f);
        return label;
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
        if (minutes <= 9)
            temp1 = "0" + minutes;
        if (seconds <= 9)
            temp2 = "0" + seconds;

        return temp0 + ":" + temp1 + ":" + temp2;
    }
}

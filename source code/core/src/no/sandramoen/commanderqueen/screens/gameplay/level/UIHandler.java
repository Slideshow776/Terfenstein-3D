package no.sandramoen.commanderqueen.screens.gameplay.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TextraLabel;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.characters.enemy.Enemy;
import no.sandramoen.commanderqueen.utils.BaseGame;

public class UIHandler {
    public Label debugLabel;
    public TypingLabel pickupLabel;
    public Label gameLabel;
    public Label statusLabel;

    public boolean isReset;

    private Table uiTable;
    private Array<Enemy> enemies;
    private HUD hud;

    public UIHandler(Table uiTable, Array<Enemy> enemies, HUD hud) {
        this.uiTable = uiTable;
        this.enemies = enemies;
        this.hud = hud;
        constructTable(uiTable, enemies, hud);
    }

    public void reset() {
        uiTable.reset();
        constructTable(uiTable, enemies, hud);
    }

    private void constructTable(Table uiTable, Array<Enemy> enemies, HUD hud) {
        uiTable.add(hud.weaponsTable)
                .colspan(2)
                .size(hud.getWidth(), hud.getHeight())
                .row();

        statusLabel = new Label("enemies left: " + enemies.size, new Label.LabelStyle(BaseGame.mySkin.get("arcade26", BitmapFont.class), null));
        statusLabel.setColor(Color.DARK_GRAY);
        uiTable.add(statusLabel)
                .expandX()
                .left()
                .padLeft(Gdx.graphics.getWidth() * .01f)
                .row();

        debugLabel = new Label(" ", new Label.LabelStyle(BaseGame.mySkin.get("arcade26", BitmapFont.class), null));
        debugLabel.setColor(Color.DARK_GRAY);
        uiTable.add(debugLabel)
                .expandX()
                .top()
                .left()
                .padTop(Gdx.graphics.getHeight() * .01f)
                .padLeft(Gdx.graphics.getWidth() * .01f)
                .row();

        pickupLabel = new TypingLabel("", new Label.LabelStyle(BaseGame.mySkin.get("arcade26", BitmapFont.class), null));
        uiTable.add(pickupLabel)
                .expandX()
                .top()
                .left()
                .padTop(Gdx.graphics.getHeight() * .01f)
                .padLeft(Gdx.graphics.getWidth() * .01f)
                .row();

        gameLabel = new Label("", new Label.LabelStyle(BaseGame.mySkin.get("arcade26", BitmapFont.class), null));
        gameLabel.setColor(Color.RED);
        gameLabel.setFontScale(2f);
        uiTable.add(gameLabel)
                .expand()
                .center()
                .colspan(2)
                .row();

        uiTable.add(hud.getLabelTable())
                .colspan(2)
                .size(hud.getWidth(), hud.getHeight());

        /*uiTable.setDebug(true);*/
    }

    public void setPickupLabel(String message) {
        pickupLabel.setText("{FADE}{FASTER}" + message);
        pickupLabel.restart();
        pickupLabel.clearActions();
        pickupLabel.addAction(Actions.fadeIn(0));
        pickupLabel.addAction(Actions.sequence(
                Actions.delay(4f),
                Actions.fadeOut(.5f)
        ));
    }
}

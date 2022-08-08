package no.sandramoen.commanderqueen.screens.gameplay.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.characters.enemy.Enemy;
import no.sandramoen.commanderqueen.utils.BaseGame;

public class UIHandler {
    public Label debugLabel;
    public Label gameLabel;
    public Label statusLabel;

    public UIHandler(Table uiTable, Array<Enemy> enemies, HUD hud) {
        uiTable.add(hud.weaponsTable()).colspan(2).size(hud.getWidth(), hud.getHeight())
                .row();

        statusLabel = new Label("enemies left: " + enemies.size, BaseGame.label26Style);
        uiTable.add(statusLabel)
                .expandX()
                .left()
                .padLeft(Gdx.graphics.getWidth() * .01f)
                .row();

        debugLabel = new Label(" ", BaseGame.label26Style);
        uiTable.add(debugLabel)
                .expandX()
                .top()
                .left()
                .padTop(Gdx.graphics.getHeight() * .01f)
                .padLeft(Gdx.graphics.getWidth() * .01f)
                .row();

        gameLabel = new Label("", BaseGame.label26Style);
        gameLabel.setColor(Color.RED);
        gameLabel.setFontScale(2f);
        uiTable.add(gameLabel)
                .expand()
                .center()
                .colspan(2)
                .row();

        uiTable.add(hud.getLabelTable()).colspan(2).size(hud.getWidth(), hud.getHeight());

        /*uiTable.setDebug(true);*/
    }
}

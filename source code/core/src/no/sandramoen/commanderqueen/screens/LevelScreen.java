package no.sandramoen.commanderqueen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;

import no.sandramoen.commanderqueen.actors.Box;
import no.sandramoen.commanderqueen.actors.Player;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen;

public class LevelScreen extends BaseScreen {
    private Player player;


    public void initialize() {
        Box markerO = new Box(0f, 0f, 0f, mainStage3D);
        markerO.setColor(Color.BROWN);
        markerO.loadImage("crate");
        markerO.setScale(2, 2, -2);

        player = new Player(0f, 0f, 0f, mainStage3D);
    }

    public void update(float dt) {
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q)
            Gdx.app.exit();
        return super.keyDown(keycode);
    }
}

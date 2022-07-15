package no.sandramoen.commanderqueen;

import no.sandramoen.commanderqueen.screens.LevelScreen3D;
import no.sandramoen.commanderqueen.utils.BaseGame;

public class MyGdxGame extends BaseGame {

	@Override
	public void create() {
		super.create();
		setActiveScreen(new LevelScreen3D());
	}
}

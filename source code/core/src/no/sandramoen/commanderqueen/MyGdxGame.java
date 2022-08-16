package no.sandramoen.commanderqueen;

import no.sandramoen.commanderqueen.screens.gameplay.LevelScreen;
import no.sandramoen.commanderqueen.screens.shell.MenuScreen;
import no.sandramoen.commanderqueen.screens.shell.OptionsScreen;
import no.sandramoen.commanderqueen.screens.shell.SplashScreen;
import no.sandramoen.commanderqueen.utils.BaseGame;

public class MyGdxGame extends BaseGame {

	@Override
	public void create() {
		super.create();
		// setActiveScreen(new SplashScreen());
		// setActiveScreen(new MenuScreen());
		setActiveScreen(new OptionsScreen());
		// setActiveScreen(new LevelScreen());
	}
}

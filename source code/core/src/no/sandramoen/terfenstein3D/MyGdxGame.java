package no.sandramoen.terfenstein3D;

import no.sandramoen.terfenstein3D.screens.gameplay.LevelScreen;
import no.sandramoen.terfenstein3D.screens.shell.MenuScreen;
import no.sandramoen.terfenstein3D.screens.shell.SplashScreen;
import no.sandramoen.terfenstein3D.utils.BaseGame;

public class MyGdxGame extends BaseGame {

	@Override
	public void create() {
		super.create();
		setActiveScreen(new SplashScreen());
		setActiveScreen(new MenuScreen());
		// setActiveScreen(new OptionsScreen());
		setActiveScreen(new LevelScreen(30, BaseGame.level3Map, "level 3", 100, 0, 50, 25, 0, null));
	}
}
 
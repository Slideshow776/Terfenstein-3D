package no.sandramoen.terfenstein3D;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import java.awt.Dimension;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Terfenstein 3D");
		setWindowedMode(.8f, config);
		new Lwjgl3Application(new MyGdxGame(), config);
	}

	private static void setWindowedMode(float percentOfScreenSize, Lwjgl3ApplicationConfiguration config) {
		Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) (dimension.width * percentOfScreenSize);

		float aspectRatio = 16 / 9f;
		int height = (int) (width / aspectRatio);

		System.out.println("Window dimensions => width: " + width + ", height: " + height);
		config.setWindowedMode(width, height);
	}
}

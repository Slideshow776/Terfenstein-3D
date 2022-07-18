package no.sandramoen.commanderqueen.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public abstract class BaseGame extends Game implements AssetErrorListener {

    private static BaseGame game;
    public static AssetManager assetManager;

    // game assets
    public static TextureAtlas textureAtlas;
    public static Label.LabelStyle label26Style;
    public static Music levelMusic0;
    public static Sound pistolShotSound;
    public static Sound ghoulDeathSound;

    // game state
    public static float mouseMovementSensitivity = .05f;
    public static float voiceVolume = 1f;
    public static float soundVolume = .7f;
    public static float musicVolume = .7f;

    public BaseGame() {
        game = this;
    }

    public void create() {
        Gdx.input.setInputProcessor(new InputMultiplexer());
        UI();
        assetManager();
    }

    public static void setActiveScreen(BaseScreen3D screen) {
        game.setScreen(screen);
    }

    public void dispose() {
        super.dispose();
        try {
            assetManager.dispose();
        } catch (Error error) {
            Gdx.app.error(this.getClass().getSimpleName(), error.toString());
        }
    }

    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(this.getClass().getSimpleName(), "Could not load asset: " + asset.fileName, throwable);
    }

    private void UI() {
        label26Style = new Label.LabelStyle();
        BitmapFont myFont2 = new BitmapFont(Gdx.files.internal("fonts/arcade26.fnt"));
        label26Style.font = myFont2;
    }

    private void assetManager() {
        long startTime = System.currentTimeMillis();
        assetManager = new AssetManager();
        assetManager.setErrorListener(this);
        assetManager.load("images/included/packed/images.pack.atlas", TextureAtlas.class);

        // music
        assetManager.load("audio/music/342991__furbyguy__stuttering-guitar-metal.wav", Music.class);

        // sound
        assetManager.load("audio/sound/370220__eflexmusic__pistol-shot-close-mixed.wav", Sound.class);
        assetManager.load("audio/sound/249686__cylon8472__cthulhu-growl.wav", Sound.class);

        // tiled maps
        // assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        // assetManager.load("maps/level1.tmx", TiledMap.class);

        assetManager.finishLoading();

        // music
        levelMusic0 = assetManager.get("audio/music/342991__furbyguy__stuttering-guitar-metal.wav", Music.class);

        // sound
        pistolShotSound = assetManager.get("audio/sound/370220__eflexmusic__pistol-shot-close-mixed.wav", Sound.class);
        ghoulDeathSound = assetManager.get("audio/sound/249686__cylon8472__cthulhu-growl.wav", Sound.class);

        // tiled maps
        // level1Map = assetManager.get("maps/level1.tmx", TiledMap.class);

        textureAtlas = assetManager.get("images/included/packed/images.pack.atlas");
        printLoadingTime(startTime);
    }

    private void printLoadingTime(long startTime) {
        long endTime = System.currentTimeMillis();
        Gdx.app.error(this.getClass().getSimpleName(), "Asset manager took " + (endTime - startTime) + " ms to load all game assets.");
    }
}

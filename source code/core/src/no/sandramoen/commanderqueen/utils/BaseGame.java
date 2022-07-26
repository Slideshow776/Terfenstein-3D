package no.sandramoen.commanderqueen.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public abstract class BaseGame extends Game implements AssetErrorListener {

    private static BaseGame game;
    public static AssetManager assetManager;

    // game assets
    public static TextureAtlas textureAtlas;
    public static Label.LabelStyle label26Style;
    public static TiledMap testMap;
    public static TiledMap level0Map;
    public static Music level0Music;
    public static Music metalWalkingMusic;
    public static Sound pistolShotSound;
    public static Sound ghoulDeathSound;
    public static Sound ammoPickupSound;
    public static Sound armorPickupSound;
    public static Sound healthPickupSound;
    public static Sound explosionSound;

    // game state
    public static float mouseMovementSensitivity = .05f;
    public static boolean isHeadBobbing = true;
    public static float aspectRatio = 16 / 9;
    public static float voiceVolume = 1f;
    public static float soundVolume = .5f;
    public static float musicVolume = .1f;
    public static float unitScale = .0621f;
    public static Color redColor = new Color(0.647f, 0.188f, 0.188f, 1f);
    public static Color greenColor = new Color(0.459f, 0.655f, 0.263f, 1f);
    public static Color yellowColor = new Color(0.91f, 0.757f, 0.439f, 1f);

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
        assetManager.load("audio/music/398937__mypantsfelldown__metal-footsteps.wav", Music.class);

        // sound
        assetManager.load("audio/sound/370220__eflexmusic__pistol-shot-close-mixed.wav", Sound.class);
        assetManager.load("audio/sound/249686__cylon8472__cthulhu-growl.wav", Sound.class);
        assetManager.load("audio/sound/Pickup_Coin45.wav", Sound.class);
        assetManager.load("audio/sound/armor pickup.wav", Sound.class);
        assetManager.load("audio/sound/health pickup.wav", Sound.class);
        assetManager.load("audio/sound/Explosion10.wav", Sound.class);

        // tiled maps
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("maps/test.tmx", TiledMap.class);
        assetManager.load("maps/level0.tmx", TiledMap.class);

        assetManager.finishLoading();

        // music
        level0Music = assetManager.get("audio/music/342991__furbyguy__stuttering-guitar-metal.wav", Music.class);
        metalWalkingMusic = assetManager.get("audio/music/398937__mypantsfelldown__metal-footsteps.wav", Music.class);

        // sound
        pistolShotSound = assetManager.get("audio/sound/370220__eflexmusic__pistol-shot-close-mixed.wav", Sound.class);
        ghoulDeathSound = assetManager.get("audio/sound/249686__cylon8472__cthulhu-growl.wav", Sound.class);
        ammoPickupSound = assetManager.get("audio/sound/Pickup_Coin45.wav", Sound.class);
        armorPickupSound = assetManager.get("audio/sound/armor pickup.wav", Sound.class);
        healthPickupSound = assetManager.get("audio/sound/health pickup.wav", Sound.class);
        explosionSound = assetManager.get("audio/sound/Explosion10.wav", Sound.class);

        // tiled maps
        testMap = assetManager.get("maps/test.tmx", TiledMap.class);
        level0Map = assetManager.get("maps/level0.tmx", TiledMap.class);

        textureAtlas = assetManager.get("images/included/packed/images.pack.atlas");
        printLoadingTime(startTime);
    }

    private void printLoadingTime(long startTime) {
        long endTime = System.currentTimeMillis();
        Gdx.app.error(this.getClass().getSimpleName(), "Asset manager took " + (endTime - startTime) + " ms to load all game assets.");
    }
}

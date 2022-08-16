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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class BaseGame extends Game implements AssetErrorListener {

    private static BaseGame game;
    public static AssetManager assetManager;

    // game assets
    public static TextureAtlas textureAtlas;/*
    public static Label.LabelStyle label26Style;*/
    public static Skin mySkin;

    public static String defaultShader;
    public static String shockwaveShader;

    public static TiledMap testMap;
    public static TiledMap level0Map;

    public static Music level0Music;
    public static Music metalWalkingMusic;

    public static Sound pistolShotSound;
    public static Sound menigActiveSound;
    public static Sound menigHurtSound;
    public static Sound menigDeathSound;
    public static Sound menigMeleeSound;
    public static Sound ammoPickupSound;
    public static Sound armorPickupSound;
    public static Sound healthPickupSound;
    public static Sound explosionSound;
    public static Sound outOfAmmoSound;
    public static Sound invulnerableSound;
    public static Sound vulnerableSound;
    public static Sound metalSound;
    public static Sound wetSplashSound;
    public static Sound bootAttackSound;
    public static Sound bootMissSound;
    public static Sound hundMeleeSound;
    public static Sound hundDieSound;
    public static Sound hundActivateSound;
    public static Sound shotgunSound;
    public static Sound door0OpeningSound;
    public static Sound door0ClosingSound;
    public static Sound elevatorSound;
    public static Sound click1Sound;
    public static Sound hoverOverEnterSound;

    // game state
    public static boolean isCustomShadersEnabled = true;
    public static float mouseMovementSensitivity = .05f;
    public static boolean isHeadBobbing = true;
    public static float aspectRatio = 16 / 9f;
    public static float voiceVolume = 1f;
    public static float soundVolume = .5f;
    public static float musicVolume = .1f;
    public static float unitScale = .0621f;
    public static Color redColor = new Color(0.647f, 0.188f, 0.188f, 1f);
    public static Color greenColor = new Color(0.459f, 0.655f, 0.263f, 1f);
    public static Color yellowColor = new Color(0.91f, 0.757f, 0.439f, 1f);
    public static Color darkColor = new Color(.4f, .4f, .4f, 1f);
    public static Color whiteColor = new Color(0.922f, 0.929f, 0.914f, 1f);

    public BaseGame() {
        game = this;
    }

    public void create() {
        Gdx.input.setInputProcessor(new InputMultiplexer());
        UI();
        assetManager();
    }

    public static void setActiveScreen(BaseScreen screen) {
        game.setScreen(screen);
    }

    public static void setActiveScreen(BaseScreen3D screen) {
        game.setScreen(screen);
    }

    @Override
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
        mySkin = new Skin(Gdx.files.internal("skins/mySkin/mySkin.json"));
        mySkin.getFont("arcade26").getData().setScale(Gdx.graphics.getWidth() * .0005f);
        mySkin.getFont("arcade64").getData().setScale(Gdx.graphics.getWidth() * .0005f);
    }

    private void assetManager() {
        long startTime = System.currentTimeMillis();
        assetManager = new AssetManager();
        assetManager.setErrorListener(this);
        assetManager.setLoader(Text.class, new TextLoader(new InternalFileHandleResolver()));
        assetManager.load("images/included/packed/images.pack.atlas", TextureAtlas.class);

        // shaders
        assetManager.load(new AssetDescriptor("shaders/default.vs", Text.class, new TextLoader.TextParameter()));
        assetManager.load(new AssetDescriptor("shaders/shockwave.fs", Text.class, new TextLoader.TextParameter()));

        // music
        assetManager.load("audio/music/342991__furbyguy__stuttering-guitar-metal.wav", Music.class);
        assetManager.load("audio/music/398937__mypantsfelldown__metal-footsteps.wav", Music.class);

        // sound
        assetManager.load("audio/sound/370220__eflexmusic__pistol-shot-close-mixed.wav", Sound.class);
        assetManager.load("audio/sound/menig_ai.wav", Sound.class);
        assetManager.load("audio/sound/menig_active.wav", Sound.class);
        assetManager.load("audio/sound/menig_ugh.wav", Sound.class);
        assetManager.load("audio/sound/Pickup_Coin45.wav", Sound.class);
        assetManager.load("audio/sound/armor pickup.wav", Sound.class);
        assetManager.load("audio/sound/health pickup.wav", Sound.class);
        assetManager.load("audio/sound/Explosion10.wav", Sound.class);
        assetManager.load("audio/sound/566384__combatsfx4you__dry-fire-out-of-ammo.wav", Sound.class);
        assetManager.load("audio/sound/invulnerable.wav", Sound.class);
        assetManager.load("audio/sound/vulnerable.wav", Sound.class);
        assetManager.load("audio/sound/35213__abyssmal__slashkut.wav", Sound.class);
        assetManager.load("audio/sound/488608__spacejoe__metal-bowl-7.wav", Sound.class);
        assetManager.load("audio/sound/521958__kastenfrosch__wet-impact-2.ogg", Sound.class);
        assetManager.load("audio/sound/493913__damnsatinist__heavy-punch.wav", Sound.class);
        assetManager.load("audio/sound/632763__adh-dreaming__transition-swoosh.wav", Sound.class);
        assetManager.load("audio/sound/418107__crazymonke9__single-dog-bark-1.wav", Sound.class);
        assetManager.load("audio/sound/72724__moffet__impatient-whimpers-and-barks.wav", Sound.class);
        assetManager.load("audio/sound/hund_activate.wav", Sound.class);
        assetManager.load("audio/sound/145209__lensflare8642__shotgun-sounds.wav", Sound.class);
        assetManager.load("audio/sound/door0 opening.wav", Sound.class);
        assetManager.load("audio/sound/door0 closing.wav", Sound.class);
        assetManager.load("audio/sound/502341__universodemalaonda__elevator-03.wav", Sound.class);
        assetManager.load("audio/sound/click1.wav", Sound.class);
        assetManager.load("audio/sound/hoverOverEnter.wav", Sound.class);

        // tiled maps
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("maps/test.tmx", TiledMap.class);
        assetManager.load("maps/level0.tmx", TiledMap.class);

        assetManager.finishLoading();


        // shaders
        defaultShader = assetManager.get("shaders/default.vs", Text.class).getString();
        shockwaveShader = assetManager.get("shaders/shockwave.fs", Text.class).getString();

        // music
        level0Music = assetManager.get("audio/music/342991__furbyguy__stuttering-guitar-metal.wav", Music.class);
        metalWalkingMusic = assetManager.get("audio/music/398937__mypantsfelldown__metal-footsteps.wav", Music.class);

        // sound
        pistolShotSound = assetManager.get("audio/sound/370220__eflexmusic__pistol-shot-close-mixed.wav", Sound.class);
        menigHurtSound = assetManager.get("audio/sound/menig_ai.wav", Sound.class);
        menigActiveSound = assetManager.get("audio/sound/menig_active.wav", Sound.class);
        menigDeathSound = assetManager.get("audio/sound/menig_ugh.wav", Sound.class);
        ammoPickupSound = assetManager.get("audio/sound/Pickup_Coin45.wav", Sound.class);
        armorPickupSound = assetManager.get("audio/sound/armor pickup.wav", Sound.class);
        healthPickupSound = assetManager.get("audio/sound/health pickup.wav", Sound.class);
        explosionSound = assetManager.get("audio/sound/Explosion10.wav", Sound.class);
        outOfAmmoSound = assetManager.get("audio/sound/566384__combatsfx4you__dry-fire-out-of-ammo.wav", Sound.class);
        invulnerableSound = assetManager.get("audio/sound/invulnerable.wav", Sound.class);
        vulnerableSound = assetManager.get("audio/sound/vulnerable.wav", Sound.class);
        menigMeleeSound = assetManager.get("audio/sound/35213__abyssmal__slashkut.wav", Sound.class);
        metalSound = assetManager.get("audio/sound/488608__spacejoe__metal-bowl-7.wav", Sound.class);
        wetSplashSound = assetManager.get("audio/sound/521958__kastenfrosch__wet-impact-2.ogg", Sound.class);
        bootAttackSound = assetManager.get("audio/sound/493913__damnsatinist__heavy-punch.wav", Sound.class);
        bootMissSound = assetManager.get("audio/sound/632763__adh-dreaming__transition-swoosh.wav", Sound.class);
        hundMeleeSound = assetManager.get("audio/sound/418107__crazymonke9__single-dog-bark-1.wav", Sound.class);
        hundDieSound = assetManager.get("audio/sound/72724__moffet__impatient-whimpers-and-barks.wav", Sound.class);
        hundActivateSound = assetManager.get("audio/sound/hund_activate.wav", Sound.class);
        shotgunSound = assetManager.get("audio/sound/145209__lensflare8642__shotgun-sounds.wav", Sound.class);
        door0OpeningSound = assetManager.get("audio/sound/door0 opening.wav", Sound.class);
        door0ClosingSound = assetManager.get("audio/sound/door0 closing.wav", Sound.class);
        elevatorSound = assetManager.get("audio/sound/502341__universodemalaonda__elevator-03.wav", Sound.class);
        click1Sound = assetManager.get("audio/sound/click1.wav", Sound.class);
        hoverOverEnterSound = assetManager.get("audio/sound/hoverOverEnter.wav", Sound.class);

        // tiled maps
        testMap = assetManager.get("maps/test.tmx", TiledMap.class);
        level0Map = assetManager.get("maps/level0.tmx", TiledMap.class);

        textureAtlas = assetManager.get("images/included/packed/images.pack.atlas");
        GameUtils.printLoadingTime(getClass().getSimpleName(), startTime);
    }
}

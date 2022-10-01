package no.sandramoen.commanderqueen.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
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

import no.sandramoen.commanderqueen.screens.gameplay.LevelScreen;

public abstract class BaseGame extends Game implements AssetErrorListener {

    private static BaseGame game;
    public static AssetManager assetManager;

    // game assets
    public static TextureAtlas textureAtlas;
    public static Skin mySkin;
    public static LevelScreen levelScreen;

    public static String defaultShader;
    public static String shockwaveShader;

    public static TiledMap testMap;
    public static TiledMap level1Map;
    public static TiledMap level2Map;
    public static TiledMap level3Map;
    public static TiledMap level4Map;
    public static TiledMap level5Map;
    public static TiledMap level6Map;
    public static TiledMap level7Map;

    public static Music menuMusic;
    public static Music levelFinishMusic;
    public static Music level1Music;
    public static Music level2Music;
    public static Music level3Music;
    public static Music level4Music;
    public static Music level5Music;
    public static Music level6Music;
    public static Music level7Music;
    public static Music metalWalkingMusic;
    public static Music ambientFanMusic;
    public static Music chainSawIdleMusic;
    public static Music chainSawAttackingMusic;

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
    public static Sound playerUgh;
    public static Sound secretWallSound;
    public static Sound holyBallSpawnSound;
    public static Sound holyBallExplosionSound;
    public static Sound caseDroppingSound;
    public static Sound chaingunPowerDownSound;
    public static Sound keySound;
    public static Sound doorUnlockedSound;
    public static Sound doorLockedSound;
    public static Sound weaponPickupSound;
    public static Sound rocketLaunchSound;

    // game state
    public static Preferences preferences;
    public static boolean loadPersonalParameters;
    public static boolean isCustomShadersEnabled = true;
    public static float mouseMovementSensitivity = .05f;
    public static boolean isHeadBobbing;
    public static float aspectRatio = 16 / 9f;
    public static float voiceVolume = 1f;
    public static float soundVolume = .5f;
    public static float musicVolume = .1f;
    public static float unitScale = .0621f;
    public static Color redColor = new Color(0.647f, 0.188f, 0.188f, 1f);
    public static Color greenColor = new Color(0.459f, 0.655f, 0.263f, 1f);
    public static Color yellowColor = new Color(0.91f, 0.757f, 0.439f, 1f);
    public static Color darkColor = new Color(.4f, .4f, .4f, 1f);
    public static Color grayColor = new Color(0.506f, 0.592f, 0.588f, 1f);
    public static Color whiteColor = new Color(0.922f, 0.929f, 0.914f, 1f);
    public static Color blueColor = new Color(0.31f, 0.561f, 0.729f, 1f);

    public BaseGame() {
        game = this;
    }

    public void create() {
        Gdx.input.setInputProcessor(new InputMultiplexer());
        loadGameState();
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

    private void loadGameState() {
        GameUtils.loadGameState();
        if (!loadPersonalParameters) {
            soundVolume = .3f;
            musicVolume = .7f;
            voiceVolume = 1f;
            mouseMovementSensitivity = .05f;
            isHeadBobbing = true;
        }
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
        assetManager.load("audio/music/398937__mypantsfelldown__metal-footsteps.wav", Music.class);
        assetManager.load("audio/music/249738__adrilahan__fan.wav", Music.class);
        assetManager.load("audio/music/584444__daniel-prellball__military-tank-music.wav", Music.class);
        assetManager.load("audio/music/559485__code-box__battle-tactics.wav", Music.class);
        assetManager.load("audio/music/595840__lagmusics__heavy-metal-looping.mp3", Music.class);
        assetManager.load("audio/music/524240__badoink__hard-rock-loop.wav", Music.class);
        assetManager.load("audio/music/587251__lagmusics__epic-and-aggressive-percussion.mp3", Music.class);
        assetManager.load("audio/music/578908__lagmusics__virtual-heavy-metal.wav", Music.class);
        assetManager.load("audio/music/457210__kiddpark__13-drum-cadences.wav", Music.class);
        assetManager.load("audio/music/580131__badoink__t-rox.wav", Music.class);
        assetManager.load("audio/music/585515__badoink__glitch-rock-loop.wav", Music.class);
        assetManager.load("audio/sound/453259__kyles__chainsaw-start-and-idle.wav", Music.class);
        assetManager.load("audio/sound/453259__kyles__chainsaw-start-and-idle_HIGH_PITHCED.wav", Music.class);

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
        assetManager.load("audio/sound/player_ugh.wav", Sound.class);
        assetManager.load("audio/sound/243699__ertfelda__hidden-wall-opening.wav", Sound.class);
        assetManager.load("audio/sound/557194__pip__lightsaber-ignition.wav", Sound.class);
        assetManager.load("audio/sound/Laser_Shoot24.wav", Sound.class);
        assetManager.load("audio/sound/414436__inspectorj__dropping-metal-pin-on-wood-b.wav", Sound.class);
        assetManager.load("audio/sound/395378__skylar1146__machinepoweroff.wav", Sound.class);
        assetManager.load("audio/sound/563519__gdog1622__keys-metalretrieve-trimmed-01.wav", Sound.class);
        assetManager.load("audio/sound/131438__skydran__keys-on-door-and-open.wav", Sound.class);
        assetManager.load("audio/sound/391724__jpolito__jp-circuitbox-locked01.wav", Sound.class);
        assetManager.load("audio/sound/433563__burghrecords__cinematic-impact-intro-01.wav", Sound.class);
        assetManager.load("audio/sound/480870__c3sabertooth__launch-futuristic.wav", Sound.class);

        // tiled maps
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("maps/test.tmx", TiledMap.class);
        assetManager.load("maps/level 1.tmx", TiledMap.class);
        assetManager.load("maps/level 2.tmx", TiledMap.class);
        assetManager.load("maps/level 3.tmx", TiledMap.class);
        assetManager.load("maps/level 4.tmx", TiledMap.class);
        assetManager.load("maps/level 5.tmx", TiledMap.class);
        assetManager.load("maps/level 6.tmx", TiledMap.class);
        assetManager.load("maps/level 7.tmx", TiledMap.class);

        assetManager.finishLoading();


        // shaders
        defaultShader = assetManager.get("shaders/default.vs", Text.class).getString();
        shockwaveShader = assetManager.get("shaders/shockwave.fs", Text.class).getString();

        // music
        menuMusic = assetManager.get("audio/music/587251__lagmusics__epic-and-aggressive-percussion.mp3", Music.class);
        levelFinishMusic = assetManager.get("audio/music/457210__kiddpark__13-drum-cadences.wav", Music.class);
        level1Music = assetManager.get("audio/music/584444__daniel-prellball__military-tank-music.wav", Music.class);
        level2Music = assetManager.get("audio/music/559485__code-box__battle-tactics.wav", Music.class);
        level3Music = assetManager.get("audio/music/595840__lagmusics__heavy-metal-looping.mp3", Music.class);
        level4Music = assetManager.get("audio/music/524240__badoink__hard-rock-loop.wav", Music.class);
        level5Music = assetManager.get("audio/music/578908__lagmusics__virtual-heavy-metal.wav", Music.class);
        level6Music = assetManager.get("audio/music/580131__badoink__t-rox.wav", Music.class);
        metalWalkingMusic = assetManager.get("audio/music/398937__mypantsfelldown__metal-footsteps.wav", Music.class);
        ambientFanMusic = assetManager.get("audio/music/249738__adrilahan__fan.wav", Music.class);
        level7Music = assetManager.get("audio/music/585515__badoink__glitch-rock-loop.wav", Music.class);
        chainSawIdleMusic = assetManager.get("audio/sound/453259__kyles__chainsaw-start-and-idle.wav", Music.class);
        chainSawAttackingMusic = assetManager.get("audio/sound/453259__kyles__chainsaw-start-and-idle_HIGH_PITHCED.wav", Music.class);

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
        playerUgh = assetManager.get("audio/sound/player_ugh.wav", Sound.class);
        secretWallSound = assetManager.get("audio/sound/243699__ertfelda__hidden-wall-opening.wav", Sound.class);
        holyBallSpawnSound = assetManager.get("audio/sound/557194__pip__lightsaber-ignition.wav", Sound.class);
        holyBallExplosionSound = assetManager.get("audio/sound/Laser_Shoot24.wav", Sound.class);
        caseDroppingSound = assetManager.get("audio/sound/414436__inspectorj__dropping-metal-pin-on-wood-b.wav", Sound.class);
        chaingunPowerDownSound = assetManager.get("audio/sound/395378__skylar1146__machinepoweroff.wav", Sound.class);
        keySound = assetManager.get("audio/sound/563519__gdog1622__keys-metalretrieve-trimmed-01.wav", Sound.class);
        doorUnlockedSound = assetManager.get("audio/sound/131438__skydran__keys-on-door-and-open.wav", Sound.class);
        doorLockedSound = assetManager.get("audio/sound/391724__jpolito__jp-circuitbox-locked01.wav", Sound.class);
        weaponPickupSound = assetManager.get("audio/sound/433563__burghrecords__cinematic-impact-intro-01.wav", Sound.class);
        rocketLaunchSound = assetManager.get("audio/sound/480870__c3sabertooth__launch-futuristic.wav", Sound.class);

        // tiled maps
        testMap = assetManager.get("maps/test.tmx", TiledMap.class);
        level1Map = assetManager.get("maps/level 1.tmx", TiledMap.class);
        level2Map = assetManager.get("maps/level 2.tmx", TiledMap.class);
        level3Map = assetManager.get("maps/level 3.tmx", TiledMap.class);
        level4Map = assetManager.get("maps/level 4.tmx", TiledMap.class);
        level5Map = assetManager.get("maps/level 5.tmx", TiledMap.class);
        level6Map = assetManager.get("maps/level 6.tmx", TiledMap.class);
        level7Map = assetManager.get("maps/level 7.tmx", TiledMap.class);

        textureAtlas = assetManager.get("images/included/packed/images.pack.atlas");
        GameUtils.printLoadingTime(getClass().getSimpleName(),"Assetmanager", startTime);
    }
}

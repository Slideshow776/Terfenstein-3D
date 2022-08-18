package no.sandramoen.commanderqueen.screens.gameplay.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.screens.gameplay.LevelScreen;
import no.sandramoen.commanderqueen.utils.BaseGame;

public class TileHandler {
    private static float playerPushCounter;
    private static float PLAYER_PUSH_FREQUENCY = .5f;
    private static boolean isPlayerReadyToPush;

    public static void updateTiles(float dt, Array<Tile> tiles, Player player) {
        for (Tile tile : tiles) {
            if (tile.type == "walls" && player.overlaps(tile))
                player.preventOverlap(tile);

            if (player.isWithinDistance(Tile.height, tile) && tile.type == "walls" && isPlayerReadyToPush && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                if (!tile.secret.isEmpty() && tile.triggerSecret())
                    LevelScreen.foundSecrets++;
                BaseGame.playerUgh.play(BaseGame.soundVolume, MathUtils.random(.9f, 1.1f), 0);
                isPlayerReadyToPush = false;
            }
        }

        if (playerPushCounter > PLAYER_PUSH_FREQUENCY) {
            playerPushCounter = 0;
            isPlayerReadyToPush = true;
        } else {
            playerPushCounter += dt;
        }
    }
}

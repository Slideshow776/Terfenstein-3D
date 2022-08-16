package no.sandramoen.commanderqueen.screens.gameplay.level;

import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.Player;

public class TileHandler {
    public static void updateTiles(Array<Tile> tiles, Player player) {
        for (Tile tile : tiles) {
            if (tile.type == "walls" && player.overlaps(tile))
                player.preventOverlap(tile);
        }
    }
}

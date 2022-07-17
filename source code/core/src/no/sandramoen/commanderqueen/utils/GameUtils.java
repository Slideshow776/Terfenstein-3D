package no.sandramoen.commanderqueen.utils;

import no.sandramoen.commanderqueen.actors.Tile;

public class GameUtils {
    public static float getPositionRelativeToFloor(float height) {
        return (Tile.height - height) / -2;
    }
}

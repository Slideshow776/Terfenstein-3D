package no.sandramoen.commanderqueen.screens.gameplay.level;

import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.enemy.Enemy;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.GameUtils;

public class EnemyHandler {

    public static void preventOverlapWithOtherEnemies(Array<Enemy> enemies, int i) {
        for (int j = 0; j < enemies.size; j++) {
            if (enemies.get(i) != enemies.get(j))
                enemies.get(i).preventOverlap(enemies.get(j));
        }
    }

    public static void preventOverLapWithTile(Array<Enemy> enemies, Tile tile, int i) {
        if (tile.type == "walls" && enemies.get(i).overlaps(tile))
            enemies.get(i).preventOverlap(tile);
    }

    public static void illuminate(boolean intervalFlag, Array<Enemy> enemies, Array<Tile> tiles, int i) {
        for (Tile tile : tiles) {
            EnemyHandler.preventOverLapWithTile(enemies, tile, i);
            if (intervalFlag && enemies.get(i).overlaps(tile))
                GameUtils.illuminateBaseActor(enemies.get(i), tile);
        }
    }

    public static void updateEnemiesShootableList(Array<Enemy> enemies, Array<BaseActor3D> shootable) {
        for (int i = 0; i < enemies.size; i++)
            enemies.get(i).setShootableList(shootable);
    }

    public static void updateEnemiesEnemiesList(Array<Enemy> enemies) {
        for (int i = 0; i < enemies.size; i++)
            enemies.get(i).setEnemiesList(enemies);
    }

    public static void activateEnemies(Array<Enemy> enemies, float range, BaseActor3D source) {
        for (Enemy enemy : enemies) {
            if (enemy.isWithinDistance(range, source))
                enemy.activate(source);
        }
    }
}

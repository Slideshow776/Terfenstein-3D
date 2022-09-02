package no.sandramoen.commanderqueen.screens.gameplay.level;

import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Barrel;
import no.sandramoen.commanderqueen.actors.Door;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.HolyBall;
import no.sandramoen.commanderqueen.actors.characters.Hund;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.characters.Sersjant;
import no.sandramoen.commanderqueen.actors.characters.enemy.Enemy;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.GameUtils;

public class EnemyHandler {

    public static void update(
            Array<Enemy> enemies, Array<Tile> tiles, Array<Door> doors, Array<BaseActor3D> projectiles, Player player,
            Array<BaseActor3D> shootable, HUD hud, Array<TileShade> tileShades
    ) {

        for (int i = 0; i < enemies.size; i++) {
            preventOverlapWithOtherEnemies(enemies, i);
            preventOverLapWithTile(tiles, enemies.get(i));
            preventOverlapWithDoors(doors, enemies.get(i));
            handleProjectiles(projectiles, player, shootable, hud);
        }

        /*GameUtils.checkEnemyShading(tileShades, enemies);*/
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

    private static void preventOverlapWithOtherEnemies(Array<Enemy> enemies, int i) {
        for (int j = 0; j < enemies.size; j++) {
            if (enemies.get(i) != enemies.get(j))
                if (!(enemies.get(i) instanceof Hund) || !(enemies.get(j) instanceof Hund))
                    enemies.get(i).preventOverlap(enemies.get(j));
        }
    }

    private static void preventOverLapWithTile(Array<Tile> tiles, Enemy enemy) {
        for (Tile tile : tiles) {
            if (tile.type.equalsIgnoreCase("1st floor") && enemy.overlaps(tile)) {
                enemy.preventOverlap(tile);
                enemy.isForcedToMove = false;
            }
        }
    }

    private static void preventOverlapWithDoors(Array<Door> doors, Enemy enemy) {
        for (Door door : doors) {
            if (enemy.overlaps(door)) {
                door.openAndClose();
                enemy.preventOverlap(door);
                enemy.isForcedToMove = false;
            }
        }
    }

    private static void handleProjectiles(Array<BaseActor3D> projetiles, Player player, Array<BaseActor3D> shootable, HUD hud) {
        checkProjectilesCollision(projetiles, player, shootable, hud);
        removeProjectiles(projetiles);
    }

    private static void checkProjectilesCollision(Array<BaseActor3D> projectiles, Player player, Array<BaseActor3D> shootables, HUD hud) {
        for (BaseActor3D projectile : projectiles) {
            for (BaseActor3D shootable : shootables) {
                if (shootable instanceof Barrel && projectile.overlaps(shootable)) {
                    if (projectile instanceof HolyBall) {
                        HolyBall holyBall = (HolyBall) projectile;
                        ((Barrel) shootable).decrementHealth(holyBall.getDamage(), projectile.distanceBetween(player));
                        holyBall.explode();
                    }
                } else if (shootable instanceof Tile && ((Tile) shootable).type.equalsIgnoreCase("walls") && projectile.overlaps(shootable)) {
                    if (projectile instanceof HolyBall)
                        ((HolyBall) projectile).explode();
                }
            }
            if (projectile.overlaps(player)) {
                player.forceMoveAwayFrom(projectile);
                if (projectile instanceof HolyBall) {
                    HolyBall holyBall = (HolyBall) projectile;
                    hud.decrementHealth(holyBall.getDamage(), projectile);
                    holyBall.explode();
                }
            }
        }
    }

    private static void removeProjectiles(Array<BaseActor3D> projectiles) {
        for (BaseActor3D projectile : projectiles) {
            if (projectile instanceof HolyBall && ((HolyBall) projectile).isRemovable) {
                projectile.remove();
                projectiles.removeValue(projectile, false);
            }
        }
    }
}

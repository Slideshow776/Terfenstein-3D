package no.sandramoen.commanderqueen.screens.gameplay.level;

import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Barrel;
import no.sandramoen.commanderqueen.actors.Door;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.HolyBall;
import no.sandramoen.commanderqueen.actors.characters.Hund;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.characters.Rocket;
import no.sandramoen.commanderqueen.actors.characters.Sersjant;
import no.sandramoen.commanderqueen.actors.characters.enemy.Enemy;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class EnemyHandler {

    public static void update(
            Array<Enemy> enemies, Array<Tile> tiles, Array<Door> doors, Array<BaseActor3D> projectiles, Player player,
            Array<BaseActor3D> shootable, HUD hud, Stage3D stage3D
    ) {

        for (int i = 0; i < enemies.size; i++) {
            preventOverlapWithOtherEnemies(enemies, i);
            preventOverLapWithTile(tiles, enemies.get(i));
            preventOverlapWithDoors(doors, enemies.get(i));
        }

        handleProjectiles(projectiles, player, shootable, hud, stage3D);
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

    private static void handleProjectiles(Array<BaseActor3D> projetiles, Player player, Array<BaseActor3D> shootable, HUD hud, Stage3D stage3D) {
        checkProjectilesCollision(projetiles, player, shootable, hud, stage3D);
        removeProjectiles(projetiles);
    }

    private static void checkProjectilesCollision(Array<BaseActor3D> projectiles, Player player, Array<BaseActor3D> shootables, HUD hud, Stage3D stage3D) {
        for (BaseActor3D projectile : projectiles) {
            for (BaseActor3D shootable : shootables) {
                if (shootable instanceof Barrel && projectile.overlaps(shootable)) {
                    if (projectile instanceof HolyBall) {
                        HolyBall holyBall = (HolyBall) projectile;
                        ((Barrel) shootable).decrementHealth(holyBall.getDamage(), projectile.distanceBetween(player));
                        holyBall.explode();
                    } else if (projectile instanceof Rocket) {
                        Rocket Rocket = (Rocket) projectile;
                        ((Barrel) shootable).decrementHealth(Rocket.getDamage(), projectile.distanceBetween(player));
                        Rocket.explode();
                        createBarrelExplosion(projectile.getPosition().y, projectile.getPosition().z, stage3D, player, shootables);
                    }
                } else if (shootable instanceof Tile && ((Tile) shootable).type.equalsIgnoreCase("1st floor") && projectile.overlaps(shootable)) {
                    if (projectile instanceof HolyBall)
                        ((HolyBall) projectile).explode();
                    else if (projectile instanceof Rocket) {
                        ((Rocket) projectile).explode();
                        createBarrelExplosion(projectile.getPosition().y, projectile.getPosition().z, stage3D, player, shootables);
                    }
                } else if (shootable instanceof Door && projectile.overlaps(shootable)) {
                    if (projectile instanceof HolyBall)
                        ((HolyBall) projectile).explode();
                    else if (projectile instanceof Rocket) {
                        ((Rocket) projectile).explode();
                        createBarrelExplosion(projectile.getPosition().y, projectile.getPosition().z, stage3D, player, shootables);
                    }
                } else if (shootable instanceof Enemy && projectile.overlaps(shootable)) {
                    if (projectile instanceof Rocket) {
                        ((Rocket) projectile).explode();

                        ((Enemy) shootable).decrementHealth(((Rocket) projectile).getDamage());
                        createBarrelExplosion(projectile.getPosition().y, projectile.getPosition().z, stage3D, player, shootables);
                    }
                }
            }

            if (projectile.overlaps(player)) {
                if (projectile instanceof HolyBall) {
                    player.forceMoveAwayFrom(projectile);
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

            if (projectile instanceof Rocket && ((Rocket) projectile).isRemovable) {
                projectile.remove();
                projectiles.removeValue(projectile, false);
            }
        }
    }

    private static void createBarrelExplosion(float x, float y, Stage3D stage3D, Player player, Array<BaseActor3D> shootables) {
        Barrel temp = new Barrel(x, y, stage3D, player);
        temp.isVisible = false;
        shootables.add(temp);
        temp.health = 0;
    }
}

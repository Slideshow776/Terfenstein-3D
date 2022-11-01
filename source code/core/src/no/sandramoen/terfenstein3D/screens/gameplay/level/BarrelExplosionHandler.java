package no.sandramoen.terfenstein3D.screens.gameplay.level;

import com.badlogic.gdx.utils.Array;

import no.sandramoen.terfenstein3D.actors.Barrel;
import no.sandramoen.terfenstein3D.actors.characters.Player;
import no.sandramoen.terfenstein3D.actors.hud.HUD;
import no.sandramoen.terfenstein3D.actors.characters.enemy.Enemy;
import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor3D;

public class BarrelExplosionHandler {

    public static void checkExplosionRange(HUD hud, Player player, Array<Enemy> enemies, Array<BaseActor3D> shootable, BaseActor3D source) {
        checkPlayerExplosionDamage(hud, player, source);
        checkEnemiesExplosionDamage(enemies, source, player);
        checkBarrelsExplosionDamage(shootable, source, player);
    }

    private static void checkPlayerExplosionDamage(HUD hud, Player player, BaseActor3D source) {
        if (source instanceof Barrel) {
            Barrel barrel = (Barrel) source;
            hud.decrementHealth(barrel.getBlastDamage(source.distanceBetween(player)), source);

            if (player.isWithinDistance(barrel.BLAST_RANGE, source))
                player.forceMoveAwayFrom(source);
            else
                hud.setKillFace();
        }
    }

    private static void checkEnemiesExplosionDamage(Array<Enemy> enemies, BaseActor3D source, Player player) {
        if (source instanceof Barrel) {
            Barrel barrel = (Barrel) source;

            for (Enemy enemy : enemies) {
                if (source.distanceBetween(enemy) <= barrel.BLAST_RANGE * 1.5f) {
                    enemy.decrementHealth(barrel.getBlastDamage(source.distanceBetween(enemy)));
                    if (enemy.isWithinDistance(barrel.BLAST_RANGE, source))
                        enemy.forceMoveAwayFrom(source);
                }
            }

            player.shakeyCam(1, .2f);
        }
    }



    private static void checkBarrelsExplosionDamage(Array<BaseActor3D> shootable, BaseActor3D source, Player player) {
        if (source instanceof Barrel) {
            Barrel barrel = (Barrel) source;

            for (BaseActor3D baseActor3D : shootable) {
                if (baseActor3D instanceof Barrel) {
                    Barrel otherBarrel = (Barrel) baseActor3D;
                    otherBarrel.decrementHealth(barrel.getBlastDamage(source.distanceBetween(otherBarrel)), 0);
                }
            }

            player.shakeyCam(1, .2f);
        }
    }
}

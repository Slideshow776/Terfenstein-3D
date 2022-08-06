package no.sandramoen.commanderqueen.utils.level;

import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Barrel;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.utils.Enemy;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.GameUtils;

public class BarrelExplosionHandler {

    public static void checkExplosionRange(HUD hud, Player player, Array<Enemy> enemies, Array<BaseActor3D> shootable, BaseActor3D source) {
        checkPlayerExplosionDamage(hud, player, source);
        checkEnemiesExplosionDamage(enemies, source);
        checkBarrelsExplosionDamage(shootable, source);
    }

    private static void checkBarrelsExplosionDamage(Array<BaseActor3D> shootable, BaseActor3D source) {
        if (GameUtils.isActor(source, "barrel")) {
            Barrel barrel = (Barrel) source;

            for (BaseActor3D baseActor3D : shootable) {
                if (GameUtils.isActor(baseActor3D, "barrel")) {
                    Barrel otherBarrel = (Barrel) baseActor3D;
                    otherBarrel.decrementHealth(barrel.getBlastDamage(source.distanceBetween(otherBarrel)), 0);
                }
            }
        }
    }

    private static void checkPlayerExplosionDamage(HUD hud, Player player, BaseActor3D source) {
        if (GameUtils.isActor(source, "barrel")) {
            Barrel barrel = (Barrel) source;
            hud.decrementHealth(barrel.getBlastDamage(source.distanceBetween(player)), source);

            if (player.isWithinDistance(barrel.BLAST_RANGE, source))
                player.forceMoveAwayFrom(source);
            else
                hud.setKillFace();
        }
    }

    private static void checkEnemiesExplosionDamage(Array<Enemy> enemies, BaseActor3D source) {
        if (GameUtils.isActor(source, "barrel")) {
            Barrel barrel = (Barrel) source;

            for (Enemy enemy : enemies) {
                enemy.decrementHealth(barrel.getBlastDamage(source.distanceBetween(enemy)));
                if (enemy.isWithinDistance(barrel.BLAST_RANGE, source))
                    enemy.forceMoveAwayFrom(source);
            }
        }
    }
}

package no.sandramoen.commanderqueen.screens.gameplay.level;

import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.pickups.Bullets;
import no.sandramoen.commanderqueen.actors.pickups.Armor;
import no.sandramoen.commanderqueen.actors.pickups.Health;
import no.sandramoen.commanderqueen.actors.pickups.Pickup;
import no.sandramoen.commanderqueen.actors.pickups.Shells;
import no.sandramoen.commanderqueen.actors.pickups.Shotgun;
import no.sandramoen.commanderqueen.actors.weapon.WeaponHandler;

public class PickupHandler {

    public static void update(Array<Pickup> pickups, Player player, HUD hud, WeaponHandler weaponHandler) {
        for (Pickup pickup : pickups) {
            if (player.overlaps(pickup)) {
                if (pickup instanceof Bullets || pickup instanceof Shells) {
                    hud.incrementAmmo(pickup, weaponHandler.currentWeapon);
                    removePickup(pickups, pickup);
                } else if (pickup instanceof Armor) {
                    if (hud.incrementArmor(pickup.amount, false))
                        removePickup(pickups, pickup);
                } else if (pickup instanceof Health) {
                    if (hud.incrementHealth(pickup.amount))
                        removePickup(pickups, pickup);
                } else if (pickup instanceof Shotgun) {
                    weaponHandler.makeAvailable("shotgun");
                    removePickup(pickups, pickup);
                }
            }
        }
    }

    private static void removePickup(Array<Pickup> pickups, Pickup pickup) {
        pickups.removeValue(pickup, false);
        pickup.playSound();
        pickup.remove();
    }
}

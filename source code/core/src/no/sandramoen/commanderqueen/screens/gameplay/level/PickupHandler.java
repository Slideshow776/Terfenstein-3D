package no.sandramoen.commanderqueen.screens.gameplay.level;

import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.pickups.Ammo;
import no.sandramoen.commanderqueen.actors.pickups.Armor;
import no.sandramoen.commanderqueen.actors.pickups.Health;
import no.sandramoen.commanderqueen.actors.pickups.Pickup;
import no.sandramoen.commanderqueen.actors.weapon.WeaponHandler;

public class PickupHandler {

    public static void update(Array<Pickup> pickups, Player player, HUD hud, WeaponHandler weaponHandler) {
        for (Pickup pickup : pickups) {
            if (player.overlaps(pickup)) {
                if (pickup instanceof Ammo) {
                    hud.incrementAmmo(pickup.amount, weaponHandler.currentWeapon.getClass().getSimpleName());
                    removePickup(pickups, pickup);
                }
                if (pickup instanceof Armor) {
                    if (hud.incrementArmor(pickup.amount, false))
                        removePickup(pickups, pickup);
                }
                if (pickup instanceof Health) {
                    if (hud.incrementHealth(pickup.amount))
                        removePickup(pickups, pickup);
                }
            }
        }
    }

    private static void removePickup(Array<Pickup> pickups, Pickup pickup) {
        pickups.removeValue(pickup, false);
        pickup.remove();
    }
}

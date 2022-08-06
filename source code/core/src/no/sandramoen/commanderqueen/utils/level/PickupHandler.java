package no.sandramoen.commanderqueen.utils.level;

import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.pickups.Pickup;
import no.sandramoen.commanderqueen.utils.GameUtils;

public class PickupHandler {

    public static void updatePickups(Array<Pickup> pickups, Player player, HUD hud) {
        for (Pickup pickup : pickups) {
            if (player.overlaps(pickup)) {
                if (GameUtils.isActor(pickup, "Ammo")) {
                    hud.incrementAmmo(pickup.amount);
                    removePickup(pickups, pickup);
                }
                if (GameUtils.isActor(pickup, "Armor")) {
                    if (hud.incrementArmor(pickup.amount, false))
                        removePickup(pickups, pickup);
                }
                if (GameUtils.isActor(pickup, "Health")) {
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

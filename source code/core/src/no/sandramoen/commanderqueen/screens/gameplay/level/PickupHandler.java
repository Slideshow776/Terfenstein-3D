package no.sandramoen.commanderqueen.screens.gameplay.level;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.pickups.Bullets;
import no.sandramoen.commanderqueen.actors.pickups.Armor;
import no.sandramoen.commanderqueen.actors.pickups.Chaingun;
import no.sandramoen.commanderqueen.actors.pickups.Health;
import no.sandramoen.commanderqueen.actors.pickups.Key;
import no.sandramoen.commanderqueen.actors.pickups.Pickup;
import no.sandramoen.commanderqueen.actors.pickups.Shells;
import no.sandramoen.commanderqueen.actors.pickups.Shotgun;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor;
import no.sandramoen.commanderqueen.actors.weapon.WeaponHandler;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class PickupHandler {

    public static void update(
            Array<Pickup> pickups, Player player, HUD hud, WeaponHandler weaponHandler,
            Table uiTable, UIHandler uiHandler, Stage3D stage3D
    ) {
        for (Pickup pickup : pickups) {
            if (player.overlaps(pickup)) {
                if (pickup instanceof Bullets || pickup instanceof Shells) {
                    hud.incrementAmmunition(pickup, weaponHandler.currentWeapon);
                    if (pickup instanceof Bullets)
                        setPickupLabel(uiHandler, hud, "You picked up some bullets!");
                    if (pickup instanceof Shells)
                        setPickupLabel(uiHandler, hud, "You picked up some shotgun shells!");
                    removePickup(pickups, pickup);
                } else if (pickup instanceof Armor) {
                    if (hud.incrementArmor(pickup.amount, false)) {
                        removePickup(pickups, pickup);
                        setPickupLabel(uiHandler, hud, "You picked up some armor!");
                    }
                } else if (pickup instanceof Health) {
                    if (hud.incrementHealth(pickup.amount)) {
                        removePickup(pickups, pickup);
                        setPickupLabel(uiHandler, hud, "You picked some health!");
                    }
                } else if (pickup instanceof Key) {
                    hud.addKey((Key) pickup);
                    removePickup(pickups, pickup);
                    String color = "";
                    if (((Key) pickup).color.equalsIgnoreCase("red"))
                        color = "{COLOR=" + BaseGame.redColor + "}";
                    else if (((Key) pickup).color.equalsIgnoreCase("green"))
                        color = "{COLOR=" + BaseGame.greenColor + "}";
                    else if (((Key) pickup).color.equalsIgnoreCase("blue"))
                        color = "{COLOR=" + BaseGame.blueColor + "}";
                    setPickupLabel(uiHandler, hud, "You picked up a " + color + ((Key) pickup).color + " key{CLEARCOLOR}!");
                } else if (pickup instanceof Shotgun) {
                    weaponHandler.makeAvailable("shotgun");
                    Shells shells = new Shells(0, 0, stage3D, 8, player);
                    hud.incrementAmmunition(shells, weaponHandler.currentWeapon);
                    shells.remove();

                    pickUpWeapon(hud, pickups, pickup, weaponHandler, uiTable, uiHandler);
                    setPickupLabel(uiHandler, hud, "You picked up a shotgun!");
                } else if (pickup instanceof Chaingun) {
                    weaponHandler.makeAvailable("chaingun");
                    Bullets bullets = new Bullets(0, 0, stage3D, 20, player);
                    hud.incrementAmmunition(bullets, weaponHandler.currentWeapon);
                    bullets.remove();

                    pickUpWeapon(hud, pickups, pickup, weaponHandler, uiTable, uiHandler);
                    setPickupLabel(uiHandler, hud, "You picked up a chaingun!");
                }
            }
        }
    }

    private static void removePickup(Array<Pickup> pickups, Pickup pickup) {
        pickups.removeValue(pickup, false);
        pickup.playSound();
        pickup.remove();
    }

    private static void pickUpWeapon(HUD hud, Array<Pickup> pickups, Pickup pickup, WeaponHandler weaponHandler, Table uiTable, UIHandler uiHandler) {
        hud.setEvilFace();
        removePickup(pickups, pickup);

        hud.setWeaponsTable(weaponHandler);
        uiTable.reset();
        uiHandler.isReset = true;
    }

    public static void setPickupLabel(UIHandler uiHandler, BaseActor baseActor, String message) {
        baseActor.addAction(Actions.sequence(
                Actions.delay(.1f),
                Actions.run(() -> {
                    uiHandler.setPickupLabel(message);
                })
        ));
    }
}

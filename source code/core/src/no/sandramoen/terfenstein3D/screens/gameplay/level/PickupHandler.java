package no.sandramoen.terfenstein3D.screens.gameplay.level;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.terfenstein3D.actors.characters.Player;
import no.sandramoen.terfenstein3D.actors.hud.HUD;
import no.sandramoen.terfenstein3D.actors.pickups.Bullets;
import no.sandramoen.terfenstein3D.actors.pickups.Armor;
import no.sandramoen.terfenstein3D.actors.pickups.Chaingun;
import no.sandramoen.terfenstein3D.actors.pickups.Chainsaw;
import no.sandramoen.terfenstein3D.actors.pickups.Health;
import no.sandramoen.terfenstein3D.actors.pickups.Key;
import no.sandramoen.terfenstein3D.actors.pickups.Pickup;
import no.sandramoen.terfenstein3D.actors.pickups.Rocket;
import no.sandramoen.terfenstein3D.actors.pickups.RocketLauncher;
import no.sandramoen.terfenstein3D.actors.pickups.Shells;
import no.sandramoen.terfenstein3D.actors.pickups.Shotgun;
import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor;
import no.sandramoen.terfenstein3D.actors.weapon.WeaponHandler;
import no.sandramoen.terfenstein3D.utils.BaseGame;
import no.sandramoen.terfenstein3D.utils.Stage3D;

public class PickupHandler {

    public static void update(
            Array<Pickup> pickups, Player player, HUD hud, WeaponHandler weaponHandler,
            Table uiTable, UIHandler uiHandler, Stage3D stage3D
    ) {
        for (Pickup pickup : pickups) {
            if (player.overlaps(pickup)) {
                if (pickup instanceof Bullets || pickup instanceof Shells || pickup instanceof Rocket) {
                    hud.incrementAmmunition(pickup, weaponHandler.currentWeapon);
                    if (pickup instanceof Bullets)
                        setPickupLabel(uiHandler, hud, "You picked up some bullets!", false);
                    if (pickup instanceof Shells)
                        setPickupLabel(uiHandler, hud, "You picked up some shotgun shells!", false);
                    if (pickup instanceof Rocket)
                        setPickupLabel(uiHandler, hud, "You picked up a rocket!", false);
                    removePickup(pickups, pickup);
                } else if (pickup instanceof Armor) {
                    if (hud.incrementArmor(pickup.amount, false)) {
                        removePickup(pickups, pickup);
                        setPickupLabel(uiHandler, hud, "You picked up some armor!", false);
                    }
                } else if (pickup instanceof Health) {
                    if (hud.incrementHealth(pickup.amount)) {
                        removePickup(pickups, pickup);
                        setPickupLabel(uiHandler, hud, "You picked some health!", false);
                    }
                } else if (pickup instanceof Key) {
                    hud.addKey((Key) pickup);
                    removePickup(pickups, pickup);
                    String color = "";
                    if (((Key) pickup).color.equalsIgnoreCase("red")) {
                        color = "{COLOR=" + BaseGame.redColor + "}";
                        hud.flash(BaseGame.redColor);
                    } else if (((Key) pickup).color.equalsIgnoreCase("green")) {
                        color = "{COLOR=" + BaseGame.greenColor + "}";
                        hud.flash(BaseGame.greenColor);
                    } else if (((Key) pickup).color.equalsIgnoreCase("blue")) {
                        color = "{COLOR=" + BaseGame.blueColor + "}";
                        hud.flash(BaseGame.blueColor);
                    }
                    setPickupLabel(uiHandler, hud, "You picked up a " + color + ((Key) pickup).color + " key{CLEARCOLOR}!", true);
                } else if (pickup instanceof Shotgun) {
                    weaponHandler.makeAvailable("shotgun");
                    Shells shells = new Shells(0, 0, stage3D, 8, player);
                    hud.incrementAmmunition(shells, weaponHandler.currentWeapon);
                    shells.remove();

                    pickUpWeapon(hud, pickups, pickup, weaponHandler, uiTable, uiHandler);
                    setPickupLabel(uiHandler, hud, "You picked up a shotgun!", true);
                } else if (pickup instanceof Chaingun) {
                    weaponHandler.makeAvailable("chaingun");
                    Bullets bullets = new Bullets(0, 0, stage3D, 20, player);
                    hud.incrementAmmunition(bullets, weaponHandler.currentWeapon);
                    bullets.remove();

                    pickUpWeapon(hud, pickups, pickup, weaponHandler, uiTable, uiHandler);
                    setPickupLabel(uiHandler, hud, "You picked up a chaingun!", true);
                } else if (pickup instanceof RocketLauncher) {
                    weaponHandler.makeAvailable("rocketLauncher");
                    Rocket rockets = new Rocket(0, 0, stage3D, 15, player);
                    hud.incrementAmmunition(rockets, weaponHandler.currentWeapon);
                    rockets.remove();

                    pickUpWeapon(hud, pickups, pickup, weaponHandler, uiTable, uiHandler);
                    setPickupLabel(uiHandler, hud, "You picked up a rocket launcher!", true);
                } else if (pickup instanceof Chainsaw) {
                    weaponHandler.makeAvailable("chainsaw");
                    pickUpWeapon(hud, pickups, pickup, weaponHandler, uiTable, uiHandler);
                    setPickupLabel(uiHandler, hud, "You picked up a chainsaw!", true);
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
        BaseGame.weaponPickupSound.play(BaseGame.soundVolume);
        removePickup(pickups, pickup);

        hud.setWeaponsTable(weaponHandler);
        uiTable.reset();
        uiHandler.isReset = true;
    }

    public static void setPickupLabel(UIHandler uiHandler, BaseActor baseActor, String message, boolean important) {
        baseActor.addAction(Actions.sequence(
                Actions.delay(.1f),
                Actions.run(() -> {
                    uiHandler.setPickupLabel(message, important);
                })
        ));
    }
}

package no.sandramoen.commanderqueen.actors;

import no.sandramoen.commanderqueen.actors.utils.Enemy;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Ghoul extends Enemy {
    public Ghoul(float y, float z, Stage3D s, Player player) {
        super(y, z, s, player);
        loadImage("enemies/ghoul walk 1");
    }
}

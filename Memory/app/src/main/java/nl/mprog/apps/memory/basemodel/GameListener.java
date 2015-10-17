package nl.mprog.apps.memory.basemodel;

import java.io.Serializable;

interface GameListener extends Serializable {
    public void gameStateChanged(Game game);
}

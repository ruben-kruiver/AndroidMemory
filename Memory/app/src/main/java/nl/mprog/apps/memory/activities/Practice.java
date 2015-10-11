package nl.mprog.apps.memory.activities;

import nl.mprog.apps.memory.interfaces.GameActivity;
import nl.mprog.apps.memory.models.Memory;
import nl.mprog.apps.memory.models.game.PracticeGame;

public class Practice extends GameActivity {

    @Override
    protected void newGame() {
        this.game = new PracticeGame(this);
    }

    @Override
    protected String getPersistenceFilename() {
        return Memory.PERSISTENCE_FILENAME_PRACTICE;
    }
}

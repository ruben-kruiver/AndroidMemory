package nl.mprog.apps.memory.activity;

import nl.mprog.apps.memory.basemodel.Game;
import nl.mprog.apps.memory.basemodel.GameActivity;
import nl.mprog.apps.memory.model.Memory;
import nl.mprog.apps.memory.model.game.PracticeGame;

public class Practice extends GameActivity {

    @Override
    protected Game newGame() {
        return new PracticeGame();
    }

    @Override
    protected String getPersistenceFilename() {
        return Memory.PERSISTENCE_FILENAME_PRACTICE;
    }
}

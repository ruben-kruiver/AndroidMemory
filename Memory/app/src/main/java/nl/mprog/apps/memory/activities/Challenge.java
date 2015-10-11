package nl.mprog.apps.memory.activities;

import nl.mprog.apps.memory.interfaces.GameActivity;
import nl.mprog.apps.memory.models.Memory;
import nl.mprog.apps.memory.models.game.ChallengeGame;

public class Challenge extends GameActivity {

    @Override
    protected void newGame() {
        this.game = new ChallengeGame(this);
    }

    @Override
    protected String getPersistenceFilename() {
        return Memory.PERSISTENCE_FILENAME_CHALLENGE;
    }
}
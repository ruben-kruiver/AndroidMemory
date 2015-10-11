package nl.mprog.apps.memory.models.game;

import android.content.Context;

import nl.mprog.apps.memory.interfaces.Game;

public class PracticeGame extends Game {

    public PracticeGame(Context context) {
        super(context);
    }

    @Override
    public void loadPlayingCards() {
        for (int i = 0; i < this.getNumberOfCards(); i++) {
            this.addCardToSet(i);
        }
    }

    protected Integer getNumberOfCards() {
        // For each 3 levels add 1 cardset to the game with a minimum of 4
        return 4 + ((int) Math.floor((this.currentLevel.intValue() / 3)));
    }
}
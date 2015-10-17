package nl.mprog.apps.memory.model.game;

import nl.mprog.apps.memory.basemodel.Game;

public class PracticeGame extends Game {

    public PracticeGame() {
        super();
    }

    @Override
    public void loadPlayingCards() {
        for (int i = 0; i < this.getNumberOfCards(); i++) {
            this.addCardToGame(i);
        }

        this.currentTimelimit = this.timeLimit;
        this.currentMistakesLimit = this.maximumMistakes;
    }

    protected Integer getNumberOfCards() {
        // For each 3 levels add 1 cardset to the game with a minimum of 4
        return 4 + ((int) Math.floor((this.currentLevel.intValue() / 3)));
    }
}
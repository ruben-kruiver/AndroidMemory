package nl.mprog.apps.memory.model.game;

import nl.mprog.apps.memory.basemodel.Game;
import nl.mprog.apps.memory.model.Memory;

/**
 * This Game type sets the number of cards, currentTime and the maximum mistakes
 * the values will vary in blocks of three levels where the number of cards
 * will increase and the currentTime and number of mistakes will decrease
 */
public class ChallengeGame extends Game {

    public ChallengeGame() {
        super();
    }

    @Override
    public void loadPlayingCards() {
        if (this.currentTimelimit == null) {
            this.currentTimelimit = this.timeLimit;
        }

        if (this.currentMistakesLimit == null) {
            this.currentMistakesLimit = this.maximumMistakes;
        }

        for (int i = 0; i < this.getNumberOfCards(); i++) {
            this.addCardToGame(i);
        }

        this.setTimeLimit(this.calculateTimelimit());

        this.setMaximumMistakes(this.calculateMaximumMistakes());
    }

    @Override
    public boolean hasTimeRemaining() {
        return (this.currentTimelimit - this.getCurrentTime()) > 0;
    }

    @Override
    public boolean hasMistakesRemaining() {
        return (this.maximumMistakes - this.currentMistakes) > 0;
    }

    protected Integer getNumberOfCards() {
        return 4 + ((int) Math.floor(this.currentLevel.intValue() / 3));
    }

    protected Integer calculateTimelimit() {
        int timelimitDiff = this.currentTimelimit - Memory.MIN_VALUE_TIME_LIMIT;
        int timeStepValue = ((int) Math.floor(timelimitDiff / 3));
        int stepCounter = this.currentLevel % 3;

        return this.timeLimit - (stepCounter * timeStepValue);
    }

    protected Integer calculateMaximumMistakes() {
        int mistakesDiff = this.currentMistakesLimit - Memory.MIN_VALUE_MISTAKES;
        int mistakesStepValue = ((int) Math.floor(mistakesDiff / 3));
        int stepCounter = this.currentLevel % 3;

        return this.maximumMistakes - (stepCounter * mistakesStepValue);
    }
}
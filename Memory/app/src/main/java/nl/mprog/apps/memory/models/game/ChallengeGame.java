package nl.mprog.apps.memory.models.game;

import android.content.Context;

import nl.mprog.apps.memory.interfaces.Game;
import nl.mprog.apps.memory.models.Memory;

/**
 * This Game type set the number of cards, currentTime and the maximummistakes
 * the values will vary in blocks of three levels where the number of cards
 * will increase and the currentTime and number of mistakes will decrease
 */
public class ChallengeGame extends Game {
    protected Integer maximumTimeLimit;

    protected Integer mistakesLimit;

    public ChallengeGame(Context context) {
        super(context);
    }

    @Override
    public void loadPlayingCards() {
        if (this.maximumTimeLimit == null) {
            this.maximumTimeLimit = this.timeLimit;
            this.mistakesLimit = this.maximumMistakes;
        }

        for (int i = 0; i < this.getNumberOfCards(); i++) {
            this.addCardToSet(i);
        }

        this.setTimeLimit(this.calculateTimelimit());

        this.setMaximumMistakes(this.calculateMaximumMistakes());
    }

    @Override
    public boolean hasTimeRemaining(int currentTime) {
        return (this.maximumTimeLimit - currentTime) > 0;
    }

    @Override
    public boolean hasMistakesRemaining() {
        return (this.maximumMistakes - this.currentMistakes) > 0;
    }

    protected Integer getNumberOfCards() {
        return 4 + ((int) Math.floor(this.currentLevel.intValue() / 3));
    }

    protected Integer calculateTimelimit() {
        int timelimitDiff = this.maximumTimeLimit - Memory.MIN_VALUE_TIME_LIMIT;
        int timeStepValue = ((int) Math.floor(timelimitDiff / 3));
        int stepCounter = this.currentLevel % 3;

        return this.maximumTimeLimit - (stepCounter * timeStepValue);
    }

    protected Integer calculateMaximumMistakes() {
        int mistakesDiff = this.mistakesLimit - Memory.MIN_VALUE_MISTAKES;
        int mistakesStepValue = ((int) Math.floor(mistakesDiff / 3));
        int stepCounter = this.currentLevel % 3;

        return this.mistakesLimit - (stepCounter * mistakesStepValue);
    }
}
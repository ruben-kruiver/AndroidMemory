package nl.mprog.apps.memory.basemodel;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import nl.mprog.apps.memory.model.Card;
import nl.mprog.apps.memory.model.Memory;

public abstract class Game {

    /**
     * These attributes contain the initial settings for the game instance
     */
    protected Long startTime;

    protected Integer timeLimit;

    protected Integer maximumMistakes;

    protected Integer cardsPerSet;

    /**
     * These attributes contain the values that are set during the game
     */

    protected Integer currentLevel;

    protected Integer currentMistakes;

    protected Integer numberOfCardsCorrect;

    protected boolean cardSetComplete = false;

    protected boolean isLocked = false;

    protected Handler timerHandler;

    protected Integer currentTimelimit;

    protected Integer currentMistakesLimit;

    /**
     * These attributes contain the various cards and listeners
     * that are used within the current game
     */

    protected ArrayList<Card> playingCards;

    protected ArrayList<Card> currentSet;

    protected ArrayList<Card> failedSet;

    protected ArrayList<GameListener> listeners;

    /**
     * These methods set the initial values for each of the attributes
     */
    public Game() {
        this.cardsPerSet = Memory.DEFAULT_CARDS_PER_SET;
        this.maximumMistakes = Memory.DEFAULT_MAX_MISTAKES;
        this.timeLimit = Memory.DEFAULT_TIMELIMIT;

        this.numberOfCardsCorrect = 0;
        this.currentLevel = 1;
        this.currentMistakes = 0;
        this.startTime = System.currentTimeMillis() / 1000;

        this.setStartValues();

        this.startTimer();
    }

    protected void setStartValues() {
        this.playingCards = new ArrayList();
        this.currentSet = new ArrayList();
        this.failedSet = new ArrayList();
        this.listeners = new ArrayList();

        if (this.timerHandler != null) {
            this.timerHandler.removeCallbacksAndMessages(null);
        }
    }

    public void addGameListener(GameListener gameListener) {
        this.listeners.add(gameListener);
    }

    /**
     * These are the basic getter/setter methods for the Game instance
     * The setters should only be called from the Persistence context
     * or from the preferences to initiate the desired game state
     */

    public void setCardsPerSet(Integer cardsPerSet) {
        this.cardsPerSet = cardsPerSet;
    }

    public Integer getCardsPerSet() {
        return this.cardsPerSet;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Integer getCurrentLevel() {
        return this.currentLevel;
    }

    public void setCurrentMistakes(Integer currentMistakes) {
        this.currentMistakes = currentMistakes;
    }

    public Integer getCurrentMistakes() {
        return this.currentMistakes;
    }

    public void setCurrentSet(ArrayList<Card> currentSet) {
        this.currentSet = currentSet;
    }

    public ArrayList<Card> getCurrentSet() {
        return this.currentSet;
    }

    public void setCurrentTime(int currentTime) {
        this.startTime = (long) ((System.currentTimeMillis() / 1000) - currentTime);
    }

    public Integer getCurrentTime() {
        return (int) ((System.currentTimeMillis() / 1000) - this.startTime);
    }

    public void setFailedSet(ArrayList<Card> failedSet) {
        this.failedSet = failedSet;
    }

    public ArrayList<Card> getFailedSet() {
        return this.failedSet;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public void setMaximumMistakes(Integer maximumMistakes) {
        this.maximumMistakes = maximumMistakes;
    }

    public Integer getMaximumMistakes() {
        return this.maximumMistakes;
    }

    public void setCurrentMistakesLimit(Integer currentMistakesLimit) {
        this.currentMistakesLimit = currentMistakesLimit;
    }

    public Integer getCurrentMistakesLimit() {
        return this.currentMistakesLimit;
    }

    public void setNumberOfCardsCorrect(Integer numberOfCardsCorrect) {
        this.numberOfCardsCorrect = numberOfCardsCorrect;
    }

    public Integer getNumberOfCardsCorrect() {
        return this.numberOfCardsCorrect;
    }

    public void setPlayingCards(ArrayList<Card> playingCards) {
        this.playingCards = playingCards;
    }

    public ArrayList<Card> getPlayingCards() {
        if (this.playingCards.size() == 0) {
            this.loadPlayingCards();
            this.shuffleCards();
        }

        return this.playingCards;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getTimeLimit() {
        return this.timeLimit;
    }

    public void setCurrentTimeLimit(Integer currentTimelimit) {
        this.currentTimelimit = currentTimelimit;
    }

    public Integer getCurrentTimelimit() {
        return this.currentTimelimit;
    }

    /**
     * These method are responsible for creating a new
     * game with the necessary cards. The loadPlayingCards
     * method is abstract to force the child to implement
     * its rules to decide the cards that need to be in the game
     */
    public abstract void loadPlayingCards();

    protected void addCardToGame(Integer imageIndex) {
        for (int i = 0; i < this.cardsPerSet; i++) {
            Card card = new Card();
            card.setImageIndex(imageIndex);
            card.setVisible(false);
            this.playingCards.add(card);
        }
    }

    protected void shuffleCards() {
        long seed = System.nanoTime();
        Collections.shuffle(this.playingCards, new Random(seed));
    }

    protected void startTimer() {
        if (this.timerHandler == null) {
            this.timerHandler = new Handler();
        }

        this.timerHandler.removeCallbacksAndMessages(null);
        this.timerHandler.postDelayed(updateTimer, 1000);
    }

    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            for (GameListener listener : Game.this.listeners) {
                listener.gameStateChanged(Game.this);
            }

            Game.this.timerHandler.postDelayed(this, 1000);
        }
    };

    /**
     * These methods can be called to test the current state of the game
     * and handle the appropriate actions accordingly
     */
    public Boolean isLocked() {
        return this.isLocked;
    }

    public boolean hasTimeRemaining() {
        return true;
    }

    public boolean hasMistakesRemaining() {
        return true;
    }

    public boolean hasCardsRemaining() {
        if ((this.playingCards.size() - this.numberOfCardsCorrect) == 0) {
            // The game is complete, remove all listeners to make way for the GC
            this.setStartValues();
            return false;
        }

        return true;
    }

    public void endGame() {
        this.listeners = new ArrayList();
        this.timerHandler.removeCallbacks(updateTimer);
    }

    /**
     * These methods handle the actions that need
     * to be executed when the player selected a card
     */
    public void addPickedCard(Integer position) {
        Card card = this.playingCards.get(position);
        if (!this.cardSetComplete
            && !card.isDisabled()) {

            this.currentSet.add(card);
            card.setDisabled(true);
        }

        if (this.currentSet.size() == this.cardsPerSet) {
            this.cardSetComplete = true;
        }
    }

    public Integer verifyCardSet() {
        if (!this.cardSetComplete) {
            return Memory.GAME_SET_INCOMPLETE;
        }

        boolean setResult = this.getSetResult();

        if (!setResult) {
            this.handleMistake();
        } else {
            this.numberOfCardsCorrect += this.cardsPerSet;
        }

        this.currentSet = new ArrayList();
        this.cardSetComplete = false;

        return (setResult ? Memory.GAME_SET_VALID : Memory.GAME_SET_INVALID);
    }

    protected boolean getSetResult() {
        Integer cardIndex = null;
        boolean setResult = true;

        for (Card card : this.currentSet) {
            if (cardIndex == null) {
                cardIndex = card.getImageIndex();
            } else if (!card.getImageIndex().equals(cardIndex)) {
                setResult = false;
                break;
            }
        }

        return setResult;
    }

    protected void handleMistake() {
        this.failedSet = this.currentSet; // The current set will be emptied, so store this temporarily
        this.isLocked = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Game.this.hideCurrentSet();
            }
        }, 500); // If a mistake was made keep it visible for half a second

        this.currentMistakes++;
    }

    protected void hideCurrentSet() {
        this.isLocked = false;

        for (Card card : Game.this.failedSet) {
            card.setDisabled(false);
            card.setVisible(false);
        }
        this.failedSet = new ArrayList();
    }
}
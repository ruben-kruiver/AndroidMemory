package nl.mprog.apps.memory.interfaces;

import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import nl.mprog.apps.memory.models.Card;
import nl.mprog.apps.memory.models.Memory;

public abstract class Game {

    protected Integer currentLevel;

    protected Integer timeLimit;

    protected Integer maximumMistakes;

    protected Integer currentMistakes;

    protected Long startTime;

    protected ArrayList<Card> playingCards;

    protected ArrayList<Card> currentSequence;

    protected ArrayList<Card> failedSequence;

    protected Integer cardsPerSet;

    protected boolean cardSetComplete = false;

    protected boolean isLocked = false;

    protected Integer cardsVisible;

    protected Context context;

    public Game(Context context) {
        this.timeLimit = Memory.DEFAULT_TIMELIMIT;
        this.startTime = System.currentTimeMillis() / 1000;

        this.cardsPerSet = Memory.DEFAULT_CARDS_PER_SET;

        this.currentLevel = 1;
        this.maximumMistakes = Memory.DEFAULT_MAX_MISTAKES;
        this.currentMistakes = 0;
        this.cardsVisible = 0;

        this.currentSequence = new ArrayList();
        this.playingCards = new ArrayList();

        this.context = context;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setCardsPerSet(Integer cardsPerSet) {
        this.cardsPerSet = cardsPerSet;
    }

    public void setMaximumMistakes(Integer maximumMistakes) {
        this.maximumMistakes = maximumMistakes;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Integer getCurrentLevel() {
        return this.currentLevel;
    }

    public void setCurrentMistakes(Integer mistakes) {
        this.currentMistakes = mistakes;
    }

    public Integer getCurrentMistakes() {
        return this.currentMistakes;
    }

    public boolean isLocked() {
        return this.isLocked;
    }

    public ArrayList<Card> getCards() {
        if (this.playingCards.size() == 0) {
            this.loadPlayingCards();
            this.shuffleCards();
        }

        return this.playingCards;
    }

    public void addCardPicked(Integer position) {
        Card card = this.playingCards.get(position);
        if (!this.cardSetComplete
            && !card.isDisabled()) {

            this.currentSequence.add(card);
            card.setDisabled(true);
        }

        if (this.currentSequence.size() == this.cardsPerSet) {
            this.cardSetComplete = true;
        }
    }

    public Integer verifyCardSet() {
        if (!this.cardSetComplete) {
            return Memory.GAME_SET_INCOMPLETE;
        }

        Integer cardIndex = null;
        boolean sequenceOK = true;

        for (Card card : this.currentSequence) {
            if (cardIndex == null) {
                cardIndex = card.getImageIndex();
            } else if (!card.getImageIndex().equals(cardIndex)) {
                sequenceOK = false;
                break;
            }
        }

        if (!sequenceOK) {
            this.failedSequence = this.currentSequence;
            this.isLocked = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Game.this.hideCurrentSequence();
                }
            }, 2000);

            this.currentMistakes++;
        } else {
            this.cardsVisible += this.cardsPerSet;
        }

        this.currentSequence = new ArrayList();
        this.cardSetComplete = false;

        return (sequenceOK ? Memory.GAME_SET_VALID : Memory.GAME_SET_INVALID);
    }

    public abstract void loadPlayingCards();

    public void shuffleCards() {
        long seed = System.nanoTime();
        Collections.shuffle(this.playingCards, new Random(seed));
    }

    public boolean hasTimeRemaining(int currentTime) {
        return true;
    }

    public boolean hasMistakesRemaining() {
        return true;
    }

    public boolean hasCardsRemaining() {
        return (this.playingCards.size() - this.cardsVisible) > 0;
    }

    protected void hideCurrentSequence() {
        this.isLocked = false;

        for (Card card : Game.this.failedSequence) {
            card.setDisabled(false);
            card.setVisible(false);
        }
    }

    protected void addCardToSet(int imageIndex) {
        for (int i = 0; i < this.cardsPerSet; i++) {
            Card card = new Card();
            card.setImageIndex(imageIndex);
            card.setVisible(false);
            this.playingCards.add(card);
        }
    }
}
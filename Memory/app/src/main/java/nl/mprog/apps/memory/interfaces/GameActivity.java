package nl.mprog.apps.memory.interfaces;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import nl.mprog.apps.memory.R;
import nl.mprog.apps.memory.exceptions.InvalidThemeException;
import nl.mprog.apps.memory.models.Card;
import nl.mprog.apps.memory.models.Memory;
import nl.mprog.apps.memory.models.Theme;
import nl.mprog.apps.memory.views.CardLayout;
import nl.mprog.apps.memory.views.CardView;

public abstract class GameActivity extends PersistentActivity {

    protected Theme theme;

    protected Integer maxMistakes;

    protected Integer timeLimit;

    protected Integer cardsPerSet;

    protected Game game;

    protected CardLayout cardLayout;

    protected SharedPreferences preferences;

    protected Integer currentTime;

    protected ArrayList<Card> cards;

    private Handler timerHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (this.cardLayout == null) {
            this.cardLayout = (CardLayout) this.findViewById(R.id.gameactivity_root);
        }

        this.loadSettings();

        this.loadTheme();

        this.reloadGame();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.timerHandler.removeCallbacks(updateTimer);
        this.persistence.store();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new_game) {
            this.persistence.clear();
            this.reloadGame();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void nextLevel() {
        Integer currentLevel = this.game.getCurrentLevel();

        this.persistence.setCurrentMistakes(0);
        this.persistence.setCurrentLevel(++currentLevel);

        this.reloadGame();
    }

    public void reloadGame() {
        this.newGame();

        this.game.setCurrentMistakes(this.persistence.getCurrentMistakes());
        this.game.setCurrentLevel(this.persistence.getCurrentLevel());
        this.currentTime = this.persistence.getCurrentTime();

        this.clearGrid();

        this.setGamePreferences();

        this.setGridColumnCount();
        this.updateMistakesBar();

        this.startTimer();

        this.loadCards();

        this.displayCards();
    }

    protected void addCard(Card card, Integer position) {
        CardView cardView = new CardView(this);
        cardView.setCard(card, position);
        cardView.setOnClickListener(this.cardViewListener);
        cardView.setTheme(this.theme);
        card.setCardView(cardView);

        this.cardLayout.addView(cardView);
    }

    protected void clearGrid() {
        this.cardLayout.removeAllViews();
    }

    protected void setGridColumnCount() {
        ArrayList<Card> cards = this.game.getCards();
        Integer cardsPerRow;

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            cardsPerRow = Memory.MAX_CARDS_PER_ROW_LANDSCAPE;
        } else {
            cardsPerRow = Memory.MAX_CARDS_PER_ROW_PORTRAIT;
        }

        while (cardsPerRow > 2) {
            if (cards.size() % cardsPerRow == 0
                    && (cards.size() / cardsPerRow >= Memory.MIN_ROWS_ON_GRID)) {
                break;
            }

            cardsPerRow--;
        }

        this.cardLayout.setColumnCount(cardsPerRow);
        this.cardLayout.invalidate();
        this.cardLayout.requestLayout();
    }

    protected void displayCards() {
        Integer position = 0;
        for (Card card : this.cards) {
            this.addCard(card, position);
            position++;
        }
    }

    protected void loadSettings() {
        if (this.preferences == null) {
            this.preferences = this.getSharedPreferences(Memory.PREFERENCES, MODE_PRIVATE);
        }

        this.maxMistakes = this.preferences.getInt(Memory.PREFERENCES_MAX_MISTAKES, Memory.DEFAULT_MAX_MISTAKES);
        this.timeLimit = this.preferences.getInt(Memory.PREFERENCES_TIMELIMIT, Memory.DEFAULT_TIMELIMIT);
        this.cardsPerSet = this.preferences.getInt(Memory.PREFERENCES_CARDS_PER_SET, Memory.DEFAULT_CARDS_PER_SET);
    }

    protected void loadTheme() {
        String themeName = this.preferences.getString(Memory.PREFERENCES_THEME, Memory.DEFAULT_THEME);
        try {
            this.theme = new Theme(this, themeName);
            this.cardLayout.setTheme(this.theme);
        } catch (InvalidThemeException ignored) {}
    }

    protected abstract void newGame();

    protected View.OnClickListener cardViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (GameActivity.this.game.isLocked()) { return; }

            CardView cardView = (CardView) v;
            cardView.flipCard();

            GameActivity.this.game.addCardPicked(cardView.getPositionIndex());

            Integer cardResult = GameActivity.this.game.verifyCardSet();

            GameActivity.this.persistence.setCurrentMistakes(GameActivity.this.game.getCurrentMistakes());

            if (cardResult.equals(Memory.GAME_SET_INVALID)) {
                if (!GameActivity.this.game.hasMistakesRemaining()) {
                    GameActivity.this.endGame();
                }

                GameActivity.this.updateMistakesBar();
            } else if (!GameActivity.this.game.hasCardsRemaining()) {
                Toast.makeText(GameActivity.this, R.string.game_success, Toast.LENGTH_LONG).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GameActivity.this.nextLevel();
                    }
                }, 1000);
            }
        }
    };

    protected void setGamePreferences() {
        this.game.setCardsPerSet(this.preferences.getInt(Memory.PREFERENCES_CARDS_PER_SET, Memory.DEFAULT_CARDS_PER_SET));
        this.game.setTimeLimit(this.preferences.getInt(Memory.PREFERENCES_TIMELIMIT, Memory.DEFAULT_TIMELIMIT));
        this.game.setMaximumMistakes(this.preferences.getInt(Memory.PREFERENCES_MAX_MISTAKES, Memory.DEFAULT_MAX_MISTAKES));
    }

    protected void endGame() {
        for (Card card : this.game.getCards()) {
            card.setDisabled(true);
        }

        Toast toast = Toast.makeText(this, R.string.game_over, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    protected void startTimer() {
        if (this.timerHandler == null) {
            this.timerHandler = new Handler();
        }

        this.updateCurrentTimeBar();
        this.timerHandler.removeCallbacks(updateTimer);
        this.timerHandler.postDelayed(updateTimer, 1000);
    }

    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            GameActivity.this.currentTime++;
            GameActivity.this.persistence.setCurrentTime(GameActivity.this.currentTime);
            GameActivity.this.updateCurrentTimeBar();               // Update the game timer
            GameActivity.this.timerHandler.postDelayed(this, 1000);
        }
    };

    protected void updateCurrentTimeBar() {
        TextView mistakesCounter = (TextView) this.findViewById(R.id.activity_timelimit_counter);
        String displayMessage = this.getResources().getString(R.string.header_time_progress);

        mistakesCounter.setText(displayMessage + " " + this.currentTime);
    }

    protected void updateMistakesBar() {
        TextView mistakesCounter = (TextView) this.findViewById(R.id.activity_mistakes_counter);
        String displayMessage = this.getResources().getString(R.string.header_mistakes_progress);

        mistakesCounter.setText(displayMessage +  " " + this.game.getCurrentMistakes());
    }

    protected void loadCards() {
        this.cards = this.persistence.getCards();

        if (this.cards.size() == 0) {
            this.cards = this.game.getCards();
            this.persistence.setCards(this.cards);
        }
    }
}
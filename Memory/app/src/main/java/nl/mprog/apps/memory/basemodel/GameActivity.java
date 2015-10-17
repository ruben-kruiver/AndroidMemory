package nl.mprog.apps.memory.basemodel;

import android.app.Activity;
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

import java.io.IOException;
import java.util.ArrayList;

import nl.mprog.apps.memory.R;
import nl.mprog.apps.memory.exception.InvalidThemeException;
import nl.mprog.apps.memory.model.Card;
import nl.mprog.apps.memory.model.Memory;
import nl.mprog.apps.memory.model.Persistence;
import nl.mprog.apps.memory.model.Theme;
import nl.mprog.apps.memory.view.CardLayout;
import nl.mprog.apps.memory.view.CardView;

public abstract class GameActivity extends Activity implements GameListener {

    protected Theme theme;

    protected Game game;

    protected CardLayout cardLayout;

    protected SharedPreferences preferences;

    protected Persistence persistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_game);

        if (this.cardLayout == null) {
            this.cardLayout = (CardLayout) this.findViewById(R.id.gameactivity_root);
        }

        this.loadPersistence();

        this.loadPreferences();

        this.loadTheme();

        this.loadGame(1);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            this.persistence.storeGame(this.game);
            this.game.endGame();
        } catch (IOException ignore) {
            this.displayMessage(R.string.error_persistence_failed);
        }
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
            this.persistence.clearPersistence();
            this.game.endGame();
            this.game = null;
            this.loadGame(1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called by the observable class where this activity is registered as listener
     */
    @Override
    public void gameStateChanged(Game game) {
        this.updateCurrentTimeBar();
        this.validateCurrentGameState(game);
    }

    protected void loadPersistence() {
        if (this.persistence == null) {
            this.persistence = new Persistence(this.getPersistenceFilename());
        }
    }

    /**
     * This method will return the filename for the persistence file. It
     * needs to be implemented by each child class so that each of those
     * games can have its own persistent state
     */
    protected abstract String getPersistenceFilename();

    protected void loadPreferences() {
        if (this.preferences == null) {
            this.preferences = this.getSharedPreferences(Memory.PREFERENCES, MODE_PRIVATE);
        }
    }

    protected void loadTheme() {
        String themeName = this.preferences.getString(Memory.PREFERENCES_THEME, Memory.DEFAULT_THEME);
        try {
            this.theme = new Theme(this, themeName);
            this.cardLayout.setTheme(this.theme);
        } catch (InvalidThemeException | IOException ignored) {
            this.displayMessage(R.string.error_invalid_theme);
        }
    }

    protected void displayMessage(Integer message_id) {
        Toast toast = Toast.makeText(this, message_id, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    protected void loadGame(Integer level) {
        try {
            this.game = this.persistence.restoreGame();
        } catch (Exception ignored) {} // Ignore the restore, an error is logged and a new game will be created

        if (this.game == null) {
            this.game = this.newGame();
            this.game.setCurrentLevel(level);
        }

        this.persistence.clearPersistence(); // Prevent the same game from being reloaded twice

        this.game.addGameListener(this);

        this.clearGrid();

        this.setGamePreferences();

        this.setGridColumnCount();
        this.updateMistakesBar();
        this.updateCurrentTimeBar();

        this.displayCards();
    }

    /**
     * This method will be implemented by the child classess to be able to load
     * the required Game type
     */
    protected abstract Game newGame();

    protected void clearGrid() {
        this.cardLayout.removeAllViews();
    }

    protected void setGamePreferences() {
        this.game.setCardsPerSet(this.preferences.getInt(Memory.PREFERENCES_CARDS_PER_SET, Memory.DEFAULT_CARDS_PER_SET));
        this.game.setTimeLimit(this.preferences.getInt(Memory.PREFERENCES_TIMELIMIT, Memory.DEFAULT_TIMELIMIT));
        this.game.setMaximumMistakes(this.preferences.getInt(Memory.PREFERENCES_MAX_MISTAKES, Memory.DEFAULT_MAX_MISTAKES));
    }

    protected void setGridColumnCount() {
        Integer cardsPerRow = this.calculateCardsPerRow();

        this.cardLayout.setColumnCount(cardsPerRow);
        this.cardLayout.invalidate();
        this.cardLayout.requestLayout();
    }

    /**
     * This method will calculate the number of cards that will be displayed on each row
     * It's formula calculates the closest value that will get an equal number of cards next
     * to each other, while maintaining the maximum and minimum values for the columns and rows
     */
    protected Integer calculateCardsPerRow() {
        ArrayList<Card> cards = this.game.getPlayingCards();
        Integer cardsPerRow;

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            cardsPerRow = Memory.MAX_CARDS_PER_ROW_LANDSCAPE;
        } else {
            cardsPerRow = Memory.MAX_CARDS_PER_ROW_PORTRAIT;
        }

        while (cardsPerRow > 2) {
            // Check that the number of cards per row is equal for each row and the
            // minimum number of rows is reached
            if (cards.size() % cardsPerRow == 0
                    && (cards.size() / cardsPerRow >= Memory.MIN_ROWS_ON_GRID)) {
                break;
            }

            cardsPerRow--;
        }
        return cardsPerRow;
    }

    protected void updateCurrentTimeBar() {
        TextView mistakesCounter = (TextView) this.findViewById(R.id.activity_timelimit_counter);
        String displayMessage = this.getResources().getString(R.string.header_time_progress);

        mistakesCounter.setText(displayMessage + " " + this.game.getCurrentTime());
    }

    protected void updateMistakesBar() {
        TextView mistakesCounter = (TextView) this.findViewById(R.id.activity_mistakes_counter);
        String displayMessage = this.getResources().getString(R.string.header_mistakes_progress);

        mistakesCounter.setText(displayMessage + " " + this.game.getCurrentMistakes());
    }

    protected void displayCards() {
        Integer position = 0;
        for (Card card : this.game.getPlayingCards()) {
            this.addCardToGrid(card, position);
            position++;
        }
    }

    protected void addCardToGrid(Card card, Integer position) {
        CardView cardView = new CardView(this);
        cardView.setCard(card, position);
        cardView.setOnClickListener(this.cardViewListener);
        cardView.setTheme(this.theme);
        card.setCardView(cardView);

        this.cardLayout.addView(cardView);
    }

    protected View.OnClickListener cardViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (GameActivity.this.game.isLocked()) { return; }

            CardView cardView = (CardView) v;
            cardView.flipCard();

            GameActivity.this.game.addPickedCard(cardView.getPositionIndex());

            Integer cardResult = GameActivity.this.game.verifyCardSet();
            GameActivity.this.updateMistakesBar();

            if (cardResult.equals(Memory.GAME_SET_INVALID)) {
                GameActivity.this.validateCurrentGameState(GameActivity.this.game);
            }
        }
    };

    protected void endGame(Integer message_id) {
        this.game.endGame();

    }

    protected void disableCards() {
        for (Card card : this.game.getPlayingCards()) {
            card.setDisabled(true);
        }
    }

    // If the current game
    protected void validateCurrentGameState(Game game) {
        Runnable action = null;
        if (!game.hasCardsRemaining()) {
            this.displayMessage(R.string.game_success);
            action = this.gameComplete;
        } else if (!game.hasTimeRemaining() || !game.hasMistakesRemaining()) {
            this.displayMessage(R.string.game_failed);
            action = this.gameFailed;
        }

        if (action != null) {
            this.disableCards();
            Handler handler = new Handler();
            handler.postDelayed(action, 500);
        }
    }

    /**
     * The delayed action when a game is completed successfully and the next level may start
     */
    protected Runnable gameComplete = new Runnable() {
        @Override
        public void run() {
            Integer currentLevel = GameActivity.this.game.getCurrentLevel();
            GameActivity.this.game.endGame();
            GameActivity.this.game = null;
            GameActivity.this.loadGame(++currentLevel); // Start next level game
        }
    };

    /**
     * The delayed action when the time has exceeded and the game needs to end
     */
    protected Runnable gameFailed = new Runnable() {
        @Override
        public void run() {
            Integer currentLevel = GameActivity.this.game.getCurrentLevel();
            GameActivity.this.game.endGame();
            GameActivity.this.game = null;
            GameActivity.this.loadGame(currentLevel);
        }
    };
}
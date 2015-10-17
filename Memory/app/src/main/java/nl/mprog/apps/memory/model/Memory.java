package nl.mprog.apps.memory.model;

import android.graphics.Color;

public class Memory {
    /**
     * These constants contain the default values used
     * in the game
     */
    public static final Integer DEFAULT_IMAGE_WIDTH = 50;
    public static final Integer DEFAULT_IMAGE_HEIGHT = 67;
    public static final Integer DEFAULT_CARD_MARGIN = 5;
    public static final Integer DEFAULT_CARD_COLOR = Color.LTGRAY;
    public static final Integer DEFAULT_CARDS_PER_SET = 2;
    public static final Integer DEFAULT_TIMELIMIT = 120;
    public static final Integer DEFAULT_MAX_MISTAKES = 3;
    public static final String  DEFAULT_THEME = "forrest";

    /**
     * These constants contain the minimum and maximum values
     * used in the game
     */
    public static final Integer MIN_VALUE_CARDS_PER_SET = 2;
    public static final Integer MAX_VALUE_CARDS_PER_SET = 4;
    public static final Integer MIN_VALUE_TIME_LIMIT = 30;
    public static final Integer MAX_VALUE_TIME_LIMIT = 120;
    public static final Integer MIN_VALUE_MISTAKES = 5;
    public static final Integer MAX_VALUE_MISTAKES = 25;
    public static final Integer MAX_CARDS_PER_ROW_PORTRAIT = 4;
    public static final Integer MAX_CARDS_PER_ROW_LANDSCAPE = 5;
    public static final Integer MIN_ROWS_ON_GRID = 2;

    /**
     * These constant contain the values used to send to
     * the intent from the MainMenu Activity
     */
    public static final Integer SETTINGS_UPDATED = 1;
    public static final Integer PRACTICE_ENDED = 2;
    public static final Integer CHALLENGE_ENDED = 3;

    /**
     * These constants contain the names of the files and
     * folders used for the persistence of the game
     */
    public static final String PERSISTENCE_FILENAME_CHALLENGE = "challenge.dat";
    public static final String PERSISTENCE_FILENAME_PRACTICE = "practice.dat";
    public static final String PERSISTENCE_FOLDER = "persistence";

    public static final String THEMES_RESOURCE_FOLDER = "themes";

    /**
     * These constants contain the names of the preference keys
     */
    public static final String PREFERENCES = "MemorySettings";
    public static final String PREFERENCES_CARDS_PER_SET = "CardsPerSet";
    public static final String PREFERENCES_THEME = "Theme";
    public static final String PREFERENCES_MAX_MISTAKES = "MaxMistakes";
    public static final String PREFERENCES_TIMELIMIT = "Timelimit";

    /**
     * These constant contain the values of the result
     * of the selection of a card by the player
     */
    public static final Integer GAME_SET_INCOMPLETE = -1;
    public static final Integer GAME_SET_VALID = 1;
    public static final Integer GAME_SET_INVALID = 0;
}

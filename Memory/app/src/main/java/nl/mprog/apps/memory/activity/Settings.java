package nl.mprog.apps.memory.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;

import nl.mprog.apps.memory.R;
import nl.mprog.apps.memory.model.Memory;

public class Settings extends Activity {

    /**
     * These attributes will be stored in the shared preferences
     */
    protected Integer cardsPerSet;

    protected Integer maximumMistakes;

    protected Integer timeLimit;

    protected String theme;

    protected SharedPreferences sharedPreferences;

    /**
     * These attributes will define if the preferences
     * are loaded and stored, to prevent double loading and storing
     * without any changes
     */
    protected boolean preferencesLoaded = false;

    protected boolean preferencesStored = true;

    /**
     * These attributes will hold the controls on the screen.
     * The initialSpinnerDisplay is used to make sure the
     * first call to the spinner value will be ignored on load
     */
    protected boolean initialSpinnerDisplay = true;

    protected SeekBar sbCardsPerSet;
    protected SeekBar sbMaxMistakes;
    protected SeekBar sbTimelimit;
    protected Spinner spinnerTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (!this.preferencesLoaded) {
            this.loadPreferences();
        }

        this.loadControls();
        this.setDefaultValues();
        this.initControlListeners();
    }

    @Override
    public void onPause() {
        super.onPause();

        this.storePreferences();
    }

    protected void loadPreferences() {
        this.sharedPreferences = this.getSharedPreferences(Memory.PREFERENCES, this.MODE_PRIVATE);

        this.cardsPerSet = this.sharedPreferences.getInt(Memory.PREFERENCES_CARDS_PER_SET, Memory.DEFAULT_CARDS_PER_SET);
        this.maximumMistakes = this.sharedPreferences.getInt(Memory.PREFERENCES_MAX_MISTAKES, Memory.DEFAULT_MAX_MISTAKES);
        this.timeLimit = this.sharedPreferences.getInt(Memory.PREFERENCES_TIMELIMIT, Memory.DEFAULT_TIMELIMIT);
        this.theme = this.sharedPreferences.getString(Memory.PREFERENCES_THEME, Memory.DEFAULT_THEME);

        this.preferencesLoaded = true;
    }

    protected void storePreferences() {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();

        editor.putInt(Memory.PREFERENCES_CARDS_PER_SET, this.cardsPerSet);
        editor.putInt(Memory.PREFERENCES_MAX_MISTAKES, this.maximumMistakes);
        editor.putInt(Memory.PREFERENCES_TIMELIMIT, this.timeLimit);
        editor.putString(Memory.PREFERENCES_THEME, this.theme);

        editor.commit();

        this.preferencesStored = true;
    }

    protected void loadControls() {
        this.sbMaxMistakes = (SeekBar) this.findViewById(R.id.settingsMaxMistakes);
        this.sbMaxMistakes.setMax(Memory.MAX_VALUE_MISTAKES);

        this.sbTimelimit = (SeekBar) this.findViewById(R.id.settingsTimelimit);
        this.sbTimelimit.setMax(Memory.MAX_VALUE_TIME_LIMIT);

        this.sbCardsPerSet = (SeekBar) this.findViewById(R.id.settingsCardsPerSet);
        this.sbCardsPerSet.setMax(Memory.MAX_VALUE_CARDS_PER_SET);

        this.spinnerTheme = (Spinner) this.findViewById(R.id.settingsTheme);
    }

    protected void setDefaultValues() {
        this.sbMaxMistakes.setProgress(this.maximumMistakes);
        this.sbCardsPerSet.setProgress(this.cardsPerSet);
        this.sbTimelimit.setProgress(this.timeLimit);
        this.initThemeSpinner(spinnerTheme);
    }

    protected void initThemeSpinner(Spinner spinnerTimelimit) {
        ArrayList<String> spinnerArray =  this.getThemes();

        ArrayAdapter<String> adapter = new ArrayAdapter(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimelimit.setAdapter(adapter);

        int initialPosition = adapter.getPosition(this.theme);
        this.spinnerTheme.setSelection(initialPosition);
    }

    protected ArrayList<String> getThemes() {
        ArrayList<String> themes = new ArrayList();

        try {
            Resources res = this.getResources();
            String[] themeFolders = res.getAssets().list(Memory.THEMES_RESOURCE_FOLDER);

            if (themeFolders.length > 0) {
                for (String theme : themeFolders) {
                    themes.add(theme);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return themes;
    }

    protected void initControlListeners() {
        this.sbCardsPerSet.setOnSeekBarChangeListener(this.sbCardsPerSetListener);
        this.sbMaxMistakes.setOnSeekBarChangeListener(this.sbMaxMistakesListener);
        this.sbTimelimit.setOnSeekBarChangeListener(this.sbTimelimitListener);
        this.spinnerTheme.setOnItemSelectedListener(this.spinnerThemeListener);
    }

    protected SeekBar.OnSeekBarChangeListener sbCardsPerSetListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress < Memory.MIN_VALUE_CARDS_PER_SET) {
                progress = Memory.MIN_VALUE_CARDS_PER_SET;
                seekBar.setProgress(progress);
            }

            Settings.this.preferencesStored = false;
            Settings.this.cardsPerSet = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    protected SeekBar.OnSeekBarChangeListener sbMaxMistakesListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress < Memory.MIN_VALUE_MISTAKES) {
                progress = Memory.MIN_VALUE_MISTAKES;
                seekBar.setProgress(progress);
            }

            Settings.this.preferencesStored = false;
            Settings.this.maximumMistakes = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    protected SeekBar.OnSeekBarChangeListener sbTimelimitListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress < Memory.MIN_VALUE_TIME_LIMIT) {
                progress = Memory.MIN_VALUE_TIME_LIMIT;
                seekBar.setProgress(progress);
            }

            Settings.this.preferencesStored = false;
            Settings.this.timeLimit = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    protected AdapterView.OnItemSelectedListener spinnerThemeListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (Settings.this.initialSpinnerDisplay) {          // On initialization itemselected is
                Settings.this.initialSpinnerDisplay = false;    // called once. Ignore this selection
                return;
            }

            Settings.this.preferencesStored = false;
            Settings.this.theme = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };
}

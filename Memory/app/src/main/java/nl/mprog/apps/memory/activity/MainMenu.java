package nl.mprog.apps.memory.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import nl.mprog.apps.memory.R;
import nl.mprog.apps.memory.model.Memory;

public class MainMenu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        this.initButtonListeners();
    }

    /**
     * This method initializes the listeners for the buttons
     * on the view to make sure the correct actions are beïng called
     * without having to define these on the View itself
     */
    protected void initButtonListeners() {
        Button practice = (Button) this.findViewById(R.id.action_practice);
        practice.setOnClickListener(this.practiceListener);

        Button challenge = (Button) this.findViewById(R.id.action_challenge);
        challenge.setOnClickListener(this.challengeListener);

        Button settings = (Button) this.findViewById(R.id.action_settings);
        settings.setOnClickListener(this.settingsListener);
    }

    protected View.OnClickListener challengeListener = new View.OnClickListener(){
        public void onClick(View view) {
            Intent intent = new Intent(MainMenu.this, Challenge.class);
            MainMenu.this.startActivityForResult(intent, Memory.CHALLENGE_ENDED);
        }
    };

    protected View.OnClickListener practiceListener = new View.OnClickListener(){
        public void onClick(View view) {
            Intent intent = new Intent(MainMenu.this, Practice.class);
            MainMenu.this.startActivityForResult(intent, Memory.PRACTICE_ENDED);
        }
    };

    protected View.OnClickListener settingsListener = new View.OnClickListener(){
        public void onClick(View view) {
            Intent intent = new Intent(MainMenu.this, Settings.class);
            MainMenu.this.startActivityForResult(intent, Memory.SETTINGS_UPDATED);
        }
    };
}

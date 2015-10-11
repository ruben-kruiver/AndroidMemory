package nl.mprog.apps.memory.interfaces;

import android.app.Activity;
import android.os.Bundle;

import nl.mprog.apps.memory.models.Persistence;

public abstract class PersistentActivity extends Activity {

    protected Persistence persistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.persistence == null) {
            this.persistence = new Persistence(this, this.getPersistenceFilename());
        }

        if (!this.persistence.isLoaded()) {
            this.persistence.load();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        this.persistence.store();
    }

    protected abstract String getPersistenceFilename();
}

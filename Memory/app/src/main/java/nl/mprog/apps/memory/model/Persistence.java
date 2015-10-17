package nl.mprog.apps.memory.model;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import nl.mprog.apps.memory.basemodel.Game;
import nl.mprog.apps.memory.model.persistence.Keeper;
import nl.mprog.apps.memory.model.persistence.Loader;

public class Persistence {

    protected String filename;

    protected boolean isLoaded;

    protected Game game;

    protected String rootNodeName;

    public Persistence(String filename) {
        this.filename = filename;
        this.isLoaded = false;
        this.game = null;
        this.rootNodeName = "persistence";
    }

    public void clearPersistence() {
        this.game = null;
    }

    public Game restoreGame() throws Exception {
        File persistenceFile = this.loadPersistenceFile();

        if (persistenceFile.exists() && !this.isLoaded) {
            try {
                Loader loader = new Loader(persistenceFile);
                this.game = loader.reloadGame();
                this.isLoaded = true;
            } catch (Exception ex) {
                Log.e("error", "Could not load game from persistence. " + ex.getMessage());
                throw ex;
            }
        }

        return this.game;
    }

    public void storeGame(Game game) throws IOException {
        try {
            Keeper keeper = new Keeper(this.loadPersistenceFile(), game);
            keeper.storeGame();
        } catch (IOException ex) {
            Log.e("error", "Could not save game to persistence. " + ex.getMessage());
            throw ex;
        }
    }

    protected File loadPersistenceFile() throws IOException {
        String basepath = Environment.getExternalStorageDirectory().toString();
        File folder = new File(basepath, Memory.PERSISTENCE_FOLDER);

        if (!folder.exists()) {
            folder.mkdirs();

            if (!folder.exists()) {
                throw new IOException("The folder could not be created.");
            }
        }

        return new File(basepath + File.separator + Memory.PERSISTENCE_FOLDER, this.filename);
    }
}

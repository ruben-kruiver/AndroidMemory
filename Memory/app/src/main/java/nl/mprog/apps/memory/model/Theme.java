package nl.mprog.apps.memory.model;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import nl.mprog.apps.memory.exception.InvalidThemeException;

public class Theme {

    protected String themeFolder;

    protected String externalStorageFolder;

    protected ArrayList<File> imagePaths;

    protected File backSideImage;

    protected File backgroundImage;

    protected Context context;

    public Theme(Context context, String themeFolder) throws InvalidThemeException, IOException {
        this.context = context;
        this.themeFolder = themeFolder;
        this.externalStorageFolder = Environment.getExternalStorageDirectory().toString()
                                        + File.separator + themeFolder;

        this.load();
        this.validateTheme();
    }

    public File getBackgroundImage() {
        return this.backgroundImage;
    }

    public File getBackSideImage    () {
        return this.backSideImage;
    }

    public File getFrontSideImageFor(Integer index) {
        return this.imagePaths.get(index);
    }

    /**
     * These methods load the images from the themes to enable them
     * to be displayed by the different views in the game
     */

    protected void load() throws InvalidThemeException, IOException {
        if (!this.validateExternalStorage()) {
            this.moveThemeToExternalStorage();
        }

        File folder = new File(Environment.getExternalStorageDirectory().toString(), Memory.THEMES_RESOURCE_FOLDER + File.separator + this.themeFolder);
        if (!folder.exists()) {
            throw new InvalidThemeException();
        }

        File[] listOfFiles = folder.listFiles();
        this.imagePaths = new ArrayList();

        for (File file : listOfFiles) {
            if (!this.isImage(file)) { continue; }

            switch (file.getName()) {
                case "background.png": this.backgroundImage = file; break;
                case "backside.png": this.backSideImage = file; break;
                default:
                    this.imagePaths.add(file);
            }
        }
    }

    protected boolean isImage(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        return options.outWidth != -1 && options.outHeight != -1;
    }

    protected void validateTheme() throws InvalidThemeException {
        if (this.backSideImage == null
                || this.backgroundImage == null
                || this.imagePaths.size() < 10) {
            throw new InvalidThemeException();
        }
    }

    /**
     * These methods are designed to move and validate the themes
     * to the external storage to be able to load the throughout the game.
     * This is done because the models otherwise can't get access to these files
     * and the Theme won't be able to be displayed
     */

    protected boolean validateExternalStorage() {
        File folder = new File(Environment.getExternalStorageDirectory().toString(), Memory.THEMES_RESOURCE_FOLDER + File.separator + this.themeFolder);

        if (!this.validateFolder(folder)) {
            Log.e("Error" , "Could not create folder");
            return false;
        }

        String[] files = folder.list();

        boolean backgroundAvailable = false;
        boolean backsideAvailable = false;
        boolean cardsAvailable = false;

        for (String filename : files) {
            switch (filename) {
                case "background.png": backgroundAvailable = true; break;
                case "backside.png": backsideAvailable = true; break;
                default : cardsAvailable = true; break;
            }
        }

        return backgroundAvailable && backsideAvailable && cardsAvailable;
    }

    protected boolean validateFolder(File folder) {
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder.exists() && folder.list().length > 0;
    }

    protected void moveThemeToExternalStorage() throws IOException {
        String themePath = Memory.THEMES_RESOURCE_FOLDER + File.separator + this.themeFolder;
        String[] content = this.context.getAssets().list(themePath);

        if (content.length > 0) {
            for (String filename : content) {
                this.moveFileToExternalStorage(themePath + File.separator + filename);
            }
        }
    }

    protected void moveFileToExternalStorage(String filename) throws IOException {
        String basepath = Environment.getExternalStorageDirectory().toString();

        File file = new File(basepath, filename);
        InputStream is = this.context.getAssets().open(filename);
        FileOutputStream os = new FileOutputStream(file);

        byte[] buffer = new byte[65536 * 2];
        int read;
        while ((read = is.read(buffer)) != -1) {
            os.write(buffer, 0, read);
        }

        is.close();
        os.flush();
        os.close();
    }
}

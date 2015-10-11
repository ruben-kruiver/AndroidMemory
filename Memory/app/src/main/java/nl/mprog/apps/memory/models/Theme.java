package nl.mprog.apps.memory.models;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import nl.mprog.apps.memory.exceptions.InvalidThemeException;

public class Theme {

    protected String themeFolder;

    protected String externalStorageFolder;

    protected ArrayList<File> imagePaths;

    protected File backSideImage;

    protected File backgroundImage;

    protected Context context;

    public Theme(Context context, String themeFolder) throws InvalidThemeException {
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

    public File getFrontSideFor(Integer index) {
        return this.imagePaths.get(index);
    }

    protected boolean isImage(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        return options.outWidth != -1 && options.outHeight != -1;
    }

    protected void load() throws InvalidThemeException {
        if (!this.validateExternalStorage()) {
            this.moveToExternalStorage();
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

    /**
     * This method validates the contents of the theme folder.
     */
    protected void validateTheme() throws InvalidThemeException {
        if (this.backSideImage == null
                || this.backgroundImage == null
                || this.imagePaths.size() < 10) {
            throw new InvalidThemeException();
        }
    }

    protected void moveToExternalStorage() {
        try {
            String themePath = Memory.THEMES_RESOURCE_FOLDER + File.separator + this.themeFolder;
            String[] content = this.context.getAssets().list(themePath);

            if (content.length > 0) {
                for (String filename : content) {
                    this.moveFileToExternalStorage(themePath + File.separator + filename);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void moveFileToExternalStorage(String filename) {
        String basepath = Environment.getExternalStorageDirectory().toString();

        try {
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
        } catch (Exception e) {
            Log.e("Error", e.getLocalizedMessage());
        }
    }

    protected boolean validateExternalStorage() {
        File folder = new File(Environment.getExternalStorageDirectory().toString(), Memory.THEMES_RESOURCE_FOLDER + File.separator + this.themeFolder);

        if (!folder.exists()) {
            folder.mkdirs();

            if (!folder.exists()) {
                Log.e("Error", "Could not create folder");
            }

            return false;
        }

        String[] files = folder.list();

        if (files.length == 0) { return false; }

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
}

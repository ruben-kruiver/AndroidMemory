package nl.mprog.apps.memory.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class Theme {

    protected String themeFolder;

    protected HashMap<Integer, File> imagePaths;

    protected String backSideImage;

    protected String backgroundImage;

    public Theme(String themeFolder) {
        this.themeFolder = themeFolder;
    }

    protected void load() {
        File folder = new File(this.themeFolder);
        File[] listOfFiles = folder.listFiles();

        ArrayList<File> files = new ArrayList();

        for (File file : listOfFiles) {
            files.add(file);
        }
    }


    protected String getMD5Checksum(String filepath) {
        String hash = "";

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            FileInputStream is = new FileInputStream(filepath);
            DigestInputStream dis = new DigestInputStream(is, md);

            byte[] dataBytes = new byte[1024];

            int nread = 0;

            while ((nread = dis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            };

            byte[] digest = md.digest();

            hash = new String(digest);
        } catch (IOException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        return hash;
    }
}

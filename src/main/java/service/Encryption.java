package service;

import controller.fxCtrls.MainCtrl;
import javafx.scene.control.ProgressBar;
import service.base.BaseEncryption;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class Encryption extends BaseEncryption {

    private final Log log = new Log();
    private final MainCtrl mainCtrl = Main.mainLoader.getController();
    private final ProgressBar progressBar = mainCtrl.getProgressBar();
    private int BUFFER = 153600;
    private int limit = Main.defaultBuffer;

    @Override
    protected void start(List<File> files, byte[] key) throws IOException {
        RandomAccessFile inFile;
        for (File file : files) {
            log.info("File path: " + file.getPath());
            if (!file.exists() || file.length() == 0) {
                log.warn("File not found or empty, continue!");
                continue;
            }

            inFile = new RandomAccessFile(file, "rw");

            byte[] readBytes = new byte[BUFFER];
            int counter = 0;
            int offset = 0;

            long fileSize = file.length();
            double done = 0;

            progressBar.setVisible(true);

            while(true) {
                int actuallyRead = inFile.read(readBytes, 0, limit <= 0 ? BUFFER : (int)Math.min(limit-done,BUFFER));
                offset = ownEncrypt(readBytes, key, offset);
                inFile.seek((long) counter * BUFFER);
                inFile.write(readBytes,0, actuallyRead);
                if (actuallyRead < BUFFER) break;
                done += actuallyRead;
                counter++;
                progressBar.setProgress(done/fileSize);
            }

            progressBar.setVisible(false);
            inFile.close();
        }
    }

    private static int ownEncrypt(byte[] text, byte[] key, int off) {
        int[] keyHash = createSimpleHash(key);

        int nextKey = Math.max(off, 0);
        int tmpChar;

        for (int i = 0; i < text.length; i++) {
            tmpChar = text[i];
            text[i] = (byte) ((text[i] ^ key[nextKey]) ^ keyHash[i % key.length]);
            nextKey = Math.abs(tmpChar * text[i]) % key.length;
        }

        return nextKey;
    }

    private static int[] createSimpleHash(byte[] key) {
        int[] chars2 = new int[key.length];
        int[] hash = new int[key.length];
        for (int i = 0; i < key.length; i++) {
            chars2[i] = key[key.length-(i+1)] ^ i;
            hash[i] = (char)chars2[i];
            for (int n = 0; n < chars2.length; n++) {
                hash[i] = hash[i] ^ key[n];
            }
        }
        return hash;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit < BUFFER && limit > 0) {
            BUFFER = limit;
        }
        this.limit = limit;
    }
}
package service.base;

import service.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class BaseEncryption {
    private static final Log log = new Log();

    abstract protected void start(List<File> files, String rawKey) throws IOException;
    abstract protected void start(List<File> files, File keyFile) throws IOException;

    public void encrypt(List<File> files, String rawKey) {
        Thread th = new Thread(() -> {

            double done;
            log.info("Encryption started");
            long start = System.currentTimeMillis();

            try {
                start(files, rawKey);
            } catch (IOException e) {
                log.error(e.getMessage());
            }

            done = (System.currentTimeMillis() - start) / 1000.0;
            log.info("Done in " + done + "s");
        });

        th.setDaemon(false);
        th.start();
    }

    public void encrypt(List<File> files, File keyFile) {
        Thread th = new Thread(() -> {

            long start;
            double done;

            log.info("Encryption started");
            start = System.currentTimeMillis();

            try {
                start(files, keyFile);
            } catch (IOException e) {
                log.error(e.getMessage());
            }

            done = (System.currentTimeMillis() - start) / 1000.0;
            log.info("Done in " + done + "s");
        });

        th.setDaemon(false);
        th.start();
    }

    protected void encrypt(byte[] text, byte[] key) {
        encrypt(text, key, 0);
    }

    protected int encrypt(byte[] text, byte[] key, int off) {
        int keyPos = off;
        for (int i = 0; i < text.length; i++) {
            text[i] = (byte) (text[i] ^ key[keyPos]);
            keyPos = (keyPos+1) % key.length;
        }
        return keyPos;
    }
}

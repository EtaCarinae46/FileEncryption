package service;

import service.base.BaseEncryption;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class NormalEncryption extends BaseEncryption {

    private final Log log = new Log();
    public int buffer = Main.defaultBuffer;
    private final int BUFFER = 153600;

    @Override
    protected void start(List<File> files, String rawKey) throws IOException {
        byte[] key = rawKey.getBytes();

        RandomAccessFile in;

        for (File file : files) {
            log.info("File path: " + file.getPath());
            if (!file.exists() || file.length() == 0) {
                log.warn("File not found or empty, continue!");
                continue;
            }

            in = new RandomAccessFile(file, "rw");

            byte[] readBytes = new byte[BUFFER];
            int counter = 0;
            int off = 0;

            while(true) {
                int actuallyRead = in.read(readBytes, 0, BUFFER);
                off = ownEncrypt(readBytes, key, off);
                in.seek((long) counter * BUFFER);
                in.write(readBytes,0, actuallyRead);
                if (actuallyRead < BUFFER) break;
                counter++;
            }
            in.close();
        }
    }

    @Override
    protected void start(List<File> files, File keyFile) throws IOException {
        byte[] text = new byte[buffer];
        byte[] key = new byte[buffer];

        int textSize;
        int keySize;
        RandomAccessFile inFile;
        RandomAccessFile inKey;

        for (File file : files) {
            log.info("File path: " + file.getPath());
            if (!file.exists() || file.length() == 0) {
                log.warn("File not found, continue!");
                continue;
            }

            inFile = new RandomAccessFile(file, "rw");
            inKey = new RandomAccessFile(keyFile, "r");

            textSize = inFile.read(text, 0, buffer);
            keySize = inKey.read(key, 0, buffer);

            encrypt(text, key);

            inFile.seek(0);
            inFile.write(text, 0, textSize);

            inFile.close();
            inKey.close();
        }
    }

    private static int ownEncrypt(byte[] text, byte[] key, int off) {
        int[] keyHash = createSimpleHash(key);

        int nextKey = Math.max(off, 0);
        int tmpChar;

        for (int i = 0; i < text.length; i++) {
            tmpChar = text[i];
            text[i] = (byte) ((text[i] ^ key[nextKey]) ^ keyHash[i % key.length]);
            nextKey = (tmpChar * text[i]) % key.length;
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
}
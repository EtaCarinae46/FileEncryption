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

    @Override
    protected void start(List<File> files, String rawKey) throws IOException {
        byte[] text = new byte[buffer];
        byte[] key = rawKey.getBytes();

        int realSize;
        RandomAccessFile in;

        for (File file : files) {
            log.info("File path: " + file.getPath());
            if (!file.exists() || file.length() == 0) {
                log.warn("File not found or empty, continue!");
                continue;
            }

            in = new RandomAccessFile(file, "rw");
            realSize = in.read(text, 0, buffer);
            in.seek(0);
            encrypt(text, key);
            in.write(text, 0, realSize);
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
}
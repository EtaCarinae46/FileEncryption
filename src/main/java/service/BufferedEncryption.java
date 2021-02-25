package service;

import controller.fxCtrls.MainCtrl;
import service.base.BaseEncryption;
import javafx.scene.control.ProgressBar;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;


public class BufferedEncryption extends BaseEncryption {

    private final MainCtrl mainCtrl = Main.mainLoader.getController();
    private final Log log = new Log();

    private final ProgressBar progressBar = mainCtrl.getProgressBar();
    private final int BUFFER = 2097152; // 2MB

    public BufferedEncryption() {}

    @Override
    protected void start(List<File> files, String rawKey) throws IOException {
        RandomAccessFile inFile;
        int blockSize, readBytes, blockCount, keyPos = 0;
        byte[] text, key = rawKey.getBytes();
        long currentFilePointer;

        for (File file : files) {
            log.info("File path: " + file.getPath());
            if (!file.exists() || file.length() == 0) {
                log.warn("File not found or empty, continue!");
                continue;
            }
            inFile = new RandomAccessFile(file, "rw");

            blockSize = (int)Math.min(file.length(), BUFFER);
            blockCount = (int)Math.ceil(file.length()/(double) blockSize);
            text = new byte[blockSize];

            progressBar.setVisible(true);

            for (int i = 0; i < blockCount; i++) {
                currentFilePointer = inFile.getFilePointer();
                readBytes = inFile.read(text, 0, blockSize);
                keyPos = encrypt(text, key, keyPos);

                inFile.seek(currentFilePointer);
                inFile.write(text, 0, readBytes);
                progressBar.setProgress(i/(double)blockCount);
            }
            inFile.close();
            progressBar.setVisible(false);
            progressBar.setProgress(0.0);
        }
    }

    @Override
    protected void start(List<File> files, File keyFile) throws IOException {
        RandomAccessFile inFile, inKey = new RandomAccessFile(keyFile, "r");
        int blockSize, keySize, readBytes, readKey, blockCount, keyPos = 0;
        long currentFilePointer;
        byte[] text, tempKey;

        for (File file : files) {
            log.info("File path: " + file.getPath());
            if (!file.exists() || file.length() == 0) {
                log.warn("File not found or empty, continue!");
                continue;
            }
            inFile = new RandomAccessFile(file, "rw");

            blockSize = (int) Math.min(file.length(), BUFFER);
            keySize = (int) Math.min(keyFile.length(), BUFFER);

            text = new byte[blockSize];
            tempKey = new byte[keySize];
            blockCount = (int) Math.ceil(file.length() / (double) blockSize);

            progressBar.setVisible(true);

            for (int i = 0; i < blockCount; i++) {
                currentFilePointer = inFile.getFilePointer();
                inKey.seek(inKey.getFilePointer() % keyFile.length());
                readBytes = inFile.read(text, 0, blockSize);
                inKey.read(tempKey, 0, keySize);
                keyPos = encrypt(text, tempKey, keyPos);
                inFile.seek(currentFilePointer);
                inFile.write(text, 0, readBytes);
                progressBar.setProgress(i / (double) blockCount);
            }
            inFile.close();
            progressBar.setVisible(false);
            progressBar.setProgress(0.0);
        }
    }
}

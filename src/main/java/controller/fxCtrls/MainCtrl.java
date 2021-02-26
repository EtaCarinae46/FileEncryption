package controller.fxCtrls;

import static main.Main.logger;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.AlertBox;
import main.Main;
import service.Log;
import service.NormalEncryption;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainCtrl {

    @FXML private CheckBox       bufferedEncrypt;
    @FXML private CheckBox       enableAdvanced;
    @FXML private CheckBox       encryptMaxSize;
    @FXML private TextField      bufferSizeArea;
    @FXML private VBox           advancedVBox;
    @FXML private CheckBox       keyFromFile;
    @FXML private ProgressBar    progressBar;
    @FXML private TextField      filePath;
    @FXML private PasswordField  pwdField;
    @FXML private TextField      keyPath;
    @FXML private TextArea       logArea;

    private final Log log = new Log();
    private final Locale locale = new Locale("en");
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", locale);
    private List<File> fileList = new ArrayList<>();

    /**
     * File choosing modal
     */
    @FXML
    private void fileChooser() {

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files", "*"));
        fileList = fc.showOpenMultipleDialog(null);

        // Ha több mint 1 fájl lett kiválasztva
        if (fileList != null && fileList.size() > 1) {

            // Fájlok számának kiírása
            filePath.setDisable(true);
            filePath.setText(fileList.size() + " files opened!");

            // Log tab...
            logger.info("Selected files (" + fileList.size() + "):");
            for (File a : fileList)
                logger.info(a.getAbsolutePath());

        } else if (fileList != null && fileList.size() == 1) { // Ha csak 1 fájl
            filePath.disableProperty().setValue(true);
            filePath.setText(fileList.get(0).getPath());
            logger.info("Selected file: " + fileList.get(0));
        }
    }

    /**
     * Validates the UI then starts the encryption
     * @throws IOException
     */
    @FXML
    private void encrypt() throws IOException {
        NormalEncryption normalEncryption = new NormalEncryption();

        if (fileList != null && fileList.size() == 0) {
            fileList.add(new File(filePath.getText()));
        }

        if (handleErrors() != 0)
            return;

        boolean adv = enableAdvanced.isSelected();
        boolean key = keyFromFile.isSelected();
        boolean max = encryptMaxSize.isSelected();
        boolean buf = bufferedEncrypt.isSelected();

        // if advanced options are turned off just running normal encryption
        if (!adv) {
            normalEncryption.encrypt(fileList, pwdField.getText().getBytes());
            return;
        }
        byte[] keyArray = null;
        if (key) {
            File keyFile = new File(keyPath.getText());
            if (keyFile.length() > Main.defaultBuffer) {
                log.error("Too big key!");
                return;
            }
            RandomAccessFile inKey = new RandomAccessFile(keyFile, "r");
            keyArray = new byte[(int)keyFile.length()];
            inKey.read(keyArray);
        }

        int size;
        if (max && (size = toBufferSize(bufferSizeArea.getText())) != -1) {
            normalEncryption.setLimit(size);
        }

        if (buf) {
            normalEncryption.setLimit(-1);
        }

        normalEncryption.encrypt(fileList, keyArray == null ? pwdField.getText().getBytes() : keyArray);

    }

    /**
     * Helper method, converts a String text safely to int
     * if it cannot be converted to int it'll return -1 (as a Buffer size cannot be -1)
     * @param text as an input
     * @return integer, -1 if the text cannot be converted
     */
    private int toBufferSize(String text) {
        int i;
        try {
            i = Integer.parseInt(text);
            if (i < 0) i = -1;
        } catch (Exception e) {
            i = -1;
        }
        return i;
    }

    /**
     * Helper method that validates the UI
     * @return 0 on success
     */
    private int handleErrors() {
        // Checking pwd field and file path input
        if ((pwdField.getText().length() <= 3 && !(enableAdvanced.isSelected() && keyFromFile.isSelected()
                && keyPath.getText().length() > 0)) || filePath.getText().length() == 0) {
            AlertBox.show("Error", resourceBundle.getString("missing"), "Okay");
            return 1;
        }

        // Warning prompt
        if (AlertBox.show("Warning", resourceBundle.getString(enableAdvanced.isSelected() ? "warning.advanced" : "warning.normal"), "Yes", "No") != 1)
            return 2;

        return 0;
    }


    /**
     * Clears the file list
     */
    @FXML private void clearList() {
        fileList = new ArrayList<>();
        filePath.setText("");
        pwdField.setText("");
        filePath.disableProperty().setValue(false);
        logger.info("File list cleared!");
    }

    @FXML private void clearLog() {
        logArea.setText("");
    }

    /**
     * Enables the other checkboxes/text fields
     * if the advanced checkbox is checked
     */
    @FXML private void enableAdvancedClick() {
        advancedVBox.setDisable(!enableAdvanced.isSelected());
        keyFromFile.setSelected(false);
        keyPath.setDisable(true);
        encryptMaxSize.setSelected(false);
        encryptMaxSize.setDisable(false);
        bufferSizeArea.setDisable(true);
        bufferedEncrypt.setSelected(false);
    }

    @FXML private void keyFromFileClick() {
        keyPath.setDisable(!keyFromFile.isSelected());
        pwdField.setDisable(keyFromFile.isSelected());
    }

    @FXML private void encryptMaxSizeAction() {
        bufferSizeArea.setDisable(!encryptMaxSize.isSelected());
    }

    @FXML private void bufferedEncryptAction() {
        encryptMaxSize.setSelected(false);
        bufferSizeArea.setDisable(!encryptMaxSize.isSelected());
        encryptMaxSize.setDisable(bufferedEncrypt.isSelected());
    }

    @FXML private void closeEvent() {
        Main.window.close();
    }

    private double x,y;

    public void setLog(String text, boolean clear) {
        if (clear) logArea.setText(text);
        else logArea.setText(logArea.getText() + text);

        logArea.setScrollTop(Double.MAX_VALUE);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @FXML private void dragBarMousePressed(MouseEvent e) {
        x = e.getSceneX();
        y = e.getSceneY();
    }

    @FXML private void dragBarDrag(MouseEvent e) {
        Main.window.setX(e.getScreenX() - x);
        Main.window.setY(e.getScreenY() - y);
    }
}

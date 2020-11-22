package controller.fxCtrls;

import static main.Main.logger;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.AlertBox;
import main.Main;
import service.BufferedEncryption;
import service.NormalEncryption;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainCtrl {

    /*  ==== DEKLARÁCIÓK ====  */
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

    private final Locale locale = new Locale("en");
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", locale);
    private List<File> fileList = new ArrayList<>();

    /*  ==== FÁJLOK KIVÁLASZTÁSA ====  */
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

    /*  ==== ENCRIPTION ====  */
    @FXML
    private void encrypt() {
        NormalEncryption normalEncryption = new NormalEncryption();
        BufferedEncryption bufferedEncryption = new BufferedEncryption();

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
            normalEncryption.encrypt(fileList, pwdField.getText());
            return;
        }

        // if buffered encryption (Encrypt the entire file) checked no need to check max buff Size
        if (buf) {
            if (key && keyPath.getText().length() > 0) {
                bufferedEncryption.encrypt(fileList, new File(keyPath.getText()));
            } else {
                bufferedEncryption.encrypt(fileList, pwdField.getText());
            }

            return;
        }

        // At this pont we're sure we'll need normal encryption since buffered encryption not checked
        // if max encrypt size checked and its a number change default buffer
        int newBuffSize;
        if (max && bufferSizeArea.getText().length() > 0 &&
                (newBuffSize = toBufferSize(bufferSizeArea.getText())) != -1) {
            normalEncryption.buffer = newBuffSize;
        }

        if (key) {
            normalEncryption.encrypt(fileList, new File(keyPath.getText()));
        } else {
            normalEncryption.encrypt(fileList, pwdField.getText());
        }
    }

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

    /*  ==== VALIDATING INPUTS ====  */
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


    /*  ==== FÁJL LISTA TÖRLÉSE ====  */
    @FXML private void clearList() {
        fileList = new ArrayList<>();
        filePath.setText("");
        pwdField.setText("");
        filePath.disableProperty().setValue(false);
        logger.info("File list cleared!");
    }


    /*  ==== LOG TÖRLÉSE ====  */
    @FXML private void clearLog() {
        logArea.setText("");
    }

    /*  ==== VBOX FELOLDÁSA (HALADÓ BEÁLLÍTÁSOK) ====  */
    @FXML private void enableAdvancedClick() {
        advancedVBox.setDisable(!enableAdvanced.isSelected());
        keyFromFile.setSelected(false);
        keyPath.setDisable(true);
        encryptMaxSize.setSelected(false);
        bufferSizeArea.setDisable(true);
        bufferedEncrypt.setSelected(false);
    }

    /*  ==== KULCS FORRÁS FELOLDÁSA (HALADÓ BEÁLLÍTÁSOK) ====  */
    @FXML private void keyFromFileClick() {
        keyPath.setDisable(!keyFromFile.isSelected());
        pwdField.setDisable(keyFromFile.isSelected());
    }

    /*  ==== MAX TITKOSÍTÁSI MÉRET BEVITEL FELOLDÁSA (HALADÓ BEÁLLÍTÁSOK) ====  */
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

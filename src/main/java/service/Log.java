package service;

import controller.fxCtrls.MainCtrl;
import main.Main;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private final MainCtrl mainCtrl = Main.mainLoader.getController();

    public void info(String text) {
        info(text, false);
    }

    public void info(String text, boolean clear) {
        commonLog(" [INFO] ", text, clear);
    }

    public void warn(String text) {
        warn(text, false);
    }

    public void warn(String text, boolean clear) {
        commonLog(" [WARN] ", text, clear);
    }

    public void error(String text) {
        error(text, false);
    }

    public void error(String text, boolean clear) {
        commonLog(" [ERROR] ", text, clear);
    }

    private void commonLog(String prefix, String text, boolean clear) {
        String pattern = "HH:mm:ss,SSS";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        String log = date + prefix + text + "\n";
        mainCtrl.setLog(log, clear);
    }
}

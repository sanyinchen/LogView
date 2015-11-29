/*
 * Copyright (C) Apache licence, Inc. All Rights Reserved.
 */
package tony.com.logmodel.model;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by sanyinchen on 15/11/24.
 */
public class LogCat extends Thread {
    private Process process;
    private BufferedReader bufferedReader;
    private LogCatListner logCatListner;
    private boolean continueReading = true;

    public void stopReading() {
        continueReading = false;
    }

    // 设置监听
    public void setListener(LogCatListner listener) {
        logCatListner = listener;
    }

    public LogCatListner getCurrentListener() {
        return logCatListner;
    }

    private void readLogCat() {
        BufferedReader bufferedReader = getBufferedReader();
        String trace = null;
        try {
            trace = bufferedReader.readLine();
            while (trace != null && continueReading) {
                notifyListener(trace);
                trace = bufferedReader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private BufferedReader getBufferedReader() {
        if (bufferedReader == null && process != null) {
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        }
        return bufferedReader;
    }

    private void notifyListener(String trace) {
        if (logCatListner != null) {
            // Log.d("srcomp", "底层trace分发-----" + trace);
            // System.out.println("-----------trace:" + trace+"--------------");
            logCatListner.onTraceRead(trace);
        }
    }

    // 主要执行操作
    @Override
    public void run() {
        super.run();
        try {
            process = Runtime.getRuntime().exec("logcat -v time");
        } catch (IOException e) {
            e.printStackTrace();
        }
        readLogCat();
    }

    // 回调
    public interface LogCatListner {
        void onTraceRead(String traceMsg);
    }

}

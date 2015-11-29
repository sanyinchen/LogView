/*
 * Copyright (C) Apache licence, Inc. All Rights Reserved.
 */
package tony.com.logmodel.model;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by sanyinchen on 15/11/24.
 */
public class LogMainThread implements MainThread {
    private final Handler handler;

    public LogMainThread() {
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void post(Runnable runnable) {
        handler.post(runnable);
    }
}

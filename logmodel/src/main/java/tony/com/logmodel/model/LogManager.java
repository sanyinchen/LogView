/*
 * Copyright (C) Apache licence, Inc. All Rights Reserved.
 */
package tony.com.logmodel.model;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;
import tony.com.logmodel.LogConfig;

/**
 * Created by sanyinchen on 15/11/24.
 */
public class LogManager {
    private LogCat logCat;
    private MainThread mainThread;
    private List<TraceObject> tracesBuffer;
    private Listener listener;
    private LogConfig logConfig = new LogConfig();
    private long lastNotification;

    public LogManager(LogCat logCat, MainThread mainThread) {
        this.logCat = logCat;
        this.mainThread = mainThread;
        tracesBuffer = new LinkedList<>();
    }

    public void startReading() {
        logCat.setListener(new LogCat.LogCatListner() {
            @Override
            public void onTraceRead(String traceMsg) {
                // Log.d("srcomp", "startReading -----");
                addTraceToBuffer(traceMsg);
                notifyNewTraces();
            }
        });
        if (logCat.getState().equals(Thread.State.NEW)) {
            Log.d("srcomp", "logCatThread start-----");
            logCat.start();
        }
    }

    public void reStart() {
        LogCat.LogCatListner logCatListner = logCat.getCurrentListener();
        if (logCatListner == null) {
            logCatListner = new LogCat.LogCatListner() {
                @Override
                public void onTraceRead(String traceMsg) {
                    addTraceToBuffer(traceMsg);
                }
            };
        }
        lastNotification = 0;
        if (tracesBuffer != null) {
            tracesBuffer.clear();
        } else {
            tracesBuffer = new LinkedList<>();
        }
        if (logCat != null || logCat.isAlive()) {
            logCat.stopReading();
            logCat.interrupt();
            logCat = null;
        }
        logCat = new LogCat();
        logCat.setListener(logCatListner);
        logCat.start();
    }

    public void stopReading() {
        logCat.stopReading();
        logCat.interrupt();
    }

    private synchronized void addTraceToBuffer(String trace) {
        if (shouldAddTrace(trace)) {
            //Log.d("srcomp", "addTraceToBuffer -----" + trace);
            TraceObject traceObject = null;
            try {
                traceObject = TraceObject.fromString(trace);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (traceObject != null) {
                if (!tracesBuffer.contains(traceObject)) {
                    tracesBuffer.add(traceObject);
                }
            } else {
                Log.e("srcomp", "error type trace");
            }
        } else {
            // Log.e("srcomp", "shouldAddTrace failure");
        }

    }

    private boolean shouldAddTrace(String trace) {
        boolean hasFilterConfigured = logConfig.hasFilter();
        return !hasFilterConfigured || traceMatchesFilter(trace);
    }

    private synchronized boolean traceMatchesFilter(String logcatTrace) {
        TraceLevel levelFilter = logConfig.getFilterTraceLevel();
        String filter = logConfig.getFilter().toLowerCase();
        String logcatTraceLowercase = logcatTrace.toLowerCase();
        return logcatTraceLowercase.contains(filter) && containsTraceLevel(logcatTrace, levelFilter);
    }

    private boolean containsTraceLevel(String logcatTrace, TraceLevel levelFilter) {
        return levelFilter.equals(TraceLevel.VERBOSE) || hasTraceLevelEqualOrHigher(logcatTrace,
                levelFilter);
    }

    private boolean hasTraceLevelEqualOrHigher(String logcatTrace, TraceLevel levelFilter) {
        TraceLevel level = TraceLevel.getTraceLevel(logcatTrace.charAt(TraceObject.TRACE_LEVEL_INDEX));
        return level.ordinal() >= levelFilter.ordinal();
    }

    private boolean shouldNotifyListeners() {
        long now = System.currentTimeMillis();
        long timeFromLastNotification = now - lastNotification;
        boolean hasTracesToNotify = tracesBuffer.size() > 0;
        return timeFromLastNotification > logConfig.getSamplingRate() && hasTracesToNotify;
    }

    private void notifyNewTraces() {
        if (shouldNotifyListeners()) {
            final List<TraceObject> traces = new LinkedList<>(tracesBuffer);
            tracesBuffer.clear();
            finalNotification(traces);
        }
    }

    private synchronized void finalNotification(final List<TraceObject> tracesBuffer) {
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onNewTraces(tracesBuffer);
                    lastNotification = System.currentTimeMillis();
                }
            }
        });

    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    public void unregisterListener() {
        this.listener = null;
    }

    public LogConfig getLogConfig() {
        return logConfig;
    }

    public void setLogConfig(LogConfig logConfig) {
        this.logConfig = logConfig;
    }

    public interface Listener {
        void onNewTraces(List<TraceObject> traces);
    }
}

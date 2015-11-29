/*
 * Copyright (C) Apache licence, Inc. All Rights Reserved.
 */
package tony.com.logmodel.control;

import java.util.List;

import android.util.Log;
import android.view.View;
import tony.com.logmodel.LogConfig;
import tony.com.logmodel.LogView;
import tony.com.logmodel.model.LogManager;
import tony.com.logmodel.model.TraceLevel;
import tony.com.logmodel.model.TraceObject;

/**
 * Created by sanyinchen on 15/11/24.
 */
public class LogCtrl implements LogManager.Listener {
    private static final int MIN_VISIBLE_POSITION_TO_ENABLE_AUTO_SCROLL = 3;
    private LogManager logManager;
    private LogView logView;
    private TraceBuffer traceBuffer;
    private boolean isInitalized;

    public LogCtrl(LogManager logManager, LogView logView, LogConfig logConfig) {
        this.logManager = logManager;
        this.logView = logView;
        if (logConfig == null) {
            throw new IllegalArgumentException("logConfig must not be null");
        }
        traceBuffer = new TraceBuffer(logConfig.getMaxNumberOfTracesToShow());
        setLogConfig(logConfig);
    }

    public void resume() {
        if (!isInitalized) {
            isInitalized = true;
            logManager.registerListener(this);
            logManager.startReading();
        }
    }

    public void pause() {
        if (isInitalized) {
            isInitalized = false;
            logManager.unregisterListener();
            logManager.stopReading();
        }
    }

    public void setLogConfig(LogConfig config) {

        updateBufferConfig(config);
        updateLogConfig(config);
    }

    private void updateLogConfig(LogConfig logConfig) {
        if (logManager != null) {
            logManager.setLogConfig(logConfig);
        }
    }

    private void updateBufferConfig(LogConfig lynxConfig) {
        traceBuffer.setBufferSize(lynxConfig.getMaxNumberOfTracesToShow());
        refreshTraces();
    }

    private void refreshTraces() {
        onNewTraces(traceBuffer.getTraces());
    }

    @Override
    public void onNewTraces(List<TraceObject> traces) {
        // test codes
        //        Log.i("srcomp", "bucket size -->" + traces.size());
        //        for (TraceObject o : traces) {
        //            Log.i("srcomp", o.toString());
        //        }
        int tracesRemoved = updateTraceBuffer(traces);
        List<TraceObject> tracesToNotify = getCurrentTraces();
        logView.showTraces(tracesToNotify, tracesRemoved);
        // Log.d("srcomp", "tracesRemoved-->" + tracesRemoved);
        // Log.d("srcomp", "traceBuffer size: " + traceBuffer.getCurrentNumberOfTraces());

    }

    private int updateTraceBuffer(List<TraceObject> traces) {
        return traceBuffer.add(traces);
    }

    public void updateFilter(String filter) {
        if (isInitalized) {
            LogConfig lynxConfig = logManager.getLogConfig();
            lynxConfig.setFilter(filter);
            logManager.setLogConfig(lynxConfig);
            clearView();
            restartLog();
        }
    }

    private void clearView() {
        traceBuffer.clear();
        logView.clear();
    }

    public List<TraceObject> getCurrentTraces() {
        return traceBuffer.getTraces();
    }

    public void onScrollToPosition(int lastVisiblePositionInTheList) {
        if (shouldDisableAutoScroll(lastVisiblePositionInTheList)) {
            // Log.d("srcomp","禁止滑动");
            logView.disableAutoScroll();
        } else {
            // Log.d("srcomp","开放滑动");
            logView.enableAutoScroll();
        }
    }

    private boolean shouldDisableAutoScroll(int lastVisiblePosition) {
        int positionOffset = traceBuffer.getCurrentNumberOfTraces() - lastVisiblePosition;
        return positionOffset >= MIN_VISIBLE_POSITION_TO_ENABLE_AUTO_SCROLL;
    }

    public void updateFilterTraceLevel(TraceLevel level) {
        if (isInitalized) {
            clearView();
            LogConfig lynxConfig = logManager.getLogConfig();
            lynxConfig.setFilterTraceLevel(level);
            logManager.setLogConfig(lynxConfig);
            restartLog();
        }
    }

    private void restartLog() {
        logManager.reStart();
    }

    public interface View {

        void showTraces(List<TraceObject> traces, int removedTraces);

        void clear();

        void disableAutoScroll();

        void enableAutoScroll();

        void agentRefreshAction(android.view.View.OnClickListener onClickListener);
    }
}

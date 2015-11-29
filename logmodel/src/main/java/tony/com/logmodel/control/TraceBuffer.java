/*
 * Copyright (C) Apache licence, Inc. All Rights Reserved.
 */
package tony.com.logmodel.control;

import java.util.LinkedList;
import java.util.List;

import tony.com.logmodel.model.TraceObject;

/**
 * Created by sanyinchen on 15/11/24.
 */
public class TraceBuffer {

    private int bufferSize;
    private final List<TraceObject> traces;

    TraceBuffer(int bufferSize) {
        this.bufferSize = bufferSize;
        traces = new LinkedList<TraceObject>();
    }

    void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        removeExceededTracesIfNeeded();
    }

    int add(List<TraceObject> traces) {
        this.traces.addAll(traces);
        return removeExceededTracesIfNeeded();
    }

    List<TraceObject> getTraces() {
        return traces;
    }

    public int getCurrentNumberOfTraces() {
        return traces.size();
    }

    public void clear() {
        traces.clear();
    }

    private int removeExceededTracesIfNeeded() {
        int tracesToDiscard = getNumberOfTracesToDiscard();
        if (tracesToDiscard > 0) {
            discardTraces(tracesToDiscard);
        }
        return tracesToDiscard;
    }

    private int getNumberOfTracesToDiscard() {
        int currentTracesSize = this.traces.size();
        int tracesToDiscard = currentTracesSize - bufferSize;
        tracesToDiscard = tracesToDiscard < 0 ? 0 : tracesToDiscard;
        return tracesToDiscard;
    }

    private void discardTraces(int tracesToDiscard) {
        for (int i = 0; i < tracesToDiscard; i++) {
            traces.remove(0);
        }
    }

}

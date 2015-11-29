/*
 * Copyright (C) Apache licence, Inc. All Rights Reserved.
 */
package tony.com.logmodel.model;

/**
 * Created by sanyinchen on 15/11/24.
 */
public class TraceObject {
    private static final char TRACE_LEVEL_SEPARATOR = '/';
    private static final int END_OF_DATE_INDEX = 18;
    private static final int START_OF_MESSAGE_INDEX = 21;
    public static final int MIN_TRACE_SIZE = 21;
    public static final int TRACE_LEVEL_INDEX = 19;

    private TraceLevel traceLevel;
    private String message;
    private String date;

    public TraceObject(TraceLevel traceLevel, String message, String date) {
        this.traceLevel = traceLevel;
        this.message = message;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // egc "02-07 17:45:33.014 D/Any debug trace"
    public static TraceObject fromString(String locatTrace) throws Exception {
        if (locatTrace == null || locatTrace.length() < MIN_TRACE_SIZE
                || locatTrace.charAt(20) != TRACE_LEVEL_SEPARATOR) {
            // throw new Exception("Consturctor traceobject Error");
            return null;
        } else {
            TraceLevel level = TraceLevel.getTraceLevel(locatTrace.charAt(TRACE_LEVEL_INDEX));
            String date = locatTrace.substring(0, END_OF_DATE_INDEX);
            String message = locatTrace.substring(START_OF_MESSAGE_INDEX, locatTrace.length());
            return new TraceObject(level, message, date);
        }
    }

    public TraceLevel getTraceLevel() {
        return this.traceLevel;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TraceObject)) {

            return false;
        }
        TraceObject traceObject = (TraceObject) o;
        return traceObject.getMessage().equals(message) && traceObject.getTraceLevel() == traceLevel || traceObject
                .getDate().equals(getDate()) || message.contains(((TraceObject) o).getMessage())  || extraCompare(message);
    }

    protected boolean extraCompare(String trace) {
        String[] tests = trace.split("Trace\\{");
        if (tests.length > 1) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = traceLevel.hashCode();
        result = 31 * result + message.hashCode() + date.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Trace{" + "'level=" + traceLevel + ", message='" + message + '\'' + '}';
    }
}

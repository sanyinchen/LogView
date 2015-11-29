/*
 * Copyright (C) Apache licence, Inc. All Rights Reserved.
 */
package tony.com.logmodel;

import java.io.Serializable;

import tony.com.logmodel.model.TraceLevel;

/**
 * Created by sanyinchen on 15/11/24.
 */
public class LogConfig implements Serializable, Cloneable {
    private static final long serialVersionUID = 293939299388293L;

    private static final float DEFAULT_TEXT_SIZE_IN_PX = 36;

    private int maxNumberOfTracesToShow = 800;
    private String filter;
    private TraceLevel filterTraceLevel;
    private Float textSizeInPx;
    private int samplingRate = 150;

    public LogConfig() {
        // filter = "console";
        filter = "comp";
        filterTraceLevel = TraceLevel.VERBOSE;
    }

    public LogConfig setMaxNumberOfTracesToShow(int maxNumberOfTracesToShow) {
        if (maxNumberOfTracesToShow <= 0) {
            throw new IllegalArgumentException(
                    "You can't use a max number of traces equals or lower than zero.");
        }

        this.maxNumberOfTracesToShow = maxNumberOfTracesToShow;
        return this;
    }

    public LogConfig setFilter(String filter) {
        if (filter == null) {
            throw new IllegalArgumentException("filter can't be null");
        }
        this.filter = filter;
        return this;
    }

    public LogConfig setFilterTraceLevel(TraceLevel filterTraceLevel) {
        if (filterTraceLevel == null) {
            throw new IllegalArgumentException("filterTraceLevel can't be null");
        }
        this.filterTraceLevel = filterTraceLevel;
        return this;
    }

    public LogConfig setTextSizeInPx(float textSizeInPx) {
        this.textSizeInPx = textSizeInPx;
        return this;
    }

    public LogConfig setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
        return this;
    }

    public int getMaxNumberOfTracesToShow() {
        return maxNumberOfTracesToShow;
    }

    public String getFilter() {
        return filter;
    }

    public TraceLevel getFilterTraceLevel() {
        return filterTraceLevel;
    }

    public boolean hasFilter() {
        return !"".equals(filter) || !TraceLevel.VERBOSE.equals(filterTraceLevel);
    }

    public float getTextSizeInPx() {
        return textSizeInPx == null ? DEFAULT_TEXT_SIZE_IN_PX : textSizeInPx;
    }

    public boolean hasTextSizeInPx() {
        return textSizeInPx != null;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LogConfig)) {
            return false;
        }

        LogConfig that = (LogConfig) o;

        if (maxNumberOfTracesToShow != that.maxNumberOfTracesToShow) {
            return false;
        }
        if (samplingRate != that.samplingRate) {
            return false;
        }
        if (filter != null ? !filter.equals(that.filter) : that.filter != null) {
            return false;
        }
        if (textSizeInPx != null ? !textSizeInPx.equals(that.textSizeInPx)
                : that.textSizeInPx != null) {
            return false;
        }
        if (filterTraceLevel != that.filterTraceLevel) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = maxNumberOfTracesToShow;
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        result = 31 * result + (textSizeInPx != null ? textSizeInPx.hashCode() : 0);
        result = 31 * result + samplingRate;
        return result;
    }

    @Override
    public Object clone() {
        return new LogConfig().setMaxNumberOfTracesToShow(getMaxNumberOfTracesToShow())
                .setFilter(filter)
                .setFilterTraceLevel(filterTraceLevel)
                .setSamplingRate(getSamplingRate());
    }

    @Override
    public String toString() {
        return "LynxConfig{"
                + "maxNumberOfTracesToShow="
                + maxNumberOfTracesToShow
                + ", filter='"
                + filter
                + '\''
                + ", textSizeInPx="
                + textSizeInPx
                + ", samplingRate="
                + samplingRate
                + '}';
    }
}

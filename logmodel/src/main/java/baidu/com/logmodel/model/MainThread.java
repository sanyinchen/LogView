/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package baidu.com.logmodel.model;

/**
 * Created by sanyinchen on 15/11/24.
 */
public interface MainThread {
    void post(Runnable runnable);
}

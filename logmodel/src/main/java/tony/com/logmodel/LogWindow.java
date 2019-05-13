/*
 * Copyright (C) Apache licence, Inc. All Rights Reserved.
 */
package tony.com.logmodel;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import tony.com.logmodel.CommonUtils.LogUtils;

/**
 * Created by sanyinchen on 15/11/25.
 */
public class LogWindow {
    private LogView logView;
    private WindowManager.LayoutParams params;
    private WindowManager wm;
    private boolean isAdd = false;
    private Context context;
    private View.OnTouchListener onTouchListener;
    private Application mApplication;
    private WeakReference<LogWindow> owf;
    private boolean registerLifeCycleInStop = false;
    private LogConfig logConfig;
    private int INITIALHEIGHT = 5;
    private int MINHEIGHT = 170;
    private int MAXHEIGHTOFFSET = 10;
    private int INITIALTOPUCH = 25;
    private int MAXTOUCHAREA = 50;
    private int MINWEDITH = 100;
    private LogView.ChangeWindowListener changeWindowListener;

    public LogWindow setregisterLifeCycleInStop(boolean registerLifeCycleInStopn) {
        this.registerLifeCycleInStop = registerLifeCycleInStopn;
        return this;
    }

    public LogWindow(Context context, Application application) {
        this.context = context;
        this.mApplication = application;
        owf = new WeakReference<LogWindow>(this);
    }

    public LogWindow setOnTouchListener(View.OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
        return this;
    }

    public LogWindow setWindowManager(WindowManager wm, WindowManager.LayoutParams params) {
        this.wm = wm;
        this.params = params;
        return this;
    }

    public LogWindow setLogCOnfig(LogConfig logConfig) {
        this.logConfig = logConfig;
        return this;
    }

    public void dismiss() {
        if (isAdd && logView != null && logView.getParent() != null && wm != null) {
            wm.removeView(logView);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void creatLogView() {
        Log.d("srcomp", "creat LogView");
        if (!isAdd) {
            Map<String, Object> extradata = new HashMap<>();
            extradata.put("heidth", (LogUtils.getDevDispplay(context)[1]) / INITIALHEIGHT);
            extradata.put("width", LogUtils.getDevDispplay(context)[0]);
            extradata.put("touchArea", LogUtils.diptopx(context, INITIALTOPUCH));
            extradata.put("touchAreaMax", LogUtils.diptopx(context, MAXTOUCHAREA));
            if (wm == null) {
                wm = (WindowManager) context.getSystemService(
                        Context.WINDOW_SERVICE);
                params = new WindowManager.LayoutParams();
                params.format = PixelFormat.RGBA_8888;
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION;
                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = LogUtils.diptopx(context, (int) extradata.get("heidth"));

            }
            isAdd = true;
            if (logView == null) {

                logView = new LogView(context, extradata);
            }
            if (onTouchListener == null) {
                onTouchListener = new View.OnTouchListener() {
                    int lastX, lastY;
                    int paramX, paramY;
                    int startX, startY;

                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {

                            case MotionEvent.ACTION_DOWN:
                                lastX = (int) event.getRawX();
                                lastY = (int) event.getRawY();
                                paramX = params.x;
                                paramY = params.y;
                                startX = lastX;
                                startY = lastY;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                int dx = (int) event.getRawX() - lastX;
                                int dy = (int) event.getRawY() - lastY;
                                params.x = paramX + dx;
                                params.y = paramY + dy;
                                // 更新悬浮窗位置
                                wm.updateViewLayout(logView, params);
                                break;

                            default:
                                break;
                        }

                        return false;
                    }
                };
            }
            if (changeWindowListener == null) {
                changeWindowListener = new LogView.ChangeWindowListener() {

                    @Override
                    public void changeWindowHeight(int height) {

                        if (height < LogUtils.diptopx(context, MINHEIGHT)) {
                            height = LogUtils.diptopx(context, MINHEIGHT);
                        }
                        if (height >= LogUtils.getDevDispplay(context)[1] - LogUtils
                                .diptopx(context, MAXHEIGHTOFFSET)) {
                            height = LogUtils.getDevDispplay(context)[1] - LogUtils.diptopx(context, MAXHEIGHTOFFSET);
                        }
                        params.height = height;
                        wm.updateViewLayout(logView, params);
                    }

                    @Override
                    public void changeWindowsWidth(int width) {
                        if (width <= LogUtils.diptopx(context, MINWEDITH)) {
                            width = LogUtils.diptopx(context, MINWEDITH);
                        }
                        params.width = width;
                        wm.updateViewLayout(logView, params);
                    }

                };
            }

            logView.setOnTouchListener(onTouchListener);
            logView.setChangeWindowListener(changeWindowListener);
            if (logConfig != null && logView.getLogManager() != null) {
                logView.getLogManager().setLogConfig(logConfig);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mApplication.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                    }

                    @Override
                    public void onActivityStarted(Activity activity) {

                    }

                    @Override
                    public void onActivityResumed(Activity activity) {

                    }

                    @Override
                    public void onActivityPaused(Activity activity) {

                    }

                    @Override
                    public void onActivityStopped(Activity activity) {

                        LogWindow logWindow = owf.get();
                        if (logWindow == null) {
                            return;
                        }
                        if (logWindow.registerLifeCycleInStop) {
                            Context context = logWindow.context;
                            WindowManager wm = logWindow.wm;
                            LogView logView = logWindow.logView;

                            if (context == activity) {
                                if (wm != null && logView != null && activity != null && (
                                        logView.isActivated() || logView.isEnabled()
                                )) {
                                    wm.removeViewImmediate(logView);
                                }
                            }
                            if (activity != null) {
                                activity.getApplication().unregisterActivityLifecycleCallbacks(this);
                            }
                        }
                    }

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {
                        // LogWindow logWindow = owf.get();
                        LogWindow logWindow = owf.get();
                        if (logWindow == null) {
                            return;
                        }
                        Context context = logWindow.context;
                        WindowManager wm = logWindow.wm;
                        LogView logView = logWindow.logView;
                        Boolean registerLifeCycleInStop = logWindow.registerLifeCycleInStop;
                        if (!registerLifeCycleInStop) {

                            if (context == activity) {
                                if (wm != null && logView != null && activity != null && activity.isFinishing() && (
                                        logView.isActivated() || logView.isEnabled()
                                )) {
                                    wm.removeViewImmediate(logView);
                                }
                            }
                            if (activity != null) {
                                activity.getApplication().unregisterActivityLifecycleCallbacks(this);
                            }
                        }
                    }
                });
            }
            logView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (KeyEvent.KEYCODE_BACK == event.getKeyCode() && event.getAction() == KeyEvent.ACTION_UP) {
                        wm.removeViewImmediate(logView);
                    }
                    return false;
                }
            });
            wm.addView(logView, params);

        }
    }
}

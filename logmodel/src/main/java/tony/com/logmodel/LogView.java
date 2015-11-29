/*
 * Copyright (C) Apache licence, Inc. All Rights Reserved.
 */
package tony.com.logmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import tony.com.logmodel.CommonUtils.LogUtils;
import tony.com.logmodel.adpater.SimpleTextAdapter;
import tony.com.logmodel.control.LogCtrl;
import tony.com.logmodel.model.LogCat;
import tony.com.logmodel.model.LogMainThread;
import tony.com.logmodel.model.LogManager;
import tony.com.logmodel.model.TraceLevel;
import tony.com.logmodel.model.TraceObject;

/**
 * Created by sanyinchen on 15/11/24.
 */
public class LogView extends LinearLayout implements LogCtrl.View {

    private Button settingButton;
    private EditText filterEditText;
    private LinearLayout bottomLayout;
    private Spinner spinner;
    private ListView mListView;
    private SimpleTextAdapter simpleTextAdapter;
    private List<TraceObject> data;
    private LogCtrl logCtrl;
    private int lastScrollPosition;
    private OnClickListener refreshClickListener;
    private LinearLayout extraSetLayout;

    private SeekBar widthSeekBar;
    private SeekBar heidthSeekBar;
    private SeekBar touchAreaSeekBar;
    private Map<String, Object> extraData;
    private ChangeWindowListener changeWindowListener;
    private LinearLayout.LayoutParams listLayoutParams;
    private LogManager logManager;
    private LogConfig logConfig;

    public LogView(Context context, Map<String, Object> extraData) {
        super(context);
        this.extraData = extraData;
        initializeView();
        hookListener();
        initializePresenter();

        // this.setFocusable(false);
    }

    public LogView setRefreshAction() {

        return this;
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public void setChangeWindowListener(ChangeWindowListener changeWindowListener) {
        this.changeWindowListener = changeWindowListener;
    }

    private void hookListener() {
        data = new ArrayList<TraceObject>();
        simpleTextAdapter = new SimpleTextAdapter(data, getContext());
        mListView.setAdapter(simpleTextAdapter);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (lastScrollPosition - firstVisibleItem != 1) {
                    lastScrollPosition = firstVisibleItem;
                }
                int lastVisiblePositionInTheList = firstVisibleItem + visibleItemCount;
                if (logCtrl != null) {
                    logCtrl.onScrollToPosition(lastVisiblePositionInTheList);
                }
            }
        });

        ArrayAdapter<TraceLevel> adapter =
                new ArrayAdapter<TraceLevel>(getContext(), android.R.layout.simple_list_item_1, TraceLevel.values());
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (logCtrl != null && position != 1) {
                    logCtrl.updateFilterTraceLevel((TraceLevel) parent.getItemAtPosition(position));
                }
                if (logCtrl != null && position == 1) {

                    logCtrl.updateFilterTraceLevel((TraceLevel) parent.getItemAtPosition(position));
                    logCtrl.updateFilter("console");
                    filterEditText.setText("console");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                logCtrl.updateFilter(s.toString().trim());
                updateView();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        settingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extraSetLayout.getVisibility() == VISIBLE) {
                    extraSetLayout.setVisibility(GONE);
                } else {
                    extraSetLayout.setVisibility(VISIBLE);
                }
            }
        });

        heidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (changeWindowListener != null) {
                    changeWindowListener.changeWindowHeight(seekBar.getProgress());
                }
            }
        });

        widthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (changeWindowListener != null) {
                    changeWindowListener.changeWindowsWidth(seekBar.getProgress());
                }
            }
        });

        touchAreaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                listLayoutParams.setMargins(0, LogUtils.diptopx(getContext(), progress), 0, 0);
                updateView();
                //                if (changeWindowListener != null) {
                //                    changeWindowListener.changeTouchArea(progress);
                //                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void updateView() {
        this.invalidate();
    }

    private void initializePresenter() {
        logManager = new LogManager(new LogCat(), new LogMainThread());
        logConfig = new LogConfig();
        logCtrl = new LogCtrl(logManager, this, logConfig);
        logCtrl.resume();
        filterEditText.setText(logConfig.getFilter());
    }

    private void initializeView() {
        this.setOrientation(VERTICAL);

        filterEditText = new EditText(getContext());
        bottomLayout = new LinearLayout(getContext());
        spinner = new Spinner(getContext());

        LinearLayout.LayoutParams refreshLayoutParams =
                new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 5);
        filterEditText.setHint("setFilter");
        filterEditText.setTextColor(Color.BLACK);
        filterEditText.setSingleLine();
        filterEditText.setGravity(Gravity.CENTER_VERTICAL);
        bottomLayout.addView(filterEditText, refreshLayoutParams);

        LinearLayout.LayoutParams spinnerLayoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 4);
        spinnerLayoutParams.setMargins(LogUtils.diptopx(getContext(), 6), 0, 0, 0);
        spinner.setGravity(Gravity.CENTER);
        bottomLayout.addView(spinner, spinnerLayoutParams);

        LinearLayout.LayoutParams refreshButtonLayoutParams =
                new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2);
        refreshButtonLayoutParams.setMargins(LogUtils.diptopx(getContext(), 6), 0, 0, 0);

        settingButton = new Button(getContext());
        settingButton.setText("setting");
        LinearLayout.LayoutParams settingButtonLayoutParams =
                new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 3);
        settingButtonLayoutParams.setMargins(LogUtils.diptopx(getContext(), 6), 0, 0, 0);
        bottomLayout.addView(settingButton, settingButtonLayoutParams);

        mListView = new ListView(getContext());
        mListView.setDivider(new ColorDrawable(Color.WHITE));
        mListView.setBackgroundDrawable(new ColorDrawable(Color.argb(200, 0, 0, 0)));
        mListView.setStackFromBottom(true);// 设置从底部开始填充

        listLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        listLayoutParams.setMargins(0, LogUtils.diptopx(getContext(), 25), 0, 0);
        this.addView(mListView, listLayoutParams);

        LinearLayout.LayoutParams bottomLayoutLayoutParams =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.addView(bottomLayout, bottomLayoutLayoutParams);

        extraSetLayout = new LinearLayout(getContext());
        extraSetLayout.setOrientation(VERTICAL);
        // 宽度进读条设置
        LinearLayout widthLayout = new LinearLayout(getContext());
        TextView widthlab = new TextView(getContext());
        widthlab.setText("控件宽度:");
        LinearLayout.LayoutParams widthParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        widthLayout.addView(widthlab, widthParams);
        //widthProgeressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
        widthSeekBar = new SeekBar(getContext());
        widthSeekBar.setMax(LogUtils.getDevDispplay(getContext())[0]);
        widthSeekBar.setProgress(LogUtils.getDevDispplay(getContext())[0]);
        LinearLayout.LayoutParams widthProgressBarParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        widthLayout.addView(widthSeekBar, widthProgressBarParams);
        extraSetLayout.addView(widthLayout);

        // 高度进度条设置
        LinearLayout heightLayout = new LinearLayout(getContext());
        TextView heigthlab = new TextView(getContext());
        heigthlab.setText("控件高度:");
        LinearLayout.LayoutParams heigthParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        heightLayout.addView(heigthlab, heigthParams);
        // heidthProgressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);

        heidthSeekBar = new SeekBar(getContext());
        heidthSeekBar.setMax(LogUtils.getDevDispplay(getContext())[1]);
        heidthSeekBar.setProgress((int) extraData.get("width"));

        LinearLayout.LayoutParams heidthProgressBarParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        heightLayout.addView(heidthSeekBar, heidthProgressBarParams);
        extraSetLayout.addView(heightLayout);

        // 触摸区域设置
        LinearLayout touchAreaLayout = new LinearLayout(getContext());
        TextView touchLab = new TextView(getContext());
        touchLab.setText("触摸区域:");
        LinearLayout.LayoutParams touchParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        touchAreaLayout.addView(touchLab, touchParams);
        // heidthProgressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);

        touchAreaSeekBar = new SeekBar(getContext());
        touchAreaSeekBar.setMax((int) extraData.get("touchAreaMax"));
        touchAreaSeekBar.setProgress((int) extraData.get("touchArea"));

        LinearLayout.LayoutParams touchAreaSeekBarParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        touchAreaLayout.addView(touchAreaSeekBar, touchAreaSeekBarParams);
        extraSetLayout.addView(touchAreaLayout);
        this.addView(extraSetLayout);

        extraSetLayout.setVisibility(GONE);
        mListView.setFocusable(false);
        mListView.setItemsCanFocus(false);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isVisible()) {
            resumePresenter();
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (changedView != this) {
            return;
        }

        if (visibility == View.VISIBLE) {
            resumePresenter();
        } else {
            pausePresenter();
        }
    }

    private boolean isVisible() {
        return getVisibility() == View.VISIBLE;
    }

    private void resumePresenter() {
        if (isPresenterReady()) {
            logCtrl.resume();
            int lastPosition = data.size() - 1;
            mListView.setSelection(lastPosition);
        }
    }

    private boolean isPresenterReady() {
        return logCtrl != null;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        pausePresenter();
        Log.d("srcomp", "View destory");
    }

    private void pausePresenter() {
        if (isPresenterReady()) {
            logCtrl.pause();
        }
    }

    @Override
    public void showTraces(List<TraceObject> traces, int removedTraces) {
        if (lastScrollPosition == 0) {
            lastScrollPosition = mListView.getFirstVisiblePosition();
        }
        if (data != null) {
            data.clear();
            data.addAll(traces);
        }
        // Log.d("srcomp", "data size: " + data.size());
        simpleTextAdapter.notifyDataSetChanged();
        updateScrollPosition(removedTraces);

    }

    private void updateScrollPosition(int removedTraces) {
        boolean shouldUpdateScrollPosition = removedTraces > 0;
        if (shouldUpdateScrollPosition) {
            int newScrollPosition = lastScrollPosition - removedTraces;
            lastScrollPosition = newScrollPosition;
            mListView.setSelectionFromTop(newScrollPosition, 0);
        }
    }

    @Override
    public void clear() {
        data.clear();
        simpleTextAdapter.notifyDataSetChanged();
    }

    @Override
    public void disableAutoScroll() {
        mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
    }

    @Override
    public void enableAutoScroll() {
        mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    }

    @Override
    public void agentRefreshAction(android.view.View.OnClickListener onClickListener) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);

    }

    public interface ChangeWindowListener {
        void changeWindowHeight(int height);

        void changeWindowsWidth(int width);

    }

}

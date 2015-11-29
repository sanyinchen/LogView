/*
 * Copyright (C) Apache licence, Inc. All Rights Reserved.
 */
package tony.com.logmodel.adpater;

import java.util.List;

import org.w3c.dom.Text;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import tony.com.logmodel.model.TraceLevel;
import tony.com.logmodel.model.TraceObject;

/**
 * Created by sanyinchen on 15/11/25.
 */
public class SimpleTextAdapter extends BaseAdapter {
    private List<TraceObject> data;
    private Context context;

    public SimpleTextAdapter(List<TraceObject> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {

            TextView textView = new TextView(context);
            textView.setTextColor(Color.WHITE);
            convertView = textView;
            viewHolder = new ViewHolder();
            viewHolder.logview = textView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // viewHolder.logview.setText(data.get(position).getDate() + " " + data.get(position).getMessage());
        TraceObject traceObject = data.get(position);
        viewHolder.logview.setText("[" + traceObject.getTraceLevel() + "]   " + traceObject.getMessage());
        viewHolder.logview.setTextColor(Color.WHITE);
        if (traceObject.getMessage().contains("[console]")) {
            viewHolder.logview.setTextColor(Color.GREEN);
        }
        if (traceObject.getTraceLevel() == TraceLevel.ERROR) {
            viewHolder.logview.setTextColor(Color.RED);
        }

        return convertView;
    }

    public static class ViewHolder {
        TextView logview;
    }
}

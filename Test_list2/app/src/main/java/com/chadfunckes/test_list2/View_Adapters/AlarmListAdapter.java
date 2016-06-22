package com.chadfunckes.test_list2.View_Adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.chadfunckes.test_list2.Models.Alarm;
import com.chadfunckes.test_list2.R;

import java.util.List;

public class AlarmListAdapter implements ListAdapter {
    private final String TAG = "ALARM LIST ADAPTER";
    private final Context _context;
    private final List<Alarm> theList;

    public AlarmListAdapter(Context context, List<Alarm> theList){
        this._context = context;
        this.theList = theList;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final Alarm thisAlarm = (Alarm) getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) _context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.alarm_list, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.alarmList);
        String min;
        if (thisAlarm.minute < 10) min = "0" + thisAlarm.minute;
        else min = String.valueOf(thisAlarm.minute);
        textView.setText(thisAlarm.month + "/" + thisAlarm.day + "/" + thisAlarm.year + " " + thisAlarm.hour + ":" + min);

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return theList.size();
    }

    @Override
    public Object getItem(int position) {
        return theList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return theList.get(position).GID;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}


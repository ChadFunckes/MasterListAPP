package com.chadfunckes.test_list2;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;

import java.text.DateFormat;
import java.util.Calendar;

public class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, min, android.text.format.DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        Alarm_Activity.alHour = hourOfDay;
        Alarm_Activity.alMinute = minute;

        Button button = (Button) getActivity().findViewById(R.id.TimeButton);
        String min;
        if (minute < 10){
            min = "0" + minute;
        }
        else
            min = String.valueOf(minute);

        button.setText(hourOfDay + ":" + min);
    }
}

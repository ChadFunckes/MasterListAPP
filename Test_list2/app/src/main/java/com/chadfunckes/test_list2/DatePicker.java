package com.chadfunckes.test_list2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;

import java.util.Calendar;

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Alarm_Activity.alYear = year;
        Alarm_Activity.alMonth = monthOfYear;
        Alarm_Activity.alDay = dayOfMonth;

        Button button = (Button) getActivity().findViewById(R.id.DateButton);
        button.setText(monthOfYear + "/" + dayOfMonth + "/" + year);
    }
}

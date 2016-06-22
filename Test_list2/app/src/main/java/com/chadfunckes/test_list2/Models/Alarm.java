package com.chadfunckes.test_list2.Models;

import java.util.Calendar;
import java.util.Comparator;

public class Alarm {
    public int AID;
    public int GID;
    public int IID;
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;

    public int getMilTime(){
        Calendar thisCal = (Calendar) Calendar.getInstance().clone();
        thisCal.set(year, month, day, hour, minute, 0);
        return (int) thisCal.getTimeInMillis();
    }

    // provides the comparator to sort by date
    public static Comparator<Alarm> ByDate = new Comparator<Alarm>(){

        @Override
        public int compare(Alarm lhs, Alarm rhs) {
            int left = lhs.getMilTime();
            int right = rhs.getMilTime();

            return left-right;
        }
    };
}

package com.chadfunckes.test_list2.Containers;

/*
This class is a group container, to hold info about the group lists
the format is like a basic structure, no specific getters and setters
are needed.
 */

public class group {
    public int _id;
    public String name;
    public double GPS_LAT;
    public double GPS_LNG;
    public String alarm;

    // default constructor does not include id and name on purpose
    public group(){
        GPS_LAT = 0;
        GPS_LNG = 0;
        alarm = "";
    }

}

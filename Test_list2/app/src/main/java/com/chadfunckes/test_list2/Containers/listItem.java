package com.chadfunckes.test_list2.Containers;

/*
This container is a basic structure that holds info about a specific list item
requires no special getters or setter or constructors.  Basically a structure.
 */

public class listItem {
    public int _id;
    public int groupID;
    public String name;
    public int finished;
    public int has_extra;
    public String notes;
    public String image;

    public listItem(){ // all ID's and name are left blank on purpose, all others to prevent nulls
        finished = 0;
        has_extra = 0;
        notes = "";
        image = "";
    }
}

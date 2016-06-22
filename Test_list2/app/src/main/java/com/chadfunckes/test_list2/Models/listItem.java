package com.chadfunckes.test_list2.Models;

/*
This container is a basic structure that holds info about a specific list item
requires no special getters or setter or constructors.  Basically a structure.
 */

import java.util.Comparator;

public class ListItem {
    public int _id;
    public int groupID;
    public String name;
    public int finished;
    public int has_extra;
    public String notes;
    public String image;

    public ListItem(){ // all ID's and name are left blank on purpose, all others to prevent nulls
        finished = 0;
        has_extra = 0;
        notes = "";
        image = "";
    }

    // comparator allows sorting of list items in order
    public static Comparator<ListItem> ByName = new Comparator<ListItem>() {
        @Override
        public int compare(ListItem lhs, ListItem rhs) {
            String left = lhs.name.toUpperCase();
            String right = rhs.name.toUpperCase();

            return left.compareTo(right);
        }
    };
}

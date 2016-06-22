package com.chadfunckes.test_list2.Models;

import java.util.Comparator;

public class Group {
    public int _id;
    public String name;

    // default constructor does not include id and name on purpose
    public Group(){
    }

    // comparator to allow sorting of the list in ABC order
    public static final Comparator<Group> ByName = new Comparator<Group>() {
        @Override
        public int compare(Group lhs, Group rhs) {

            String left = lhs.name.toUpperCase();
            String right = rhs.name.toUpperCase();

            return left.compareTo(right);
        }
    };
}

package com.chadfunckes.test_list2.Models;

import java.util.Comparator;

public class group {
    public int _id;
    public String name;

    // default constructor does not include id and name on purpose
    public group(){
    }

    // comparator to allow sorting of the list in ABC order
    public static Comparator<group> ByName = new Comparator<group>() {
        @Override
        public int compare(group lhs, group rhs) {

            String left = lhs.name.toUpperCase();
            String right = rhs.name.toUpperCase();

            return left.compareTo(right);
        }
    };
}

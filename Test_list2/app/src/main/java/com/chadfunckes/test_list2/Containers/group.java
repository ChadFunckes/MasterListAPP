package com.chadfunckes.test_list2.Containers;

import java.util.Comparator;

public class group {
    public int _id;
    public String name;

    // default constructor does not include id and name on purpose
    public group(){
    }

    public static Comparator<group> ByName = new Comparator<group>() {
        @Override
        public int compare(group lhs, group rhs) {

            String left = lhs.name.toUpperCase();
            String right = rhs.name.toUpperCase();

            return left.compareTo(right);
        }
    };
}

package net.model;

import java.util.Arrays;

/**
 * Created by stassikorskyi on 26.11.15.
 */
public class Users {

    public Users() {
    }

    public int total_count;
    public boolean incomplete_results;
    public User[] items;

    //public boolean site_admin;


    @Override
    public String toString() {
        return "Users{" +
                "total_count=" + total_count +
                ", incomplete_results=" + incomplete_results +
                ", items=" + Arrays.toString(items) +
                '}';
    }
}

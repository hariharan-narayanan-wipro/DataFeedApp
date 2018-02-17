package com.wipro.datafeedapp.com.wipro.datafeedapp.utils;

/**
 * Created by UshaHari on 17/02/18.
 */

public class StringUtils {

    /**
     * Returns whether the given string is not null and not empty
     * @param val
     * @return true if the value is not null and not empty (after trimming), false otherwise
     */
    public static boolean isValid(String val) {
        return (val != null && val.trim().length() > 0);
    }

}

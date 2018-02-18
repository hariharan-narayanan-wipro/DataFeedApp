package com.wipro.datafeedapp.com.wipro.datafeedapp.utils;

/**
 * Utility class for common String operations.
 * Created by Hariharan on 17/02/18.
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

    /**
     * Returns true if the given string is equal to "null" ignoring case.
     * @param value
     * @return
     */
    public static boolean nullValue(String value) {
        return "null".equals(value.toLowerCase().trim());
    }

}

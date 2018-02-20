package com.wipro.datafeedapp.utils;

import com.wipro.datafeedapp.app.DataFeedApp;

/**
 * Utility class for common String operations.
 * Created by Hariharan on 17/02/18.
 */

public class StringUtils {

    /**
     * Returns whether the given string is not null and not empty
     * @param val the value passed in
     * @return true if the value is not null and not empty (after trimming), false otherwise
     */
    public static boolean isValid(String val) {
        return (val != null && val.trim().length() > 0);
    }

    /**
     * Returns true if the given string is equal to "null" ignoring case.
     * @param value the value passed in
     * @return true if the passed string does not equal the string literal "null" ignoring the case, false otherwise
     */
    public static boolean notNullValue(String value) {
        if(value == null) {
            value = "null"; //return false in this case
        }
        return !"null".equals(value.toLowerCase().trim());
    }

    public static String getString(int stringResId) {
        return DataFeedApp.getInstance().getString(stringResId);
    }

}

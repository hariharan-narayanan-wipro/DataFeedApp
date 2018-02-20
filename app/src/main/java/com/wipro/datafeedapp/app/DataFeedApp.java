package com.wipro.datafeedapp.app;

import android.app.Application;

/**
 * Application extension to fetch the string resources used in i18n
 * Created by Hariharan on 20/02/18.
 */

public class DataFeedApp extends Application {

    private static DataFeedApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static DataFeedApp getInstance() {
        return instance;
    }
}

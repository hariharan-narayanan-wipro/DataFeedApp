package com.wipro.datafeedapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import java.util.Map;

/**
 * This fragment is retained during config changes such as screen rotation.
 * The stored image cache is reused after config changes
 *
 * Snippet taken from https://developer.android.com/topic/performance/graphics/cache-bitmap.html
 */

public class RetainFragment extends Fragment {
    private static final String TAG = "RetainFragment";

    public Map<String, Bitmap> mRetainedCache;

    public RetainFragment() {}

    public static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
        RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new RetainFragment();
            fm.beginTransaction().add(fragment, TAG).commit();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}

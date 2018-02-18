package com.wipro.datafeedapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ListView;
import android.widget.Toast;

import com.wipro.datafeedapp.com.wipro.datafeedapp.model.DataFeed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Broadcast notification receiver for the data feeds
 * Created by Hariharan on 18/02/18.
 */

public class FeedReceiver extends BroadcastReceiver {

    private DataFeedActivity activity;

    public FeedReceiver(DataFeedActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long start = System.currentTimeMillis();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            DataFeedService.STATUS status = (DataFeedService.STATUS) bundle.get(DataFeedService.STATUS_KEY);
            if(status == DataFeedService.STATUS.OK) {
                String title = bundle.getString(DataFeedService.TITLE);
                activity.updateTitle(title);
                Parcelable[] result = bundle.getParcelableArray(DataFeedService.RESULT);
                updateFeeds(result);
            } else {
                String errMsg = bundle.getString(DataFeedService.ERROR_MSG);
                handleError(errMsg);
            }

        } else {
            Toast.makeText(activity, "Could not get the data from the feed", Toast.LENGTH_LONG).show();
        }
        activity.setMenuState(R.id.action_refresh, true);
        activity.stopFeedService();
        Toast.makeText(activity, "Finished in " + ((System.currentTimeMillis() - start)/1000) + " seconds", Toast.LENGTH_LONG).show();
    }

    private void handleError(String errMsg) {
        activity.updateTitle(activity.getResources().getString(R.string.app_name));
        List<DataFeed> errorFeed = Arrays.asList(new DataFeed("Error while fetching data!", errMsg, DataFeed.NULL_IMAGE_REF));
        refreshData(errorFeed);
    }

    private void updateFeeds(Parcelable[] result) {
        List<DataFeed> feeds = new ArrayList<>();
        for (int i = 0; i < result.length; i++) {
            if(result[i] instanceof DataFeed) {
                DataFeed feed = (DataFeed) result[i];
                if(feed.isFeedValid()) {
                    feeds.add(feed);
                }
            }
        }
        refreshData(feeds);
    }

    private void refreshData(List<DataFeed> newFeeds) {
        ListView feedsList = activity.getFeedsList();
        feedsList.invalidate();
        DataFeedAdapter adapter = (DataFeedAdapter) feedsList.getAdapter();
        adapter.refresh(newFeeds);
    }
}

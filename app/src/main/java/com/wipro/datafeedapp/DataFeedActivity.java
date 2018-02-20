package com.wipro.datafeedapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.wipro.datafeedapp.adapter.DataFeedAdapter;
import com.wipro.datafeedapp.model.DataFeed;
import com.wipro.datafeedapp.service.DataFeedService;
import com.wipro.datafeedapp.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Main activity class. When this activity starts up the service to fetch feeds is started in the background.
 */
public class DataFeedActivity extends AppCompatActivity {

    //ListView to hold the feeds
    private ListView feedsList;

    //Main menu
    private Menu menu;

    private ProgressBar progressBar;

    private final FeedReceiver receiver = new FeedReceiver(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feeds_list);
        progressBar = findViewById(R.id.feed_progress);
        progressBar.setIndeterminate(true);
        feedsList = findViewById(R.id.feeds_list);
        showProgress(false);
        List<DataFeed> feeds = new ArrayList<>();
        DataFeedAdapter adapter = new DataFeedAdapter(this, R.layout.activity_data_feed, feeds);
        feedsList.setAdapter(adapter);
        fetchFeeds();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh) {
            item.setEnabled(false);
            fetchFeeds();
        }
        return true;
    }

    private void fetchFeeds() {
        //start the service only when the network connection is working
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager == null ? null : connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            showProgress(true);
            //start the service from a separate thread so that it does not affect the main thread in case of connection problems
            new Thread() {
                @Override
                public void run() {
                    Intent feedIntent = new Intent(DataFeedActivity.this, DataFeedService.class);
                    startService(feedIntent);
                }
            }.start();
        } else {
            DataFeed feed = new DataFeed(StringUtils.getString(R.string.no_connection), StringUtils.getString(R.string.check_connection_msg), DataFeed.NULL_IMAGE_REF);
            DataFeedAdapter adapter = (DataFeedAdapter) feedsList.getAdapter();
            adapter.refresh(Collections.singletonList(feed));
            setMenuState(R.id.action_refresh, true);
        }
    }

    public void showProgress(boolean show) {
        if(show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(DataFeedService.BROADCAST_RECEIVER_ID));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        stopFeedService();
        super.onDestroy();
    }

    /**
     * Stops the DataFeedService instance if running.
     */
    public void stopFeedService() {
        Intent feedIntent = new Intent(this, DataFeedService.class);
        stopService(feedIntent);
    }

    public ListView getFeedsList() {
        return this.feedsList;
    }

    /**
     * Sets the menu item specified by menuId to enabled or disabled state
     * @param menuId id of the menu
     * @param state true or false indicating enabled/disabled state
     */
    public void setMenuState(int menuId, boolean state) {
        if(menu == null) {
            return;
        }
        MenuItem refreshItem = menu.findItem(menuId);
        if(refreshItem != null) {
            refreshItem.setEnabled(state);
        }
    }

    /**
     * Updates the title for the action bar.
     * @param newTitle the new value for title
     */
    public void updateTitle(String newTitle) {
        if(StringUtils.isValid(newTitle)) {
            android.app.ActionBar actionBar = getActionBar();
            if(actionBar != null) {
                actionBar.setTitle(newTitle);
            } else {
                ActionBar aBar = getSupportActionBar();
                if(aBar != null) {
                    aBar.setTitle(newTitle);
                }
            }
        }
    }
}

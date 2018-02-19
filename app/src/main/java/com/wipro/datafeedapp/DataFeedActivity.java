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
import android.widget.Toast;

import com.wipro.datafeedapp.com.wipro.datafeedapp.model.DataFeed;
import com.wipro.datafeedapp.com.wipro.datafeedapp.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
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

    private FeedReceiver receiver = new FeedReceiver(this);

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
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            showProgress(true);
            Toast.makeText(this, "Fetching data...", Toast.LENGTH_LONG).show();
            //start the service from a separate thread so that it does not affect the main thread in case of connection problems
            new Thread() {
                @Override
                public void run() {
                    Intent feedIntent = new Intent(DataFeedActivity.this, DataFeedService.class);
                    startService(feedIntent);
                }
            }.start();
        } else {
            DataFeed feed = new DataFeed("No Internet Connection", "Please check your Internet connection", DataFeed.NULL_IMAGE_REF);
            DataFeedAdapter adapter = (DataFeedAdapter) feedsList.getAdapter();
            adapter.refresh(Arrays.asList(feed));
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
     * @param menuId
     * @param state
     */
    public void setMenuState(int menuId, boolean state) {
        MenuItem refreshItem = menu.findItem(menuId);
        if(refreshItem != null) {
            refreshItem.setEnabled(state);
        }
    }

    /**
     * Updates the title for the action bar.
     * @param newTitle
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

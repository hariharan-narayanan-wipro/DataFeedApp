package com.wipro.datafeedapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.wipro.datafeedapp.com.wipro.datafeedapp.model.DataFeed;
import com.wipro.datafeedapp.com.wipro.datafeedapp.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DataFeedActivity extends AppCompatActivity {

    private ListView feedsList;

    private Menu menu;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long start = System.currentTimeMillis();
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                String title = bundle.getString(DataFeedService.TITLE);
                if(StringUtils.isValid(title)) {
                    android.app.ActionBar actionBar = getActionBar();
                    if(actionBar != null) {
                        actionBar.setTitle(title);
                    } else {
                        ActionBar aBar = getSupportActionBar();
                        if(aBar != null) {
                            aBar.setTitle(title);
                        }
                    }
                }
                Parcelable[] result = bundle.getParcelableArray(DataFeedService.RESULT);
                List<DataFeed> feeds = new ArrayList<>();
                for (int i = 0; i < result.length; i++) {
                    if(result[i] instanceof DataFeed) {
                        DataFeed feed = (DataFeed) result[i];
                        if(feed.isFeedValid()) {
                            feeds.add(feed);
                        }
                    }
                }
                feedsList.invalidate();
                DataFeedAdapter adapter = (DataFeedAdapter) feedsList.getAdapter();
                adapter.refresh(feeds);
            } else {
                Toast.makeText(DataFeedActivity.this, "Could not get the data from the feed", Toast.LENGTH_LONG).show();
            }
            MenuItem refreshItem = menu.findItem(R.id.action_refresh);
            refreshItem.setEnabled(true);
            stopFeedService();
            Toast.makeText(DataFeedActivity.this, "Finished in " + ((System.currentTimeMillis() - start)/1000) + " seconds", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feeds_list);
        feedsList = findViewById(R.id.feeds_list);
        List<DataFeed> feeds = new ArrayList<>();
        RetainFragment retainFragment =
                RetainFragment.findOrCreateRetainFragment(getFragmentManager());
        DataFeedAdapter adapter = new DataFeedAdapter(retainFragment, this, R.layout.activity_data_feed, feeds);
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
        Toast.makeText(this, "Fetching data...", Toast.LENGTH_LONG).show();
        Intent feedIntent = new Intent(this, DataFeedService.class);
        startService(feedIntent);
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

    private void stopFeedService() {
        Intent feedIntent = new Intent(this, DataFeedService.class);
        stopService(feedIntent);
    }
}

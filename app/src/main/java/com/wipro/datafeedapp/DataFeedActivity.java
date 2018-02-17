package com.wipro.datafeedapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.wipro.datafeedapp.com.wipro.datafeedapp.model.DataFeed;

import java.util.ArrayList;
import java.util.List;

public class DataFeedActivity extends AppCompatActivity {

    private ListView feedsList;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Receiveddddddddddddddddddd", String.valueOf(context));
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                Parcelable[] result = bundle.getParcelableArray(DataFeedService.RESULT);
                List<DataFeed> feeds = new ArrayList<>();
                for (int i = 0; i < result.length; i++) {
                    if(result[i] instanceof DataFeed) {
                        feeds.add((DataFeed) result[i]);
                    }
                }
                DataFeedAdapter adapter = (DataFeedAdapter) feedsList.getAdapter();
                adapter.refresh(feeds);
            } else {
                Toast.makeText(DataFeedActivity.this, "Could not get the data from the feed", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
//                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
//                "Linux", "OS/2" };
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, values);
        setContentView(R.layout.feeds_list);
        feedsList = findViewById(R.id.feeds_list);
        List<DataFeed> feeds = new ArrayList<>();
        DataFeedAdapter adapter = new DataFeedAdapter(this, R.layout.activity_data_feed, feeds);
        feedsList.setAdapter(adapter);
        fetchFeeds();
        Toast.makeText(this, "Starting fetch", Toast.LENGTH_LONG).show();
    }

    private void fetchFeeds() {
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
}

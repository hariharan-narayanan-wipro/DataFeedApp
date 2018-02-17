package com.wipro.datafeedapp;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.wipro.datafeedapp.com.wipro.datafeedapp.model.DataFeed;
import com.wipro.datafeedapp.com.wipro.datafeedapp.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hariharan on 17/02/18.
 */

public class DataFeedService extends IntentService {

    private static final String FETCH_URL = "https://dl.dropboxusercontent.com/s/2iodh4vg0eortkl/facts.json";

    private static final String ROWS = "rows";

    public static final String TITLE = "title";

    private static final String DESCRIPTION = "description";

    private static final String HREF = "imageHref";

    public static final String BROADCAST_RECEIVER_ID = "com.wipro.datafeedapp.feed.receiver";

    public static final String RESULT = "RESULT";


    public DataFeedService() {
        this("Data Feed Service");
    }

    public DataFeedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        InputStream stream = null;
        FileOutputStream fos = null;
        List<DataFeed> feeds = new ArrayList<>();
        String feedTitle = "";
        try {

            URL url = new URL(FETCH_URL);
            stream = url.openConnection().getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            StringBuilder jsonStr = new StringBuilder();

            int next = -1;
            while ((next = reader.read()) != -1) {
                jsonStr.append((char) next);
            }
            JSONObject data = new JSONObject(jsonStr.toString());
            feedTitle = data.getString(TITLE);
            JSONArray feedsArr = data.getJSONArray(ROWS);
            for (int i = 0; i < feedsArr.length(); i++) {
                JSONObject obj = feedsArr.getJSONObject(i);
                String title = obj.getString(TITLE);
                String desc = obj.getString(DESCRIPTION);
                String imgHref = obj.getString(HREF);
                feeds.add(new DataFeed(title, desc, imgHref));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(!StringUtils.isValid(feedTitle)) {
            feedTitle = FETCH_URL;
        }
        broadcastResult(feedTitle, feeds);
    }

    private void broadcastResult(String feedTitle, List<DataFeed> feeds) {
        Intent intent = new Intent(BROADCAST_RECEIVER_ID);
        intent.putExtra(TITLE, feedTitle);
        intent.putExtra(RESULT, feeds.toArray(new DataFeed[0]));
        sendBroadcast(intent);
    }
}

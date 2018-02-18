package com.wipro.datafeedapp;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.wipro.datafeedapp.com.wipro.datafeedapp.model.DataFeed;
import com.wipro.datafeedapp.com.wipro.datafeedapp.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class to fetch the JSON data from the URL
 * Created by Hariharan on 17/02/18.
 */

public class DataFeedService extends IntentService {

//    private static final String FETCH_URL = "https://dl.dropboxusercontent.com/s/2iodh4vg0eortkl/facts.json";

    private static final String FETCH_URL = "http://192.168.1.6:8080/jsondata";

    private static final String ROWS = "rows";

    public static final String TITLE = "title";

    private static final String DESCRIPTION = "description";

    private static final String HREF = "imageHref";

    public static final String BROADCAST_RECEIVER_ID = "com.wipro.datafeedapp.feed.receiver";

    public static final String RESULT = "RESULT";

    public static final String STATUS_KEY = "STATUS_KEY";

    public static final String ERROR_MSG = "ERROR_MSG";

    public enum STATUS {OK, ERROR};


    public DataFeedService() {
        this("Data Feed Service");
    }

    public DataFeedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        List<DataFeed> feeds = new ArrayList<>();
        String feedTitle = "";
        STATUS status = STATUS.OK;
        String errorMsg = null;
        try {
            String jsonData = readJsonFromUrl();
            Map<String, List<DataFeed>> resultData = parseFeeds(jsonData);
            Map.Entry<String, List<DataFeed>> data = resultData.entrySet().iterator().next();
            feedTitle = data.getKey();
            feeds = data.getValue();

        } catch (Exception e) {
            status = STATUS.ERROR;
            errorMsg = e.getMessage();
        }
        broadcastResult(feedTitle, feeds, status, errorMsg);
    }

    //fetch the json data from the url
    private String readJsonFromUrl() throws Exception {
        InputStream stream = null;
        InputStreamReader reader = null;
        try {
            stream = new HttpHandler(FETCH_URL).getInputStream();
            reader = new InputStreamReader(stream);
            StringBuilder jsonStr = new StringBuilder();
            int next = -1;
            while ((next = reader.read()) != -1) {
                jsonStr.append((char) next);
            }
            return jsonStr.toString();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    //do nothing
                }
            } if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //do nothing
                }
            }
        }
    }

    // parse the feed json and create DataFeed objects.Put it against the title as the key in the map
    // and return the result
    private Map<String, List<DataFeed>> parseFeeds(String jsonStr) throws Exception {
        JSONObject data = new JSONObject(jsonStr.toString());
        String feedTitle = data.getString(TITLE);
        if(!StringUtils.isValid(feedTitle)) {
            feedTitle = FETCH_URL;
        }
        List<DataFeed> allFeeds = new ArrayList<>();
        JSONArray feedsArr = data.getJSONArray(ROWS);
        for (int i = 0; i < feedsArr.length(); i++) {
            JSONObject obj = feedsArr.getJSONObject(i);
            String title = obj.getString(TITLE);
            String desc = obj.getString(DESCRIPTION);
            String imgHref = obj.getString(HREF);
            allFeeds.add(new DataFeed(title, desc, imgHref));
        }
        Map<String, List<DataFeed>> result = new HashMap<>();
        result.put(feedTitle, allFeeds);
        return result;
    }

    //send out the results after completion
    private void broadcastResult(String feedTitle, List<DataFeed> feeds, STATUS status, String error) {
        Intent intent = new Intent(BROADCAST_RECEIVER_ID);
        intent.putExtra(TITLE, feedTitle);
        intent.putExtra(RESULT, feeds.toArray(new DataFeed[0]));
        intent.putExtra(STATUS_KEY, status);
        if(status != STATUS.OK) {
            intent.putExtra(ERROR_MSG, error);
        }
        sendBroadcast(intent);
    }

}

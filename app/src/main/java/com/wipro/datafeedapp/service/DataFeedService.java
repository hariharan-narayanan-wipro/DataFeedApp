package com.wipro.datafeedapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.wipro.datafeedapp.HttpHandler;
import com.wipro.datafeedapp.R;
import com.wipro.datafeedapp.model.DataFeed;
import com.wipro.datafeedapp.utils.StringUtils;

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

    private static String FETCH_URL = "https://dl.dropboxusercontent.com/s/2iodh4vg0eortkl/facts.json";

    private static final String ROWS = "rows";

    public static final String TITLE = "title";

    private static final String DESCRIPTION = "description";

    private static final String HREF = "imageHref";

    public static final String BROADCAST_RECEIVER_ID = "com.wipro.datafeedapp.feed.receiver";

    public static final String RESULT = "RESULT";

    public static final String STATUS_KEY = "STATUS_KEY";

    public static final String ERROR_MSG = "ERROR_MSG";

    private static final String NULL_JSON_MSG = StringUtils.getString(R.string.null_json_msg);

    private static final Exception INVALID_JSON_DATA = new Exception(StringUtils.getString(R.string.invalid_json_msg));

    private static final String NO_DATA_AVAILABLE = StringUtils.getString(R.string.no_data_available);

    public enum STATUS {OK, ERROR};


    public DataFeedService() {
        this(StringUtils.getString(R.string.feed_service_name));
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
            if(!StringUtils.isValid(jsonData)) {
                status = STATUS.ERROR;
                errorMsg = NULL_JSON_MSG;
            } else {
                Map<String, List<DataFeed>> resultData = parseFeeds(jsonData);
                Map.Entry<String, List<DataFeed>> data = resultData.entrySet().iterator().next();
                feedTitle = data.getKey();
                feeds = data.getValue();
            }

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
            if(stream == null) {
                return null;
            }
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
        String feedTitle = null;
        if(data.has(TITLE)) {
            feedTitle = data.getString(TITLE);
        } else {
            throw new Exception(StringUtils.getString(R.string.title_not_found), INVALID_JSON_DATA);
        }
        if(!StringUtils.isValid(feedTitle)) {
            feedTitle = FETCH_URL;
        }
        List<DataFeed> allFeeds = new ArrayList<>();
        if(!data.has(ROWS)) {
            throw new Exception(StringUtils.getString(R.string.rows_not_found), INVALID_JSON_DATA);
        }
        JSONArray feedsArr = data.getJSONArray(ROWS);
        if(feedsArr.length() == 0) {
            allFeeds.add(new DataFeed( NO_DATA_AVAILABLE, StringUtils.getString(R.string.no_feeds_msg), DataFeed.NULL_IMAGE_REF));
        } else {
            for (int i = 0; i < feedsArr.length(); i++) {
                JSONObject obj = feedsArr.getJSONObject(i);
                if(obj.has(TITLE) && obj.has(DESCRIPTION) && obj.has(HREF)) {
                    String title = obj.getString(TITLE);
                    String desc = obj.getString(DESCRIPTION);
                    String imgHref = obj.getString(HREF);
                    allFeeds.add(new DataFeed(title, desc, imgHref));
                }
            }
            //check whether all the rows are invalid
            if(allFeeds.size() == 0) {
                throw INVALID_JSON_DATA;
            }
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

    /**
     * This method is purely written for testing purposes. Not to be used elsewhere
     * @param url
     */
    public static void setFetchUrl(String url) {
        if(url == null) {
            url = "http://abc.com"; //dummy url
        }
        FETCH_URL = url;
    }

}

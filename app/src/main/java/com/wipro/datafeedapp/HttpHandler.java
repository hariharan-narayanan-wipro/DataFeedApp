package com.wipro.datafeedapp;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class to deal with HTTP Urls
 * Created by Hariharan on 18/02/18.
 */

public class HttpHandler {

    //Http header key for location
    private static final String LOCATION_HEADER = "Location";

    private String urlString;

    public HttpHandler(String urlString) {
        this.urlString = urlString;
    }

    public InputStream getInputStream() throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        int respCode = conn.getResponseCode();
        //check if redirected to another page
        if(respCode == HttpURLConnection.HTTP_MOVED_PERM ||
                respCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                respCode == HttpURLConnection.HTTP_SEE_OTHER) {
            String newUrl = conn.getHeaderField(LOCATION_HEADER);
            conn = (HttpURLConnection) new URL(newUrl).openConnection();
        }
        return conn.getInputStream();
    }
}

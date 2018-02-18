package com.wipro.datafeedapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by UshaHari on 17/02/18.
 */

public class ImageDownloadTask extends AsyncTask<Void, Void, Bitmap> {

    private DataFeedAdapter adapter;

    private ImageView imgView;

    private String imgUrl;

    public ImageDownloadTask(DataFeedAdapter adapter, ImageView imgView, String imgUrl) {
        this.adapter = adapter;
        this.imgView = imgView;
        this.imgUrl = imgUrl;
    }

    @Override
    protected Bitmap doInBackground(Void... none) {
        try {
            InputStream stream = getInputStream();
            if(stream == null) {
                return null;
            }
            Bitmap bmp = BitmapFactory.decodeStream(stream);
            return bmp;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private InputStream getInputStream() throws Exception {
        URL url = new URL(imgUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        int respCode = conn.getResponseCode();
        //check if redirected to another page
        if(respCode == HttpURLConnection.HTTP_MOVED_PERM ||
                respCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                respCode == HttpURLConnection.HTTP_SEE_OTHER) {
            String newUrl = conn.getHeaderField("Location");
            conn = (HttpURLConnection) new URL(newUrl).openConnection();
        }
        try {
            return conn.getInputStream();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap image) {
        adapter.updateImageView(imgView, image, imgUrl);
    }
}

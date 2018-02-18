package com.wipro.datafeedapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Background task class for image downloading. This is used for the first time and then the image
 * is cached in memory.
 * Created by Hariharan on 17/02/18.
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
        InputStream stream = null;
        try {
            stream = new HttpHandler(imgUrl).getInputStream();
            if(stream == null) {
                return null;
            }
            Bitmap bmp = BitmapFactory.decodeStream(stream);
            return bmp;
        } catch(Exception ex) {
//            ex.printStackTrace();
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    //do nothing
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap image) {
        adapter.updateImageView(imgView, image, imgUrl);
    }
}

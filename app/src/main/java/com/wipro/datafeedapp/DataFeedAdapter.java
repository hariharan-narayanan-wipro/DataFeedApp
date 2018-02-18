package com.wipro.datafeedapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wipro.datafeedapp.com.wipro.datafeedapp.model.DataFeed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom Adapter class for the Data Feed App
 * Created by Hariharan on 17/02/18.
 */

public class DataFeedAdapter extends ArrayAdapter<DataFeed> {

    private Map<String, Bitmap> imageCache;

    private Bitmap noImgBitmap;

    public DataFeedAdapter(RetainFragment fragment, @NonNull Context context, int resource, @NonNull List<DataFeed> objects) {
        super(context, resource, objects);
        this.imageCache = fragment.mRetainedCache;
        if(this.imageCache == null) {
            this.imageCache = new HashMap<>();
            fragment.mRetainedCache = this.imageCache;
        }
        try {
            //load the no image bitmap once
            this.noImgBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image);
        } catch (Exception e) {
            //do nothing
        }
    }

    public void refresh(List<DataFeed> newData) {
        clear();
        addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        DataFeed feed = getItem(position);

        View dataFeedView = inflater.inflate(R.layout.activity_data_feed, parent, false);
        TextView titleView = dataFeedView.findViewById(R.id.titleView);
        titleView.setText(feed.getTitle());
        TextView descView = dataFeedView.findViewById(R.id.descriptionView);
        descView.setText(feed.getDescription());
        ImageView imgView = dataFeedView.findViewById(R.id.imageView);
        String imgUrl = feed.getImageHref();
        Bitmap image = imageCache.get(imgUrl);
        if(image != null) {
            imgView.setImageBitmap(image);
        } else {
            ImageDownloadTask task = new ImageDownloadTask(this, imgView, imgUrl);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        return dataFeedView;
    }

    public void updateImageView(ImageView imgView, Bitmap img, String url) {
        if(img == null) {
            img = this.noImgBitmap;
        }
        imageCache.put(url, img);
        imgView.setImageBitmap(img);
    }


}

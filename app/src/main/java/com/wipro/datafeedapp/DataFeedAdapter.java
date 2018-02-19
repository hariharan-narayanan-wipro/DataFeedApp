package com.wipro.datafeedapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Custom Adapter class for the Data Feed App
 * Created by Hariharan on 17/02/18.
 */

public class DataFeedAdapter extends ArrayAdapter<DataFeed> {

    //Map to cache the images.
    private Map<String, Bitmap> imageCache;

    private Executor executor = Executors.newFixedThreadPool(50);

    //Bitmap image to display when the actual image could not be fetched.
    private Bitmap noImgBitmap;

    public DataFeedAdapter(@NonNull Context context, int resource, @NonNull List<DataFeed> objects) {
        super(context, resource, objects);
//        this.imageCache = fragment.mRetainedCache;
        if(this.imageCache == null) {
            this.imageCache = new HashMap<>();
            imageCache.put(DataFeed.NULL_IMAGE_REF, null);
        }
        try {
            //load the no image bitmap once
            this.noImgBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image);
        } catch (Exception e) {
            //do nothing
        }
    }

    /**
     * Refreshes the list view with the new set of data
     * @param newData
     */
    public void refresh(List<DataFeed> newData) {
        clear();
        addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View dataFeedView = convertView;
        DataFeed feed = getItem(position);

        //create a new view if the old view is null
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            dataFeedView = inflater.inflate(R.layout.activity_data_feed, parent, false);
        }

        TextView titleView = dataFeedView.findViewById(R.id.titleView);
        titleView.setText(feed.getTitle());
        TextView descView = dataFeedView.findViewById(R.id.descriptionView);
        descView.setText(feed.getDescription());
        ImageView imgView = dataFeedView.findViewById(R.id.imageView);
        String imgUrl = feed.getImageHref();
        Bitmap image = imageCache.get(imgUrl);
        if (DataFeed.NULL_IMAGE_REF.equals(imgUrl)) {
            imgView.setImageBitmap(image);
        } else if (image != null) {
            imgView.setImageBitmap(image);
        } else {
            ImageDownloadTask task = new ImageDownloadTask(this, imgView, imgUrl);
            task.executeOnExecutor(executor);
        }

        return dataFeedView;
    }

    /**
     * Method where the image is added to the map and set to the view for the first time.
     * @param imgView
     * @param img
     * @param url
     */
    public synchronized void updateImageView(ImageView imgView, Bitmap img, String url) {
        if(img == null) {
            img = this.noImgBitmap;
        }
        imageCache.put(url, img);
        imgView.setImageBitmap(img);
    }


}



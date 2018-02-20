package com.wipro.datafeedapp.adapter;

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

import com.wipro.datafeedapp.task.ImageDownloadTask;
import com.wipro.datafeedapp.R;
import com.wipro.datafeedapp.model.DataFeed;

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

    private final Executor executor = Executors.newFixedThreadPool(50);

    //Bitmap image to display when the actual image could not be fetched.
    private Bitmap noImgBitmap;

    public DataFeedAdapter(@NonNull Context context, int resource, @NonNull List<DataFeed> objects) {
        super(context, resource, objects);
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
     * @param newData new set of data to be used
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
        if(feed == null) {
            feed = new DataFeed();
        }
        FeedViewHolder viewHolder;

        //create a new view if the old view is null
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //noinspection ConstantConditions
            dataFeedView = inflater.inflate(R.layout.activity_data_feed, parent, false);
            viewHolder = new FeedViewHolder();
            viewHolder.setTitleView((TextView) dataFeedView.findViewById(R.id.titleView));
            viewHolder.setDescriptionView((TextView) dataFeedView.findViewById(R.id.descriptionView));
            viewHolder.setImageView((ImageView) dataFeedView.findViewById(R.id.imageView));
            dataFeedView.setTag(viewHolder);
        } else {
            viewHolder = (FeedViewHolder) dataFeedView.getTag();
        }

        viewHolder.getTitleView().setText(feed.getTitle());
        viewHolder.getDescriptionView().setText(feed.getDescription());
        ImageView imgView = viewHolder.getImageView();
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
     * @param imgView ImageView instance
     * @param img Bitmap image instance
     * @param url URL string for the image
     */
    public synchronized void updateImageView(ImageView imgView, Bitmap img, String url) {
        if(img == null) {
            img = this.noImgBitmap;
        }
        imageCache.put(url, img);
        imgView.setImageBitmap(img);
    }


}



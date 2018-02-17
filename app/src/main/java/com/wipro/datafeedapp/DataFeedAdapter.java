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

import java.net.URL;
import java.util.List;

/**
 * Custom Adapter class for the Data Feed App
 * Created by Hariharan on 17/02/18.
 */

public class DataFeedAdapter extends ArrayAdapter<DataFeed> {


    public DataFeedAdapter(@NonNull Context context, int resource, @NonNull List<DataFeed> objects) {
        super(context, resource, objects);
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
        try {
            URL url = new URL(feed.getImageHref());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imgView.setImageBitmap(null);
            imgView.setImageBitmap(bmp);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
//        imgView.setImageURI(Uri.parse(feed.getImageHref()));

        return dataFeedView;

    }
}

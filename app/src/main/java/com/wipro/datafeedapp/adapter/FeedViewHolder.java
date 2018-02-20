package com.wipro.datafeedapp.adapter;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * View holder class for the feeds in the adapter
 * Created by Hariharan on 20/02/18.
 */

public class FeedViewHolder {

    private TextView titleView;

    private TextView descriptionView;

    private ImageView imageView;

    public TextView getTitleView() {
        return titleView;
    }

    public void setTitleView(TextView titleView) {
        this.titleView = titleView;
    }

    public TextView getDescriptionView() {
        return descriptionView;
    }

    public void setDescriptionView(TextView descriptionView) {
        this.descriptionView = descriptionView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}

package com.wipro.datafeedapp.model;

/**
 * Model for the data feed.
 *
 **/

import android.os.Parcel;
import android.os.Parcelable;

import com.wipro.datafeedapp.R;
import com.wipro.datafeedapp.utils.StringUtils;

import static com.wipro.datafeedapp.utils.StringUtils.isValid;
import static com.wipro.datafeedapp.utils.StringUtils.nullValue;


public class DataFeed implements Parcelable {


    /**
     * Default values
     */
    private static final String DEFAULT_TITLE = StringUtils.getString(R.string.default_title);

    private static final String DEFAULT_DESC = StringUtils.getString(R.string.default_description);

    private static final String DEFUALT_HREF = StringUtils.getString(R.string.default_href);

    /**
     * This key is used to store a bitmap which is null in the data feed adapter.
     * Used during errors.
     */
    public static final String NULL_IMAGE_REF = "nullImage";

    private String title;

    private String description;

    private String imageHref;


    /**
     * CREATOR for data feed
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DataFeed createFromParcel(Parcel parcel) {
            return new DataFeed(parcel);
        }

        public DataFeed[] newArray(int size) {
            return new DataFeed[size];
        }
    };

    public DataFeed() {
        this(null, null, null);
    }

    public DataFeed(String title, String description, String imageHref) {
        setTitle(title);
        setDescription(description);
        setImageHref(imageHref);
    }

    public DataFeed(Parcel parcel) {
        setTitle(parcel.readString());
        setDescription(parcel.readString());
        setImageHref(parcel.readString());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = isValid(title) && !nullValue(title) ? title : DEFAULT_TITLE;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = isValid(description) && !nullValue(description) ? description : DEFAULT_DESC;
    }

    public String getImageHref() {
        return imageHref;
    }

    public void setImageHref(String imageHref) {
        this.imageHref = isValid(imageHref) && !nullValue(imageHref) ? imageHref : DEFUALT_HREF;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int falgs) {
        parcel.writeString(this.getTitle());
        parcel.writeString(this.getDescription());
        parcel.writeString(this.getImageHref());
    }

    public boolean isFeedValid() {
        return !(title.trim().equals(DEFAULT_TITLE) &&
                description.trim().equals(DEFAULT_DESC) &&
                imageHref.trim().equals(DEFUALT_HREF));
    }
}
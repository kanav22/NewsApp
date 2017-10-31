package com.example.android.newsListing;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kanav on 27/09/17.
 */

public class News implements Parcelable {

    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        public News[] newArray(int size) {
            return new News[size];
        }
    };
    private String mTitle;
    private String mSectionName;
    private String mAuthor;
    private String mDate;
    private String mUrl;

    public News(String title, String author, String url, String date, String sectionName) {
        mTitle = title;
        mSectionName = sectionName;
        mAuthor = author;
        mDate = date;
        mUrl = url;

    }

    private News(Parcel in) {
        mTitle = in.readString();
        mSectionName = in.readString();
        mAuthor = in.readString();

    }

    @Override
    public String toString() {
        return "Title :" + mTitle +
                "Section name" + mSectionName + mAuthor + mDate;
    }

    public int describeContents() {
        return 0;
    }

    public String getTitle() {
        return mTitle;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mTitle);
        out.writeString(mSectionName);
        out.writeString(mAuthor);
        out.writeString(mDate);
        out.writeString(mUrl);
    }

    public String getSectionName() {
        return mSectionName;

    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getDate() {
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }
}

package com.example.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Reviews implements Parcelable {

    public static final String PARCELABLE_KEY = "parcelable_key";
    public final static Creator<Reviews> CREATOR = new Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel parcel) {
            return new Reviews(parcel);
        }

        @Override
        public Reviews[] newArray(int i) {
            return new Reviews[i];
        }

    };
    private String Author;
    private String Content;

    public Reviews(String author, String content) {
        Author = author;
        Content = content;
    }

    private Reviews(Parcel in) {
        Author = in.readString();
        Content = in.readString();
    }

    public String getAuthor() {
        return Author;
    }

    public String getText() {
        return Content;
    }

    @Override
    public String toString() {
        return "Review{" +
                "Author='" + Author + '\'' +
                "Content='" + Content + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Author);
        parcel.writeString(Content);
    }
}

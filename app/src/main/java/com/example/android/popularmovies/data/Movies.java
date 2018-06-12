package com.example.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Movies implements Parcelable {

    public static final String PARCELABLE_KEY = "parcelable_key";
    public final static Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel parcel) {
            return new Movies(parcel);
        }

        @Override
        public Movies[] newArray(int i) {
            return new Movies[i];
        }

    };
    private String Title;
    private String PosterPath;
    private String PublishedDate;
    private double Average;
    private String Synopsis;

    public Movies(String title, String posterPath, String publishedDate, double average, String synopsis) {
        Title = title;
        PosterPath = posterPath;
        PublishedDate = publishedDate;
        Average = average;
        Synopsis = synopsis;
    }

    private Movies(Parcel in) {
        Title = in.readString();
        PosterPath = in.readString();
        PublishedDate = in.readString();
        Average = in.readDouble();
        Synopsis = in.readString();
    }

    public String getTitle() {
        return Title;
    }

    public String getPosterPath() {
        return PosterPath;
    }

    public String getPublishedDate() {
        return PublishedDate;
    }

    public Double getAverage() {
        return Average;
    }

    public String getSynopsis() {
        return Synopsis;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "Titile='" + Title + '\'' +
                ", PosterPath='" + PosterPath + '\'' +
                ", PublishedDate='" + PublishedDate + '\'' +
                ", Average='" + Average + '\'' +
                ", Synopsis='" + Synopsis +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Title);
        parcel.writeString(PosterPath);
        parcel.writeString(PublishedDate);
        parcel.writeDouble(Average);
        parcel.writeString(Synopsis);
    }
}

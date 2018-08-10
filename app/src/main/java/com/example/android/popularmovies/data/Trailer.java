package com.example.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

public class Trailer implements Parcelable {

    public static final String PARCELABLE_KEY = "parcelable_key";
    public final static Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel parcel) {
            return new Trailer(parcel);
        }

        @Override
        public Trailer[] newArray(int i) {
            return new Trailer[i];
        }

    };

    private String Name;
    private String Key;
    private String Type;


    public Trailer(String name, String key, String type) {
        Name = name;
        Key = key;
        Type = type;
    }

    private Trailer(Parcel in) {
        Name = in.readString();
        Key = in.readString();
        Type = in.readString();
    }

    public String getName() {
        return Name;
    }

    public String getKey() {
        return Key;
    }

    public String getType() {
        return Type;
    }

    @Override
    public String toString() {
        return "Review{" +
                "Name='" + Name + '\'' +
                "Key='" + Key + '\'' +
                "Type='" + Type + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Name);
        parcel.writeString(Key);
        parcel.writeString(Type);
    }
}

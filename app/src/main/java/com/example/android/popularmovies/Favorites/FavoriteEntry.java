package com.example.android.popularmovies.Favorites;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.popularmovies.data.Movies;

@Entity(tableName = "favorites")
public class FavoriteEntry {

    @PrimaryKey (autoGenerate = true)
    private int id;
    private String movieId;
    private String title;
    private String posterPath;
    private String publishedDate;
    private double average;
    private String synopsis;

    @Ignore
    public FavoriteEntry(String movieId, String title, String posterPath, String publishedDate, double average, String synopsis){
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
        this.publishedDate = publishedDate;
        this.average = average;
        this.synopsis = synopsis;
    }

    public FavoriteEntry(int id, String movieId, String title, String posterPath, String publishedDate, double average, String synopsis){
        this.id = id;
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
        this.publishedDate = publishedDate;
        this.average = average;
        this.synopsis = synopsis;
    }


    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getMovieId(){
        return movieId;
    }

    public void setMovieId(String movieId){
        this.movieId = movieId;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getPosterPath(){
        return  posterPath;
    }

    public void setPosterPath(String posterPath){
        this.posterPath = posterPath;
    }

    public String getPublishedDate(){
        return  publishedDate;
    }

    public void setPublishedDate(String publishedDate){
        this.publishedDate = publishedDate;
    }

    public double getAverage(){
        return average;
    }

    public void setAverage(double average){
        this.average = average;
    }

    public String getSynopsis(){
        return synopsis;
    }

    public void setSynopsis(String synopsis){
        this.synopsis = synopsis;
    }
}

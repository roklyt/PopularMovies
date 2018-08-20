package com.example.android.popularmovies.Favorites;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "favorites")
public class FavoriteEntry {

    @PrimaryKey (autoGenerate = true)
    private int id;
    private String movieId;
    private String title;

    @Ignore
    public FavoriteEntry(String movieId, String title){
        this.movieId = movieId;
        this.title = title;
    }

    public  FavoriteEntry(int id, String movieId, String title){
        this.id = id;
        this.movieId = movieId;
        this.title = title;
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
}

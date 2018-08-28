package com.example.android.popularmovies.Favorites;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY title")
    LiveData<List<FavoriteEntry>> loadAllFavorites();

    @Insert
    void insertFavorite(FavoriteEntry favoriteEntity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavorite(FavoriteEntry favoriteEntity);

    @Delete
    void deleteFavorite(FavoriteEntry favoriteEntity);

    @Query("SELECT * FROM favorites WHERE movieId = :movieId")
    LiveData<FavoriteEntry> loadFavoriteByMovieId(String movieId);


}

package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.popularmovies.Favorites.AppDatabase;
import com.example.android.popularmovies.Favorites.FavoriteEntry;

public class AddFavoritesViewModel extends ViewModel {

    private LiveData<FavoriteEntry> favorite;

    public AddFavoritesViewModel(AppDatabase database, String movieId){
        favorite = database.favoriteDao().loadFavoriteByMovieId(movieId);
    }

    public LiveData<FavoriteEntry> getFavorite(){
        return favorite;
    }

}

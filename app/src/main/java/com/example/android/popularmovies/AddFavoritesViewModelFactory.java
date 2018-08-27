package com.example.android.popularmovies;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.Favorites.AppDatabase;

public class AddFavoritesViewModelFactory extends ViewModelProvider.NewInstanceFactory{

    private final AppDatabase FavoritesDb;
    private final String FavoriteMovieId;

    public AddFavoritesViewModelFactory(AppDatabase database, String favoriteMovieId){
        FavoritesDb = database;
        FavoriteMovieId = favoriteMovieId;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddFavoritesViewModel(FavoritesDb, FavoriteMovieId);
    }
}

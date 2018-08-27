package com.example.android.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmovies.Favorites.AppDatabase;
import com.example.android.popularmovies.Favorites.FavoriteEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<FavoriteEntry>> favorites;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase databse = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the database.");
        favorites = databse.favoriteDao().loadAllFavorites();
    }

    public LiveData<List<FavoriteEntry>> getFavorites() {
        return favorites;
    }
}

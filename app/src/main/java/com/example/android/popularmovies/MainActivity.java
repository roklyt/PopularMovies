package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Adapter.MovieAdapter;
import com.example.android.popularmovies.Favorites.AppDatabase;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.data.Movies;
import com.example.android.popularmovies.utilities.MovieJsonUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements com.example.android.popularmovies.Adapter.MovieAdapter.MovieAdapterOnClickHandler {

    /* Recycleview adapter*/
    private MovieAdapter MovieAdapter;
    /* List of all movies*/
    private List<Movies> MoviesList = new ArrayList<>();
    /* recyclerview to populate all movies*/
    private RecyclerView RecyclerView;
    /* Error text view*/
    private TextView ErrorMessageDisplay;
    /* Progress bar as indicator */
    private ProgressBar LoadingIndicator;

    private AppDatabase FavoritesDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* find all views */
        ErrorMessageDisplay = findViewById(R.id.error_message);

        RecyclerView = findViewById(R.id.recyclerview_movies);

        LoadingIndicator = findViewById(R.id.pb_loading_indicator);

        /* set grid layout manager to the recyclerview */
        RecyclerView.setLayoutManager(new GridLayoutManager(this, calculateSpan()));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_key),
                getString(R.string.settings_order_default));

        if(orderBy.equals(R.string.settings_order_favorites_value)){
            FavoritesDb = AppDatabase.getInstance(getApplicationContext());


        }else{
            /* Add the Movie adapter to the recyclerview.*/
            MovieAdapter = new MovieAdapter(this, MoviesList);
            RecyclerView.setAdapter(MovieAdapter);

            /* If network is available proceed else show error message */
            if (checkNetwork()) {
                loadMovieData();
            } else {
                Toast.makeText(this, getString(R.string.error_no_network), Toast.LENGTH_LONG).show();
                showErrorMessage();
            }

        }
    }

    /* Calculate how many thumbnails fit on the screen */
    private int calculateSpan() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        return width / NetworkUtils.W_185_VALUE;
    }

    /* Check network */
    private boolean checkNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        ErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        RecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        RecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        ErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void loadMovieData() {
        /* Get shared preferences and pas them to the async task*/
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_key),
                getString(R.string.settings_order_default));

        String apiKey = sharedPrefs.getString(
                getString(R.string.settings_api_key_key), "");

        new FetchMoviesTask().execute(orderBy, apiKey);
    }

    /* On click listener to make an intent for the selected movie to the details activity */
    @Override
    public void onClick(Movies currentMovie) {
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(this, destinationClass);
        intentToStartDetailActivity.putExtra(Movies.PARCELABLE_KEY, currentMovie);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /* start the settings activity if the user click the settings symbol. The if is not necessary here because we have only one button but i implemented it for further use */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // COMPLETED (6) Move the logic into the run method and
                // Extract the list of tasks to a final variable
                final List<FavoritesEntry> favorites = FavoritesDb.favoriteDao().loadAllFavorites();
                // COMPLETED (7) Wrap the setTask call in a call to runOnUiThread
                // We will be able to simplify this once we learn more
                // about Android Architecture Components
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //MovieAdapter.setMovieData(favorites);
                    }
                });
            }
        });
    }


    /* Async Task to make an url request against the tmdb to get the movie list*/
    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movies>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movies> doInBackground(String... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String searchFor = params[0];
            String apiKey = params[1];
            /* build the url */
            URL movieRequestUrl = NetworkUtils.buildUrl(apiKey, searchFor);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                /* If there is an status object something went wrong. We pas here the error message into our error text view */
                JSONObject movieJson = new JSONObject(jsonMovieResponse);
                if (movieJson.has(MovieJsonUtils.MOVIE_STATUS_CODE)) {
                    ErrorMessageDisplay.setText(movieJson.getString(MovieJsonUtils.MOVIE_STATUS_MESSGAE));
                    showErrorMessage();
                    return null;
                }
                /* Everything is fine we can parse the json to get or movies*/
                List<Movies> movieDataList = MovieJsonUtils
                        .getMovieListFromJson(jsonMovieResponse);

                return movieDataList;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movies> movieData) {
            /* Hide the loading bar */
            LoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                showMovieDataView();
                /* set the new data to the adapter */
                MovieAdapter.setMovieData(movieData);
            } else {
                showErrorMessage();
            }
        }
    }
}

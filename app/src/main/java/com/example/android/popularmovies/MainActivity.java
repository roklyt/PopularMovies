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

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.DetailActivity;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movies;
import com.example.android.popularmovies.utilities.MovieJsonUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{

    private MovieAdapter MovieAdapter;

    private List<Movies> MoviesList =new ArrayList<>();

    private RecyclerView RecyclerView;

    private TextView ErrorMessageDisplay;

    private ProgressBar LoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ErrorMessageDisplay = findViewById(R.id.error_message);

        RecyclerView = findViewById(R.id.recyclerview_movies);

        LoadingIndicator = findViewById(R.id.pb_loading_indicator);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int spanCount = width / 185;

        RecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        MovieAdapter = new MovieAdapter(this, MoviesList);
        RecyclerView.setAdapter(MovieAdapter);

        if(checkNetwork()){
            loadMovieData();
        }else{
            Toast.makeText(this, getString(R.string.error_no_network), Toast.LENGTH_LONG).show();
            showErrorMessage();
        }
    }

    private boolean checkNetwork(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        ErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        RecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        RecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        ErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void loadMovieData(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_key),
                getString(R.string.settings_order_default));

        String apiKey = sharedPrefs.getString(
                getString(R.string.settings_api_key_key),"");

        new FetchMoviesTask().execute(new String[]{orderBy, apiKey});

    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param currentMovie The weather for the day that was clicked
     */
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
            URL movieRequestUrl = NetworkUtils.buildUrl(apiKey,searchFor);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                JSONObject movieJson = new JSONObject(jsonMovieResponse);
                if(movieJson.has(MovieJsonUtils.MOVIE_STATUS_CODE)){
                    ErrorMessageDisplay.setText(movieJson.getString(MovieJsonUtils.MOVIE_STATUS_MESSGAE));
                    showErrorMessage();
                    return null;
                }

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
            LoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                showMovieDataView();
                MovieAdapter.setMovieData(movieData);
            } else {
                showErrorMessage();
            }
        }
    }
}

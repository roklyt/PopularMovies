package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Adapter.ReviewAdapter;
import com.example.android.popularmovies.Adapter.TrailerAdapter;
import com.example.android.popularmovies.Favorites.AppDatabase;
import com.example.android.popularmovies.Favorites.FavoriteEntry;
import com.example.android.popularmovies.data.Reviews;
import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.data.Movies;
import com.example.android.popularmovies.utilities.ReviewsJsonUtils;
import com.example.android.popularmovies.utilities.TrailerJsonUtils;
import com.squareup.picasso.Picasso;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements  com.example.android.popularmovies.Adapter.TrailerAdapter.TrailerAdapterOnClickHandler{

    /* Recycleview adapter*/
    private com.example.android.popularmovies.Adapter.ReviewAdapter ReviewAdapter;
    /* List of all reviews*/
    private List<Reviews> ReviewsList = new ArrayList<>();
    /* Recycleview adapter*/
    private com.example.android.popularmovies.Adapter.TrailerAdapter TrailerAdapter;
    /* List of all trailer*/
    private List<Trailer> TrailerList = new ArrayList<>();

    private static MenuItem ShareItem;

    private static String YoutubeKey = "";

    Button FavoriteButton;

    /* recyclerview to populate all reviews*/
    private android.support.v7.widget.RecyclerView RecyclerViewReviews;
    private android.support.v7.widget.RecyclerView RecyclerViewTrailer;

    /* recycler trailer progress bar and error message views */
    private TextView TrailerErrorTextView;
    private ProgressBar TrailerProgressBar;

    /* recycler review progress bar and error message views */
    private TextView ReviewErrorTextView;
    private ProgressBar ReviewProgressBar;

    private String MovieId;

    private AppDatabase FavoriteDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Intent intent = getIntent();
        if (intent.hasExtra(Movies.PARCELABLE_KEY)) {
            /* Get the current movies data from the intent*/
            final Movies currentMovies = intent.getParcelableExtra(Movies.PARCELABLE_KEY);

            MovieId = currentMovies.getId();

            FavoriteDb = AppDatabase.getInstance(getApplicationContext());

            FavoriteButton = findViewById(R.id.favorite_button);

            isFavorite(currentMovies);

            /* Publish all data into their views */
            ImageView posterView = findViewById(R.id.poster_image);
            Picasso.with(this)
                    .load(String.valueOf(NetworkUtils.buildPosterUrl(currentMovies.getPosterPath(), NetworkUtils.W_500)))
                    .into(posterView);

            TextView titleView = findViewById(R.id.title_text);
            titleView.setText(currentMovies.getTitle());

            TextView averageView = findViewById(R.id.average_text);
            averageView.setText(currentMovies.getAverage().toString());

            TextView releaseView = findViewById(R.id.date_text);
            releaseView.setText(currentMovies.getPublishedDate());

            TextView synopsisView = findViewById(R.id.synopsis_text);
            synopsisView.setText(currentMovies.getSynopsis());

            /* Progress bar and text view for trailer recycle view */
            TrailerErrorTextView = findViewById(R.id.detail_trailer_error_message);
            TrailerProgressBar = findViewById(R.id.pb_trailer_loading_indicator);

            RecyclerViewTrailer = findViewById(R.id.recyclerview_trailer);

            /* set linear layout manager to the recyclerview */
            RecyclerViewTrailer.setLayoutManager(new LinearLayoutManager(this));

            /* Add the review adapter to the recyclerview.*/
            TrailerAdapter = new TrailerAdapter(this, TrailerList);
            RecyclerViewTrailer.setAdapter(TrailerAdapter);

            /* Progress bar and text view for trailer recycle view */
            ReviewErrorTextView = findViewById(R.id.detail_review_error_message);
            ReviewProgressBar = findViewById(R.id.pb_review_loading_indicator);

            RecyclerViewReviews = findViewById(R.id.recyclerview_reviews);

            /* set linear layout manager to the recyclerview */
            RecyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));

            /* Add the review adapter to the recyclerview.*/
            ReviewAdapter = new ReviewAdapter(ReviewsList);
            RecyclerViewReviews.setAdapter(ReviewAdapter);

            /* If network is available proceed else show error message */
            if (checkNetwork()) {
                loadContent();
            } else {
                Toast.makeText(this, getString(R.string.error_no_network), Toast.LENGTH_LONG).show();
            }

        } else {
            //If there are no extras show toast and go back to the main activity
            Toast.makeText(this, getString(R.string.error_empty_extra), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.detail_menu, menu);

        // Locate MenuItem with ShareActionProvider
        ShareItem = menu.findItem(R.id.action_share);

        ShareItem.setIntent(createShareIntent());
        return true;
    }

    // Call to update the share intent
    private Intent createShareIntent() {

        String shareUrl = "http://www.youtube.com/watch?v=" + YoutubeKey;

        return ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(shareUrl)
                .getIntent();
    }

    private void updateShareIntent(){
        ShareItem.setIntent(createShareIntent());
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

    /* Check network */
    private boolean checkNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void loadContent() {
        /* Get shared preferences and pas them to the async task*/
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String apiKey = sharedPrefs.getString(
                getString(R.string.settings_api_key_key), "");

        new DetailActivity.FetchMoviesReviews().execute(apiKey, MovieId);

        new DetailActivity.FetchMoviesTrailer().execute(apiKey, MovieId);
    }

    @Override
    public void onClick(Trailer currentTrailer) {
            String id = currentTrailer.getKey();

            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            try {
                this.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                this.startActivity(webIntent);
            }

    }

    private void isFavorite(final Movies currentMovies){
            final String currentMovieId = currentMovies.getId();
            final AddFavoritesViewModelFactory factory = new AddFavoritesViewModelFactory(FavoriteDb, currentMovieId);
            final AddFavoritesViewModel viewModel = ViewModelProviders.of(this, factory).get(AddFavoritesViewModel.class);
            viewModel.getFavorite().observe(this, new Observer<FavoriteEntry>() {
                @Override
                public void onChanged(@Nullable final FavoriteEntry favoriteEntry) {
                    if(favoriteEntry == null){
                        Log.d("isFavorite", "Favorite entry is null");
                        FavoriteButton.setText(R.string.mark_as_favorite_button);
                        FavoriteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onFavoriteSetButtonClicked(currentMovies);
                            }
                        });
                    }else{
                        Log.d("isFavorite", "Favorite entry is not null");
                        FavoriteButton.setText(R.string.un_mark_as_favorite_button);
                        FavoriteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onFavoriteDeleteButtonClicked(favoriteEntry);
                            }
                        });

                    }
                }
            });
    }

    public void onFavoriteDeleteButtonClicked(FavoriteEntry currentFavorite){
        final String currentFavoriteMovieId = currentFavorite.getMovieId();

        final AddFavoritesViewModelFactory factory = new AddFavoritesViewModelFactory(FavoriteDb, currentFavoriteMovieId);
        final AddFavoritesViewModel viewModel = ViewModelProviders.of(this, factory).get(AddFavoritesViewModel.class);

        Toast.makeText(this, String.format(getString(R.string.movie_un_marked_toast), currentFavorite.getTitle()), Toast.LENGTH_LONG).show();

        viewModel.getFavorite().observe(this, new Observer<FavoriteEntry>() {
            @Override
            public void onChanged(@Nullable FavoriteEntry favoriteEntry) {
                viewModel.getFavorite().removeObserver(this);
                FavoriteDb.favoriteDao().deleteFavorite(favoriteEntry);
                finish();
            }
        });
    }

    public void onFavoriteSetButtonClicked(final Movies currentMovies){

        Toast.makeText(this, String.format(getString(R.string.movie_marked_toast), currentMovies.getTitle()), Toast.LENGTH_LONG).show();

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final FavoriteEntry favorite = new FavoriteEntry(
                        currentMovies.getId(),
                        currentMovies.getTitle(),
                        currentMovies.getPosterPath(),
                        currentMovies.getPublishedDate(),
                        currentMovies.getAverage(),
                        currentMovies.getSynopsis());


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //FavoriteDb.favoriteDao().deleteFavorite(favorite);
                        FavoriteDb.favoriteDao().insertFavorite(favorite);
                        finish();
                    }
                });
            }
        });
    }

    private void showReviewData(){
        RecyclerViewReviews.setVisibility(View.VISIBLE);
        ReviewErrorTextView.setVisibility(View.INVISIBLE);
    }

    private void showReviewErrorMessage(){
        RecyclerViewReviews.setVisibility(View.INVISIBLE);
        ReviewErrorTextView.setVisibility(View.VISIBLE);
    }

    /* Async Task to make an url request against the tmdb to get the reviews list*/
    public class FetchMoviesReviews extends AsyncTask<String, Void, List<Reviews>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ReviewProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Reviews> doInBackground(String... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String apiKey = params[0];
            String movieId = params[1];

            /* build the url */
            URL reviewRequestUrl = NetworkUtils.buildReviewUrl(movieId, apiKey);

            try {
                String jsonReviewResponse = NetworkUtils
                        .getResponseFromHttpUrl(reviewRequestUrl);

                /* Everything is fine we can parse the json to get our review*/
                return ReviewsJsonUtils
                        .getReviewListFromJson(jsonReviewResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Reviews> reviewData) {
            /* Hide the loading bar */
            ReviewProgressBar.setVisibility(View.INVISIBLE);
            if (reviewData.size() != 0) {
                showReviewData();
                /* set the new data to the adapter */
                ReviewAdapter.setReviewData(reviewData);
            } else {
                showReviewErrorMessage();
            }
        }
    }

    private void showTrailerDataView() {
        /* First, make sure the error is invisible */
        TrailerErrorTextView.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        RecyclerViewTrailer.setVisibility(View.VISIBLE);
    }

    private void showTrailerErrorMessage() {
        /* First, hide the currently visible data */
        RecyclerViewTrailer.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        TrailerErrorTextView.setVisibility(View.VISIBLE);
    }

    /* Async Task to make an url request against the tmdb to get the video list*/
    private class FetchMoviesTrailer extends AsyncTask<String, Void, List<Trailer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TrailerProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Trailer> doInBackground(String... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String apiKey = params[0];
            String movieId = params[1];

            /* build the url */
            URL reviewRequestUrl = NetworkUtils.buildTrailerUrl(movieId, apiKey);

            try {
                String jsonTrailerResponse = NetworkUtils
                        .getResponseFromHttpUrl(reviewRequestUrl);

                return TrailerJsonUtils
                        .getTrailerListFromJson(jsonTrailerResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Trailer> trailerData) {
            /* Hide the loading bar */
            TrailerProgressBar.setVisibility(View.INVISIBLE);
            if (trailerData.size() != 0) {
                showTrailerDataView();
                /* set the new data to the adapter */
                TrailerAdapter.setTrailerData(trailerData);

                YoutubeKey = trailerData.get(0).getKey();
                updateShareIntent();
            } else {
                showTrailerErrorMessage();
            }
        }
    }
}

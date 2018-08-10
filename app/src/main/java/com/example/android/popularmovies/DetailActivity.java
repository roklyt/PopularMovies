package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Adapter.ReviewAdapter;
import com.example.android.popularmovies.Adapter.TrailerAdapter;
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

public class DetailActivity extends AppCompatActivity implements com.example.android.popularmovies.Adapter.ReviewAdapter.ReviewAdapterOnClickHandler, com.example.android.popularmovies.Adapter.TrailerAdapter.TrailerAdapterOnClickHandler{

    /* Recycleview adapter*/
    private com.example.android.popularmovies.Adapter.ReviewAdapter ReviewAdapter;
    /* List of all reviews*/
    private List<Reviews> ReviewsList = new ArrayList<>();
    /* Recycleview adapter*/
    private com.example.android.popularmovies.Adapter.TrailerAdapter TrailerAdapter;
    /* List of all trailer*/
    private List<Trailer> TrailerList = new ArrayList<>();

    /* recyclerview to populate all reviews*/
    private android.support.v7.widget.RecyclerView RecyclerView;
    private android.support.v7.widget.RecyclerView RecyclerViewTrailer;

    private String MovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Intent intent = getIntent();
        if (intent.hasExtra(Movies.PARCELABLE_KEY)) {
            /* Get the current movies data from the intent*/
            Movies currentMovies = intent.getParcelableExtra(Movies.PARCELABLE_KEY);

            MovieId = currentMovies.getId();

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

            RecyclerViewTrailer = findViewById(R.id.recyclerview_trailer);

            /* set linear layout manager to the recyclerview */
            RecyclerViewTrailer.setLayoutManager(new LinearLayoutManager(this));

            /* Add the review adapter to the recyclerview.*/
            TrailerAdapter = new TrailerAdapter(this, TrailerList);
            RecyclerViewTrailer.setAdapter(TrailerAdapter);


            RecyclerView = findViewById(R.id.recyclerview_reviews);

            /* set linear layout manager to the recyclerview */
            RecyclerView.setLayoutManager(new LinearLayoutManager(this));

            /* Add the review adapter to the recyclerview.*/
            ReviewAdapter = new ReviewAdapter(this, ReviewsList);
            RecyclerView.setAdapter(ReviewAdapter);

            /* If network is available proceed else show error message */
            if (checkNetwork()) {
                loadContent();
            } else {
                Toast.makeText(this, getString(R.string.error_no_network), Toast.LENGTH_LONG).show();
                //showErrorMessage();
            }

        } else {
            //If there are no extras show toast and go back to the main activity
            Toast.makeText(this, getString(R.string.error_empty_extra), Toast.LENGTH_SHORT).show();
            finish();
        }
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

    private void loadContent() {
        /* Get shared preferences and pas them to the async task*/
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String apiKey = sharedPrefs.getString(
                getString(R.string.settings_api_key_key), "");

        new DetailActivity.FetchMoviesReviews().execute(apiKey, MovieId);

        new DetailActivity.FetchMoviesTrailer().execute(apiKey, MovieId);
    }

    @Override
    public void onClick(Reviews currentReview) {
        Toast.makeText(this, "Da kommt enoch was", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(Trailer currentTrailer) {
        Toast.makeText(this, "Da kommt enoch was", Toast.LENGTH_LONG).show();
    }


    /* Async Task to make an url request against the tmdb to get the reviews list*/
    public class FetchMoviesReviews extends AsyncTask<String, Void, List<Reviews>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //LoadingIndicator.setVisibility(View.VISIBLE);
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
                List<Reviews> reviewDataList = ReviewsJsonUtils
                        .getReviewListFromJson(jsonReviewResponse);

                return reviewDataList;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Reviews> reviewData) {
            /* Hide the loading bar */
            //LoadingIndicator.setVisibility(View.INVISIBLE);
            if (reviewData != null) {
                //showMovieDataView();
                /* set the new data to the adapter */
                ReviewAdapter.setReviewData(reviewData);
            } else {
                //showErrorMessage();
            }
        }
    }


    /* Async Task to make an url request against the tmdb to get the video list*/
    private class FetchMoviesTrailer extends AsyncTask<String, Void, List<Trailer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //LoadingIndicator.setVisibility(View.VISIBLE);
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


                /* Everything is fine we can parse the json to get our review*/
                List<Trailer> trailerDataList = TrailerJsonUtils
                        .getTrailerListFromJson(jsonTrailerResponse);

                return trailerDataList;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Trailer> trailerData) {
            /* Hide the loading bar */
            //LoadingIndicator.setVisibility(View.INVISIBLE);
            if (trailerData != null) {
                //showMovieDataView();
                /* set the new data to the adapter */
                TrailerAdapter.setTrailerData(trailerData);
            } else {
                //showErrorMessage();
            }
        }
    }
}

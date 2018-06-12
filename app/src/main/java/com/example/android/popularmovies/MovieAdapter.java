package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.data.Movies;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    /* List for all movies*/
    private List<Movies> MoviesList;

    private final MovieAdapterOnClickHandler ClickHandler;

    /* Interface for the on click handler */
    public interface MovieAdapterOnClickHandler {
        void onClick(Movies currentMovie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler, List<Movies> moviesList) {
        ClickHandler = clickHandler;
        MoviesList = moviesList;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView MovieImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            MovieImageView = view.findViewById(R.id.poster_image_recyclerview);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Movies currentMovie = MoviesList.get(getAdapterPosition());
            ClickHandler.onClick(currentMovie);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder forecastAdapterViewHolder, int position) {
        /*Get the current movie to find the correct poster path */
        Movies movies = MoviesList.get(position);

        /*Get context to initialise picasso */
        Context context = forecastAdapterViewHolder.MovieImageView.getContext();

        /*Use picasso to get the thumbnail for tmdp and populate it to the image view */
        Picasso.with(context)
                .load(String.valueOf(NetworkUtils.buildPosterUrl(movies.getPosterPath(), NetworkUtils.W_185)))
                .into(forecastAdapterViewHolder.MovieImageView);
    }

    @Override
    public int getItemCount() {
        return MoviesList.size();
    }

    /* Set the new Movies list to the adapter */
    public void setMovieData(List<Movies> movieData) {
        MoviesList = movieData;
        notifyDataSetChanged();
    }
}
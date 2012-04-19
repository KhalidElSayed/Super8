package com.github.super8.apis.tmdb.v3.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.super8.model.Movie;

public class TmdbMovieParser extends TmdbParser<Movie> {

  @Override
  public Movie parseModel(JSONObject modelObject, Movie movie) throws JSONException {
    if (movie == null) {
      movie = new Movie();
    }
    movie.setTitle(modelObject.getString("title"));
    movie.setReleaseDate(parseDate(modelObject.getString("release_date")));

    String backdropPath = modelObject.getString("backdrop_path");
    if (!"null".equals(backdropPath)) {
      movie.setBackdropPath(backdropPath);
    }
    String posterPath = modelObject.getString("poster_path");
    if (!"null".equals(posterPath)) {
      movie.setPosterPath(posterPath);
    }

    // these fields are only available for a full person request

    // TODO: parse genres, plot/summary, tag line, votes

    return movie;
  }

}

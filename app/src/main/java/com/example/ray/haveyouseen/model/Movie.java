package com.example.ray.haveyouseen.model;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a movie in the code.
 */
public class Movie
{
    /**
     * Prefix link of the poster.
     */
    private static final String POSTER_PREFIX = "http://image.tmdb.org/t/p/w185";

    /**
     * Title of the movie.
     */
    private final String title;

    /**
     * Release date of the movie.
     */
    private final String releaseDate;

    /**
     * Array of genre ids.
     */
    private final int[] genreIds;

    /**
     * Link to the poster.
     */
    private final String posterLink;

    /**
     * Concat of all genre Strings.
     */
    private String genres = "";

    /**
     * Poster bitmap.
     */
    private Bitmap poster;

    /**
     * Overview text.
     */
    private String overview = "";

    /**
     * Constructor to create a movie instance.
     *
     * @param object the json object containing the information
     */
    public Movie(@NonNull JSONObject object) throws JSONException
    {
        this.title = object.getString("title");
        this.releaseDate = object.getString("release_date");
        this.overview = object.getString("overview");
        final JSONArray genreIdsArray = object.getJSONArray("genre_ids");
        final int[] genreIds = new int[genreIdsArray.length()];
        for (int i = 0; i < genreIdsArray.length(); i++)
        {
            genreIds[i] = genreIdsArray.getInt(i);
        }
        this.genreIds = genreIds;
        this.posterLink = POSTER_PREFIX + object.getString("poster_path");
    }

    /**
     * Set the genres of the movie.
     *
     * @param genres the genres to add.
     */
    public void setGenres(final String genres)
    {
        this.genres = genres;
    }

    /**
     * Set the poster of the movie.
     *
     * @param poster the poster to add.
     */
    public void setPoster(final Bitmap poster)
    {
        this.poster = poster;
    }

    /**
     * Set the overview.
     *
     * @param overview the String describing the movie.
     */
    public void setOverview(final String overview)
    {
        this.overview = overview;
    }

    /**
     * Getter for the title.
     *
     * @return the title String.
     */
    @NonNull
    public String getTitle()
    {
        return title;
    }

    /**
     * Getter for the release date.
     *
     * @return the String describing it.
     */
    @NonNull
    public String getReleaseDate()
    {
        return releaseDate;
    }

    /**
     * Getter for the genres.
     *
     * @return a concat String of all genres.
     */
    @NonNull
    public String getGenres()
    {
        return genres;
    }

    /**
     * Getter for the poster.
     *
     * @return a bitmap.
     */
    @Nullable
    public Bitmap getPoster()
    {
        return poster;
    }

    /**
     * Getter for the overview.
     *
     * @return a string describing the movie.
     */
    @NonNull
    public String getOverview()
    {
        return overview;
    }

    /**
     * Get the ids of the genres assigned to this movie.
     *
     * @return the ids.
     */
    public int[] getGenreIds()
    {
        return genreIds;
    }

    /**
     * Get the poster link.
     *
     * @return a url to the poster.
     */
    public String getPosterLink()
    {
        return posterLink;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final Movie movie = (Movie) o;
        return title.equals(movie.title) && releaseDate.equals(movie.releaseDate);
    }

    @Override
    public int hashCode()
    {
        return 31 * title.hashCode() + releaseDate.hashCode();
    }
}

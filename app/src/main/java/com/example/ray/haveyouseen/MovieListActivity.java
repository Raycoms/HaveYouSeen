package com.example.ray.haveyouseen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ray.haveyouseen.model.Movie;
import com.example.ray.haveyouseen.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity
{
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        final View recyclerView = findViewById(R.id.movie_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        ((SearchView) findViewById(R.id.search_bar)).setOnQueryTextListener(new OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(final String s)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s)
            {
                final RecyclerView.Adapter adapter =  ((RecyclerView) recyclerView).getAdapter();
                if(adapter instanceof SimpleItemRecyclerViewAdapter)
                {
                    ((SimpleItemRecyclerViewAdapter) adapter).searchForString(s);
                }
                return true;
            }
        });

        if (findViewById(R.id.movie_detail_container) != null)
        {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull final RecyclerView recyclerView)
    {
        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String movieUrl = "https://api.themoviedb.org/3/movie/upcoming?page=1&language=en-US&api_key=" + Constants.API_KEY;

        // Request a string response from the provided URL.
        final JsonObjectRequest movieRequest = new JsonObjectRequest(Request.Method.GET, movieUrl, null,
                                                                      new Response.Listener<JSONObject>()
                                                                      {
                                                                          @Override
                                                                          public void onResponse(final JSONObject response)
                                                                          {
                                                                              try
                                                                              {
                                                                                  JSONArray array = response.getJSONArray("results");
                                                                                  if (array != null)
                                                                                  {
                                                                                      final List<JSONObject> list = new ArrayList<>();
                                                                                      for (int i = 0; i < array.length(); i++)
                                                                                      {
                                                                                          list.add(array.getJSONObject(i));
                                                                                      }
                                                                                      recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(list,
                                                                                                                                                 response.getInt("total_pages")));
                                                                                  }
                                                                              }
                                                                              catch (final JSONException e)
                                                                              {
                                                                                  e.printStackTrace();
                                                                              }
                                                                          }
                                                                      }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //Do nothing.
            }
        });

        // Add the request to the RequestQueue.
        queue.add(movieRequest);
    }

    class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>
    {
        /**
         * List of all movies.
         */
        private final List<Movie> allValues;

        /**
         * List of all movies.
         */
        private final List<Movie> mValues;

        /**
         * Map of all genres.
         */
        private final Map<Integer, String> genres = new HashMap<>();

        /**
         * The last page we added to the list.
         */
        private int lastPage = 1;

        /**
         * The total amount of pages of movies (20 per page).
         */
        private final int totalPages;

        /**
         * Boolean value to check if the values are currently being searched.
         */
        private String searchString = "";

        /**
         * Instantiate the recyclerViewAdapter.
         *
         * @param items      the json list of the movies.
         * @param totalPages the total amount of pages.
         */
        SimpleItemRecyclerViewAdapter(final List<JSONObject> items, final int totalPages)
        {
            this.mValues = translateJsonObjectListToMovieList(items);
            this.allValues = translateJsonObjectListToMovieList(items);
            this.totalPages = totalPages;
        }

        /**
         * Empties all items in the list but the ones matching the query.
         * @param s the string to match.
         */
        void searchForString(final String s)
        {
            mValues.clear();
            this.searchString = s;
            if(s.isEmpty())
            {
                mValues.addAll(allValues);
                return;
            }

            for(final Movie movie: allValues)
            {
                if(movie.getTitle().toLowerCase().contains(s.toLowerCase()))
                {
                    mValues.add(movie);
                }
            }
            this.notifyDataSetChanged();
        }

        /**
         * Add more movies to the list.
         *
         * @param items the json objects representing these.
         */
        void addToList(final List<JSONObject> items)
        {
            for(final Movie movie: translateJsonObjectListToMovieList(items))
            {
                if(!mValues.contains(movie))
                {
                    allValues.add(movie);
                    if(searchString.isEmpty() || movie.getTitle().toLowerCase().contains(this.searchString.toLowerCase()))
                    {
                        mValues.add(movie);
                    }
                }
            }
            this.notifyItemRangeChanged(0, mValues.size());
            this.lastPage++;
        }

        /**
         * Translate the JsonObjectList to a MovieList.
         *
         * @param objectList the json object list.
         * @return the movie list.
         */
        @NonNull
        private List<Movie> translateJsonObjectListToMovieList(@NonNull final List<JSONObject> objectList)
        {
            final List<Movie> movieList = new ArrayList<>();
            for (final JSONObject jsonObject : objectList)
            {
                final Movie movie;
                try
                {
                    movie = new Movie(jsonObject);
                    movieList.add(movie);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            return movieList;
        }

        /**
         * Put all genres to the global map.
         *
         * @param genres the genres map.
         */
        void addGenres(final Map<Integer, String> genres)
        {
            this.genres.putAll(genres);
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
        {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position)
        {
            final Movie movie = mValues.get(position);

            holder.mNameView.setText(movie.getTitle());
            holder.mDateView.setText(String.format("Release date: %s", movie.getReleaseDate()));

            final StringBuilder stringBuilder = new StringBuilder("Genre: ");
            if (movie.getGenres().isEmpty())
            {
                if (genres.isEmpty())
                {
                    fillGenres(this, holder.mGenreView, movie);
                }

                final int[] genreIds = movie.getGenreIds();
                for (final int id : genreIds)
                {
                    if (genres.get(id) != null)
                    {
                        stringBuilder.append(genres.get(id)).append(" ");
                    }
                }
                movie.setGenres(stringBuilder.toString());
            }
            holder.mGenreView.setText(movie.getGenres());
            if (movie.getPoster() == null)
            {
                fillPoster(holder.mPosterView, movie);
            }
            else
            {
                holder.mPosterView.setImageBitmap(movie.getPoster());
            }
            holder.index = position;

            holder.mView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mTwoPane)
                    {
                        final Bundle arguments = new Bundle();
                        arguments.putInt(MovieDetailFragment.ARG_ITEM_ID, holder.index);
                        arguments.putString(MovieDetailFragment.ARG_TITLE, holder.mNameView.getText().toString());
                        arguments.putString(MovieDetailFragment.OVERVIEW, movie.getOverview());
                        arguments.putString(MovieDetailFragment.DATE, holder.mDateView.getText().toString());
                        arguments.putString(MovieDetailFragment.GENRE, genres.get(holder.index));

                        final BitmapDrawable bitmapDrawable = ((BitmapDrawable) holder.mPosterView.getDrawable());

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        arguments.putByteArray(MovieDetailFragment.POSTER, byteArray);

                        final MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                          .replace(R.id.movie_detail_container, fragment)
                          .commit();
                    }
                    else
                    {
                        final Context context = v.getContext();
                        final Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra(MovieDetailFragment.ARG_ITEM_ID, holder.index);
                        intent.putExtra(MovieDetailFragment.ARG_TITLE, holder.mNameView.getText());
                        intent.putExtra(MovieDetailFragment.OVERVIEW, movie.getOverview());
                        intent.putExtra(MovieDetailFragment.DATE, holder.mDateView.getText());
                        intent.putExtra(MovieDetailFragment.GENRE, holder.mGenreView.getText());

                        final BitmapDrawable bitmapDrawable = ((BitmapDrawable) holder.mPosterView.getDrawable());

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        intent.putExtra(MovieDetailFragment.POSTER, byteArray);
                        context.startActivity(intent);
                    }
                }
            });
            if ((position >= mValues.size() - 3) && lastPage < totalPages)
            {
                toggleEndOfList(this, lastPage + 1);
            }
        }

        @Override
        public int getItemCount()
        {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder
        {
            final View      mView;
            final TextView  mNameView;
            final TextView  mGenreView;
            final TextView  mDateView;
            final ImageView mPosterView;
            int index = 0;

            ViewHolder(View view)
            {
                super(view);
                mView = view;
                mNameView = view.findViewById(R.id.name);
                mGenreView = view.findViewById(R.id.genre);
                mDateView = view.findViewById(R.id.date);
                mPosterView = view.findViewById(R.id.poster);
            }

            @Override
            public String toString()
            {
                return super.toString() + " '" + mNameView.getText() + "'";
            }
        }
    }

    private void fillPoster(final ImageView view, final Movie movie)
    {
        final RequestQueue queue = Volley.newRequestQueue(this);

        // Request a image response from the provided URL.
        final ImageRequest genreRequest = new ImageRequest(movie.getPosterLink(),
                                                                      new Response.Listener<Bitmap>()
                                                                      {
                                                                          @Override
                                                                          public void onResponse(final Bitmap response)
                                                                          {
                                                                              if(response != null)
                                                                              {
                                                                                  movie.setPoster(response);
                                                                                  view.setImageBitmap(response);
                                                                              }
                                                                          }
                                                                      }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //Do nothing.
            }
        });

        queue.add(genreRequest);
    }

    private void toggleEndOfList(final SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter, final int lastPage)
    {
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String movieUrl = "https://api.themoviedb.org/3/movie/upcoming?page=" + lastPage + "&language=en-US&api_key=" + Constants.API_KEY;

        // Request a json response from the provided URL.
        final JsonObjectRequest movieRequest = new JsonObjectRequest(Request.Method.GET, movieUrl, null,
                                                                      new Response.Listener<JSONObject>()
                                                                      {
                                                                          @Override
                                                                          public void onResponse(final JSONObject response)
                                                                          {
                                                                              try
                                                                              {
                                                                                  JSONArray array = response.getJSONArray("results");
                                                                                  if (array != null)
                                                                                  {
                                                                                      final List<JSONObject> list = new ArrayList<>();
                                                                                      for (int i = 0; i < array.length(); i++)
                                                                                      {
                                                                                          list.add(array.getJSONObject(i));
                                                                                      }
                                                                                      simpleItemRecyclerViewAdapter.addToList(list);
                                                                                  }
                                                                              }
                                                                              catch (final JSONException e)
                                                                              {
                                                                                  e.printStackTrace();
                                                                              }
                                                                          }
                                                                      }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //Do nothing.
            }
        });

        // Add the request to the RequestQueue.
        queue.add(movieRequest);
    }

    private void fillGenres(final SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter, final TextView view, final Movie movie)
    {
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Url to fill the genres.
        final String genreUrl = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + Constants.API_KEY;

        // Request a json response from the provided URL.
        final JsonObjectRequest genreRequest = new JsonObjectRequest(Request.Method.GET, genreUrl, null,
                                                                      new Response.Listener<JSONObject>()
                                                                      {
                                                                          @Override
                                                                          public void onResponse(final JSONObject response)
                                                                          {
                                                                              try
                                                                              {
                                                                                  JSONArray array = response.getJSONArray("genres");
                                                                                  if (array != null)
                                                                                  {
                                                                                      final Map<Integer, String> genres = new HashMap<>();
                                                                                      for (int i = 0; i < array.length(); i++)
                                                                                      {
                                                                                          final JSONObject object = array.getJSONObject(i);
                                                                                          final int id = object.getInt("id");
                                                                                          final String name = object.getString("name");
                                                                                          genres.put(id, name);
                                                                                      }

                                                                                      final StringBuilder builder = new StringBuilder("Genre: ");

                                                                                      final int[] genreIds = movie.getGenreIds();
                                                                                      for (final int id : genreIds)
                                                                                      {
                                                                                          if (genres.get(id) != null)
                                                                                          {
                                                                                              builder.append(genres.get(id)).append(" ");
                                                                                          }
                                                                                      }
                                                                                      movie.setGenres(builder.toString());
                                                                                      view.setText(movie.getGenres());
                                                                                      simpleItemRecyclerViewAdapter.addGenres(genres);
                                                                                  }
                                                                              }
                                                                              catch (final JSONException e)
                                                                              {
                                                                                  e.printStackTrace();
                                                                              }
                                                                          }
                                                                      }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //Do nothing.
            }
        });

        queue.add(genreRequest);
    }
}

package com.example.ray.haveyouseen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

/**
 * An activity representing a single Movie detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MovieListActivity}.
 */
public class MovieDetailActivity extends AppCompatActivity
{
    public static final String ARG_ITEM_ID = "id";

    public static final String ARG_TITLE = "title";

    public static final String OVERVIEW = "overview";

    public static final String GENRE = "genre";

    public static final String DATE = "date";

    public static final String POSTER = "poster";

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null)
        {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            final Bundle arguments = new Bundle();
            final Bundle oldBundle = getIntent().getExtras();
            arguments.putInt(ARG_ITEM_ID, oldBundle.getInt(ARG_ITEM_ID));
            arguments.putString(ARG_TITLE, oldBundle.getString(ARG_TITLE));
            arguments.putString(OVERVIEW, oldBundle.getString(OVERVIEW));
            arguments.putString(DATE, oldBundle.getString(DATE));
            arguments.putString(GENRE, oldBundle.getString(GENRE));
            arguments.putByteArray(POSTER, oldBundle.getByteArray(POSTER));

            final MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
              .add(R.id.movie_detail_container, fragment)
              .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            navigateUpTo(new Intent(this, MovieListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.ray.haveyouseen;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment
{
    /**
     * Tags to retrieve and store data.
     */
    public static final String ARG_ITEM_ID = "id";

    public static final String ARG_TITLE = "title";

    public static final String OVERVIEW = "overview";

    public static final String GENRE = "genre";

    public static final String DATE = "date";

    public static final String POSTER = "poster";

    /**
     * Contains the overview.
     */
    public String overview;

    /**
     * Contains the date
     */
    public String date;

    /**
     * Contains the list of genres.
     */
    public String genres;

    /**
     * Contains the poster.
     */
    public Bitmap poster;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment()
    {

    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID))
        {
            final Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null)
            {
                appBarLayout.setTitle(getArguments().getString(ARG_TITLE));
            }

            overview = getArguments().getString(OVERVIEW);
            date = getArguments().getString(DATE);
            genres = getArguments().getString(GENRE);

            byte[] byteArray = getArguments().getByteArray(POSTER);
            if (byteArray != null)
            {
                poster = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        ((TextView) rootView.findViewById(R.id.overview)).setText(overview);
        ((TextView) rootView.findViewById(R.id.date)).setText(date);
        ((TextView) rootView.findViewById(R.id.genre)).setText(genres);
        ((ImageView) rootView.findViewById(R.id.poster)).setImageBitmap(poster);

        return rootView;
    }
}

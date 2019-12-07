package michal.edu.msapps;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment {

    private ImageView ivImage;
    private TextView tvTitle, tvYear, tvRating, tvGenre;

    public static MovieDetailsFragment newInstance(Movie movie) {
        Bundle args = new Bundle();
        args.putSerializable("movie", movie);
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movie_details, container, false);

        ivImage = v.findViewById(R.id.ivImage);
        tvTitle = v.findViewById(R.id.tvTitle);
        tvYear = v.findViewById(R.id.tvYear);
        tvRating = v.findViewById(R.id.tvRating);
        tvGenre = v.findViewById(R.id.tvGenre);

        assert getArguments() != null;
        Movie movie = (Movie) getArguments().getSerializable("movie");


        assert movie != null;
        Picasso.get().load(movie.getImage()).into(ivImage);
        tvTitle.setText(movie.getTitle());
        tvYear.setText(String.valueOf(movie.getReleaseYear()));
        tvRating.setText(String.valueOf(movie.getRating()));
        tvGenre.setText(movie.getGenre());

        return v;
    }

}

package michal.edu.msapps;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment {

    private RecyclerView rvMovies;
    private FloatingActionButton fab;

    public MovieListFragment() {
        // Required empty public constructor
    }


    //TODO: requiresApi
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_movie_list, container, false);
        rvMovies = v.findViewById(R.id.rvMovies);
        fab = v.findViewById(R.id.fab);

        ArrayList<Movie> movies = Movie_DAO.getInstance(getContext()).getMovies();

        MovieAdapter adapter = new MovieAdapter(movies, getActivity());
        rvMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMovies.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new QRSCannerFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return v;
    }

}

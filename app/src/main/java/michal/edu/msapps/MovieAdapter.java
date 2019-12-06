package michal.edu.msapps;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private FragmentActivity fragmentActivity;

    public MovieAdapter(List<Movie> movies, FragmentActivity fragmentActivity) {
        this.movies = movies;
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(fragmentActivity).inflate(R.layout.item, viewGroup, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        String year = String.valueOf(movie.getReleaseYear());
        holder.btnMovieYear.setText(year);
        holder.btnMovieName.setText(movie.getTitle());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{

        Button btnMovieYear, btnMovieName;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            btnMovieYear = itemView.findViewById(R.id.btnMovieYear);
            btnMovieName = itemView.findViewById(R.id.btnMovieName);
        }
    }
}

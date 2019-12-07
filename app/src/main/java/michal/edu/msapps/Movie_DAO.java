package michal.edu.msapps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Movie_DAO {

    private Context context;
    private SQLiteDatabase db;

    private static Movie_DAO instance;

    private Movie_DAO(Context context) {
        this.context = context;
        this.db = new MovieOpenHelper(context).getWritableDatabase();
    }

    public static Movie_DAO getInstance(Context context){
        if (instance == null){
            instance = new Movie_DAO(context);
        }

        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Movie> getMovies(){
        ArrayList<Movie> movies = new ArrayList<>();

        Cursor cursor = db.query("Movies", null, null, null, null, null, null);

        cursor.moveToFirst();
        do {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String image = cursor.getString(2);
            double rating = cursor.getDouble(3);
            int releaseYear = cursor.getInt(4);

            String gs = cursor.getString(5);

//            ArrayList<String> myGenre = new ArrayList<String>(Arrays.asList(gs.split(" ")));

            movies.add(new Movie(id, title, image, rating, releaseYear, gs));

        }while (cursor.moveToNext());


       movies.sort(Comparator.comparing(Movie::getReleaseYear));

        cursor.close();
        return movies;
    }
}

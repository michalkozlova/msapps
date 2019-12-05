package michal.edu.msapps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;

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

    public ArrayList<Movie> getMovies(){
        ArrayList<Movie> movies = new ArrayList<>();

        Cursor cursor = db.query("Movies", null, null, null, null, null, null);

        cursor.moveToFirst();
        do {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String image = cursor.getString(2);
            Double rating = cursor.getDouble(3);
            int releaseYear = cursor.getInt(4);

            String gs = cursor.getString(5);

            ArrayList<String> myGenre = new ArrayList<String>(Arrays.asList(gs.split(" ")));

            movies.add(new Movie(id, title, image, rating, releaseYear, myGenre));

        }while (cursor.moveToNext());

        cursor.close();
        return movies;
    }

    public void delete(int id){
        db.delete("Movies","id = ?", new String[]{"" + id});
    }
}
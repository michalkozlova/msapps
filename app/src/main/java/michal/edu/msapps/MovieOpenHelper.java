package michal.edu.msapps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MovieOpenHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "Movies";
    public static final int DB_VERSION = 1;

    public MovieOpenHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Movies(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "image TEXT NOT NULL," +
                "rating DOUBLE NOT NULL," +
                "releaseYear INTEGER NOT NULL," +
                "genre TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

package michal.edu.msapps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Toast.makeText(this, "hi", Toast.LENGTH_LONG).show();

        MovieOpenHelper dbHelper = new MovieOpenHelper(this);
        System.out.println(dbHelper);

        SQLiteDatabase db =  dbHelper.getWritableDatabase();


        Cursor cursor = db.rawQuery("SELECT * FROM Movies WHERE id = ?", new String[]{"1"});

        if (!cursor.moveToFirst()) {
            Toast.makeText(this, "NO movies", Toast.LENGTH_LONG).show();
            new FetchDataTask().execute(url);
        } else {
            Intent intent = new Intent(this, MovieActivity.class);
            startActivity(intent);
        }


    }

    private static String url = "https://api.androidhive.info/json/movies.json";

    private class FetchDataTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            InputStream inputStream = null;
            String result = null;

            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(strings[0]);

            try {

                HttpResponse response = client.execute(httpGet);
                inputStream = response.getEntity().getContent();

                if (inputStream != null){
                    result = convertInputStreamToString(inputStream);
                }
                else
                    result = "Failed";
                return result;

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            parseJSONandSaveInDB(s);

            Intent intent = new Intent(SplashActivity.this, MovieActivity.class);
            startActivity(intent);
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;
        }


        private void parseJSONandSaveInDB(String data){

            try {
                JSONArray jsonArray = new JSONArray(data);
                int jsonArrLength = jsonArray.length();

                for (int i = 0; i < jsonArrLength; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String title = jsonObject.getString("title");
                    String image = jsonObject.getString("image");
                    double rating = jsonObject.getDouble("rating");
                    int releaseYear = jsonObject.getInt("releaseYear");

                    JSONArray gen = jsonObject.getJSONArray("genre");
                    String genre = gen.join(",");

                    List<String> myList = new ArrayList<>(Arrays.asList(genre.split(",")));
                    List<String> myNewList = new ArrayList<>();
                    for (String s : myList) {
                        String newS = s.substring(1, s.length()-1);
                        myNewList.add(newS);
                    }

                    String stringForDB = "";
                    for (String s1 : myNewList) {
                        stringForDB += s1 + "\t";
                    }


                    MovieOpenHelper dbHelper = new MovieOpenHelper(SplashActivity.this);
                    SQLiteDatabase db =  dbHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put("title", title);
                    values.put("image", image);
                    values.put("rating", rating);
                    values.put("releaseYear", releaseYear);
                    values.put("genre", stringForDB);

                    db.insert("Movies", null, values);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}

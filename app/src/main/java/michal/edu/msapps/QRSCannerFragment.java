package michal.edu.msapps;


import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class QRSCannerFragment extends Fragment {

    private TextView txtContent;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    public QRSCannerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_qrscanner, container, false);

        txtContent = v.findViewById(R.id.txtContent);
        surfaceView = v.findViewById(R.id.surfaceView);

        barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();


        cameraSource = new CameraSource.Builder(Objects.requireNonNull(getContext()), barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();


        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (Objects.requireNonNull(getActivity()).checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    getActivity().requestPermissions(
                            new String[]{"android.permission.CAMERA"},
                            2
                    );
                }

                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if(qrCodes.size() != 0){
                    txtContent.post(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);

                            Movie newMoview = readJSON(qrCodes.valueAt(0).displayValue);


                            txtContent.setText("Movie found: " + newMoview.getTitle());
                            barcodeDetector.release();

                            checkIfinDB(newMoview, v);
                        }
                    });
                }
            }
        });

        return v;
    }


    private Movie readJSON(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);

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

            StringBuilder stringForDB = new StringBuilder();

            for (int j = 0; j < myNewList.size(); j++) {
                if (j == (myNewList.size()-1)){
                    stringForDB.append(myNewList.get(j));
                }else {
                    stringForDB.append(myNewList.get(j)).append(", ");
                }
            }

            System.out.println("Title: " + title);

            Movie movie = new Movie(title, image, rating, releaseYear, stringForDB.toString());

            return movie;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    //TODO: requiresApi
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void checkIfinDB(Movie newMovie, View view){
        ArrayList<Movie> myMovies = Movie_DAO.getInstance(getContext()).getMovies();
        boolean iHaveIt = false;

        for (Movie movie : myMovies) {
            if (movie.getTitle().equals(newMovie.getTitle())){
                iHaveIt = true;
                Snackbar.make(view, "Current movie already exist in the Database", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Objects.requireNonNull(getActivity())
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container, new MovieListFragment())
                                        .commit();
                            }
                        })
                        .show();
            }
        }

        if(!iHaveIt){
            MovieOpenHelper dbHelper = new MovieOpenHelper(getContext());
            SQLiteDatabase db =  dbHelper.getWritableDatabase();

            String realAddress = "https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SY1000_CR0,0,704,1000_AL_.jpg";

            ContentValues values = new ContentValues();
            values.put("title", newMovie.getTitle());
            values.put("image", realAddress);
            values.put("rating", newMovie.getRating());
            values.put("releaseYear", newMovie.getReleaseYear());
            values.put("genre", newMovie.getGenre());

            db.insert("Movies", null, values);

            Snackbar.make(view, "Added to the Database", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Objects.requireNonNull(getActivity())
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, new MovieListFragment())
                                    .commit();
                        }
                    })
                    .show();
        }
    }

}
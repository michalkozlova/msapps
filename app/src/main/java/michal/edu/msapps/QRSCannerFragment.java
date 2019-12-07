package michal.edu.msapps;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class QRSCannerFragment extends Fragment {


    public QRSCannerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_qrscanner, container, false);

        TextView txtContent = v.findViewById(R.id.txtContent);
        Button button = v.findViewById(R.id.button);
        ImageView imgview = v.findViewById(R.id.imgview);

        Bitmap bitmap = BitmapFactory.decodeResource(
                getActivity().getResources(),
                R.drawable.msapps
        );
        imgview.setImageBitmap(bitmap);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarcodeDetector detector =
                        new BarcodeDetector.Builder(getContext())
                                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                                .build();
                if (!detector.isOperational()) {
                    txtContent.setText("Could not set up the detector!");
                    return;
                }

                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<Barcode> barcodes = detector.detect(frame);

                Barcode thisCode = barcodes.valueAt(0);
                txtContent.setText(thisCode.rawValue);
                readJSON(thisCode.rawValue);
            }
        });


        return v;
    }


    private void readJSON(String data) {
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



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
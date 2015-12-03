package com.example.vaibhavgupta.feature403;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Button b;
    Bitmap bitmap ;
    OutputStream fout = null;
    String filePath = System.currentTimeMillis() + ".jpeg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        final MapsActivity mp = this;
        b = (Button) findViewById(R.id.share);
        setUpMapIfNeeded();
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub

                GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

                    @Override
                    public void onSnapshotReady(Bitmap snapshot) {
                        // TODO Auto-generated method stub
                        bitmap=snapshot;


                        try {
                            fout = openFileOutput(filePath,
                                    MODE_WORLD_READABLE);

                            // Write the string to the file
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
                            fout.flush();
                            fout.close();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            Log.d("ImageCapture", "FileNotFoundException");
                            Log.d("ImageCapture", e.getMessage());
                            filePath = "";
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            Log.d("ImageCapture", "IOException");
                            Log.d("ImageCapture", e.getMessage());
                            filePath = "";
                        }

                        openShareImageDialog(filePath);
                    }
                };

                mMap.snapshot(callback);
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }
  private void setUpMap() {

        mMap.setMyLocationEnabled(true);
    }
  public void openShareImageDialog(String filePath) {
        File file = this.getFileStreamPath(filePath);

        if (!filePath.equals("")) {
            final ContentValues values = new ContentValues(2);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            final Uri contentUriFile = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(android.content.Intent.EXTRA_STREAM, contentUriFile);
            startActivity(Intent.createChooser(intent, "Share Image"));
        } else {
            Log.e("Error", ":::::::::::::");
        }
    }
}
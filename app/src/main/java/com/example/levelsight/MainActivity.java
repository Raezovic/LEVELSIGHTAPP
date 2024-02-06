package com.example.levelsight;//package com.example.levelsight;


import static android.content.ContentValues.TAG;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap googleMap;
    private LocationManager locationManager;

    private static final int SPEECH_REQUEST_CODE = 1;
    private static final int  REQUEST_IMAGE_CAPTURE = 2;



    EditText location_input;

    ImageView capturedImageView;

    // AzureImageAnalyzer instance
    private AzureImageAnalyzer imageAnalyzer;
    private Camera mCamera;
    private CameraPreview mPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        EditText locationInput = findViewById(R.id.location_input);
        Button navigateButton = findViewById(R.id.navigateButton);

        //computer vision code
        AzureImageAnalyzer imageAnalyzer = new AzureImageAnalyzer(this);


        Button voiceAssistantButton = findViewById(R.id.voiceAssistantButton);
        location_input = findViewById(R.id.location_input);

        // Check camera permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        } else {
            // Create an instance of Camera
            mCamera = getCameraInstance();
            // Set camera rotation to 270 degrees counterclockwise
            mCamera.setDisplayOrientation(90);
            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = findViewById(R.id.cameraPreview);
            preview.addView(mPreview);
        }

        //  capturedImageView = findViewById(R.id.capturedImageView);

        voiceAssistantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
            }
        });


        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String destinationLocation = locationInput.getText().toString();

                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocation != null) {
                    LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    setupMap(currentLocation, destinationLocation);
                    openNavigation(currentLocation, destinationLocation);
                    // Call Azure API for image analysis


                    imageAnalyzer.analyzeImage("C:\\Users\\Lenovo\\Downloads\\streetbulb.jpeg", new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Handle the API response
                            Log.d(AzureImageAnalyzer.TAG, "API Response: " + response.toString());
                            //Log.d(TAG, "API Response: " + response.toString());
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle errors
                            Log.e(AzureImageAnalyzer.TAG, "API Error: " + error.toString());
                           // Log.e(TAG, "API Error: " + error.toString());
                        }
                    });
                }
                else {
                    Toast.makeText(MainActivity.this, "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }




    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    // Picture callback for handling captured photos
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // Process the captured photo data
        }
    };

    private void openNavigation(LatLng currentLocation, String destinationLocation) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> destinationAddresses = geocoder.getFromLocationName(destinationLocation, 1);
            if (!destinationAddresses.isEmpty()) {
                LatLng destinationLatLng = new LatLng(destinationAddresses.get(0).getLatitude(), destinationAddresses.get(0).getLongitude());

                String uri = "google.navigation:q=" + destinationLatLng.latitude + "," + destinationLatLng.longitude;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            } else {
                Toast.makeText(this, "Destination not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setupMap(LatLng currentLocation, String destinationLocation) {
        if (googleMap != null) {
            googleMap.clear();

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> destinationAddresses = geocoder.getFromLocationName(destinationLocation, 1);
                if (!destinationAddresses.isEmpty()) {
                    LatLng destinationLatLng = new LatLng(destinationAddresses.get(0).getLatitude(), destinationAddresses.get(0).getLongitude());

                    googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));
                    googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 15));

                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            String uri = "http://maps.google.com/maps?saddr=" + currentLocation.latitude + "," + currentLocation.longitude +
                                    "&daddr=" + destinationLatLng.latitude + "," + destinationLatLng.longitude;
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setPackage("com.google.android.apps.maps");
                            startActivity(intent);
                        }
                    });
                } else {
                    Toast.makeText(this, "Destination not found", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Speech recognition not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Handle the speech recognition results
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            // Do something with the spoken text
            Toast.makeText(this, "You said: " + spokenText, Toast.LENGTH_SHORT).show();
            location_input.setText(spokenText);
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Handle location changes if needed
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Handle status changes if needed
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Handle provider enable if needed
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Handle provider disable if needed
    }


}



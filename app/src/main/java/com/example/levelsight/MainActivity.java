package com.example.levelsight;//package com.example.levelsight;
//
//
//import android.content.ActivityNotFoundException;
//import android.content.Intent;
//import android.hardware.Camera;
//import android.os.Bundle;
//import android.speech.RecognizerIntent;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.Toast;
//
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.ArrayList;
//
//public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
//
//    private Camera mCamera;
//    private CameraPreview mPreview;
//
//    Button captureButton;
//    EditText location_input;
//
//
//
//
//    private static final int SPEECH_REQUEST_CODE = 1;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//        Button voiceAssistantButton = findViewById(R.id.voiceAssistantButton);
//        location_input = findViewById(R.id.location_input);
//
//        voiceAssistantButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startVoiceRecognition();
//            }
//        });
//
//
//
////        mCamera = getCameraInstance();
//        mPreview = new CameraPreview(this, mCamera);
//
//        mCamera = getCameraInstance();
//
//        if (mCamera != null) {
//            mPreview = new CameraPreview(this, mCamera);
//
//            FrameLayout previewLayout = findViewById(R.id.cameraPreview);
//            previewLayout.addView(mPreview);
//
//            captureButton = findViewById(R.id.captureButton);
//            captureButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mCamera.takePicture(null, null, mPictureCallback);
//                }
//            });
//        } else {
//            // Handle the case where the camera cannot be opened
//            Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void startVoiceRecognition() {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//
//        try {
//            startActivityForResult(intent, SPEECH_REQUEST_CODE);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(this, "Speech recognition not supported on this device", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//            // Handle the speech recognition results
//            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            String spokenText = results.get(0);
//
//            // Do something with the spoken text
//            Toast.makeText(this, "You said: " + spokenText, Toast.LENGTH_SHORT).show();
//            location_input.setText(spokenText);
//        }
//    }
//
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        // add map pointers etc
//    }
//
//
//
//    // Camera initialization method
//    private Camera getCameraInstance() {
//        Camera c = null;
//        try {
//            c = Camera.open();
//        } catch (Exception e) {
//            // Log the exception for debugging
//            Log.e("CameraPreview", "Error opening camera: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return c;
//    }
//
//
//
//    // Picture callback for handling captured photos
//    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
//        @Override
//        public void onPictureTaken(byte[] data, Camera camera) {
//            // Process the captured photo data
//        }
//    };
//
//    // Other necessary lifecycle methods, e.g., release the camera in onDestroy()
//}

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap googleMap;
    private LocationManager locationManager;

    private static final int SPEECH_REQUEST_CODE = 1;

    EditText location_input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        EditText locationInput = findViewById(R.id.location_input);
        Button navigateButton = findViewById(R.id.navigateButton);





        Button voiceAssistantButton = findViewById(R.id.voiceAssistantButton);
        location_input = findViewById(R.id.location_input);

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
                } else {
                    Toast.makeText(MainActivity.this, "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

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



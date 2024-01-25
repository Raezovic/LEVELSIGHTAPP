package com.example.levelsight;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private Camera mCamera;
    private CameraPreview mPreview;

    Button captureButton;
    EditText location_input;




    private static final int SPEECH_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button voiceAssistantButton = findViewById(R.id.voiceAssistantButton);
        location_input = findViewById(R.id.location_input);

        voiceAssistantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
            }
        });



//        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera);

        mCamera = getCameraInstance();

        if (mCamera != null) {
            mPreview = new CameraPreview(this, mCamera);

            FrameLayout previewLayout = findViewById(R.id.cameraPreview);
            previewLayout.addView(mPreview);

            captureButton = findViewById(R.id.captureButton);
            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCamera.takePicture(null, null, mPictureCallback);
                }
            });
        } else {
            // Handle the case where the camera cannot be opened
            Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show();
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
        // add map pointers etc
    }



    // Camera initialization method
    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // Log the exception for debugging
            Log.e("CameraPreview", "Error opening camera: " + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }



    // Picture callback for handling captured photos
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // Process the captured photo data
        }
    };

    // Other necessary lifecycle methods, e.g., release the camera in onDestroy()
}

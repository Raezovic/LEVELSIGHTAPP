package com.example.levelsight;


import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private Camera mCamera;
    private CameraPreview mPreview;

    Button captureButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // add map pointers etc
    }



    // Camera initialization method
    // Camera initialization method
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

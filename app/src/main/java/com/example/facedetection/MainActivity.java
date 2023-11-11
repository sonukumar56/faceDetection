package com.example.facedetection;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {
        public static final int requestCode = 1234;
    ImageView im;
    TextView tx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.clickphoto);
        tx=findViewById(R.id.resultxt);
        im=findViewById(R.id.imageset);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, requestCode);

                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == requestCode  && resultCode  == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                im.setImageBitmap(photo);
                detectface(photo);


            }
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "kuch gadbad hai",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void detectface(Bitmap photo) {
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

        FaceDetector detector = FaceDetection.getClient(options);
        InputImage image = InputImage.fromBitmap(photo, 0);
        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {

                                        String resultText="";
                                        int i=1;
                                        for (Face face : faces){

                                            resultText="Face Number "+i+
                                                    "\nSmile = " + (face.getSmilingProbability())*100+"%" +
                                                    "\nRight Eye Open = " +(face.getRightEyeOpenProbability())*100+
                                            "\nLeft Eye Open = " +(face.getLeftEyeOpenProbability())*100+
                                            "\n Rect = " +(face.getBoundingBox());
                                            i++;
                                        }
                                        if(faces.isEmpty()){
                                            Toast.makeText(MainActivity.this, "No faces detect", Toast.LENGTH_SHORT).show();
                                        }else{
                                            tx.setText(resultText);
                                            Toast.makeText(MainActivity.this, resultText, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "No faces detect", Toast.LENGTH_SHORT).show();

                                    }
                                });

    }

}
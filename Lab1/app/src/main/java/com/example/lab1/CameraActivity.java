
package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);
        String photoName = getIntent().getExtras().getString("photoName","");
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File file = new File(extStorageDirectory, photoName);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            View mImg = findViewById(R.id.image);
            ((ImageView) mImg).setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
package com.example.lab1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button pushNotification;
    Button searchGoogle;
    TextInputEditText search;
    Button openCamera;
    RadioButton frontCamera;
    RadioButton backCamera;
    Button openPhoto;
    Bitmap photo;
    String photoName;
    Button openLastPhoto;
    private static final String CHANNEL_ID = "channel_id01";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pushNotification = findViewById(R.id.button2);
        pushNotification.setOnClickListener(view -> pushNotification());
        
        searchGoogle = findViewById(R.id.button3);
        searchGoogle.setOnClickListener(view -> searchGoogle());

        openCamera = findViewById(R.id.button4);
        openCamera.setOnClickListener(view -> openCamera());

        openLastPhoto = findViewById(R.id.button5);
        openLastPhoto.setOnClickListener(view -> openLastPhoto());
    }

    private void openLastPhoto() {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.putExtra("photoName", photoName);
        startActivity(intent);
    }

    private void openCamera() {
        frontCamera = findViewById(R.id.radioButton3);
        backCamera = findViewById(R.id.radioButton2);

        if(frontCamera.isChecked()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            capturePhoto.launch(intent);
        } else {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            capturePhoto.launch(intent);
        }
    }

    private ActivityResultLauncher<Intent> capturePhoto = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                openPhoto = findViewById(R.id.button5);
                if(result.getResultCode() == Activity.RESULT_OK) {
                    photo = (Bitmap) result.getData().getExtras().get("data");

                    String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                    OutputStream outStream;
                    photoName = "MyPhoto" + System.currentTimeMillis() + ".jpeg";
                    File file = new File(extStorageDirectory, photoName);
                    try {
                        outStream = new FileOutputStream(file);
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.flush();
                        outStream.close();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    private void searchGoogle() {
        search = (TextInputEditText) findViewById(R.id.textInputEditText);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/search?q=" + search.getText()));
        startActivity(browserIntent);
    }

    private void pushNotification() {
        createNotificationChannel();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID);
            builder.setSmallIcon(R.drawable.ic_action_name)
                    .setContentTitle("Notification")
                    .setContentText("New Notification")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
        }, 10000);
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            CharSequence name = "My Notification";
            String description = "My description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
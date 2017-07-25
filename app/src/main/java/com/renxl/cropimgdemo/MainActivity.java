package com.renxl.cropimgdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int POSITIVE_CAMERA = 884;
    private static final int POSITIVE_PHOTO = 885;
    private static final int CROP_PHOTO = 888;

    private File mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    private File mFile;

    ImageView img;
    Button btnCamera, btnFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView) findViewById(R.id.img_img);
        btnCamera = (Button) findViewById(R.id.btn_camera);
        btnFile = (Button) findViewById(R.id.btn_file);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera();
            }
        });
        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFile();
            }
        });
    }

    private void showFile() {
        if (!createFile()) return;

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, POSITIVE_PHOTO);
        else
            Toast.makeText(MainActivity.this, "暂无相册应用，请选择拍照", Toast.LENGTH_SHORT).show();
    }

    private void showCamera() {
        if (!createFile()) return;

        Uri uri = Uri.fromFile(mFile);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(takePictureIntent, POSITIVE_CAMERA);
    }

    private boolean createFile() {
        if (!mPath.exists()) {
            boolean mkdirs = mPath.mkdirs();
            if (!mkdirs) return false;
        }

        long name = System.currentTimeMillis();
        mFile = new File(mPath, name + ".jpg");

        try {
            boolean newFile = mFile.createNewFile();
            if (newFile) return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private void cropImage(Uri uri) {
        if (uri == null) return;

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 2.5);
        intent.putExtra("aspectY", 1.5);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
        startActivityForResult(intent, CROP_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case POSITIVE_CAMERA:
                cropImage(Uri.fromFile(mFile));
                break;
            case POSITIVE_PHOTO:
                if (data == null || data.getData() == null) return;
                cropImage(data.getData());
                break;
            case CROP_PHOTO:
                for (int i = 0; i < 5; i++) {
                    Bitmap bitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());
                    if (bitmap != null) {
                        img.setImageBitmap(bitmap);
                        break;
                    }
                }
                Glide.with(this).load(mFile).into(img);
                break;
        }
    }
}

package com.mikelau.cropme;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

public class CameraActivity extends Activity {

    private Button btnSummon;
    private ImageView ivMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        new CroperinoConfig("IMG_", "/Imfree/Pictures", "/sdcard/Imfree/Pictures");
        CroperinoFileUtil.verifyStoragePermissions(CameraActivity.this);
        CroperinoFileUtil.setupDirectory(CameraActivity.this);

        setContentView(R.layout.galeria_prueba);

        Button but = (Button) findViewById(R.id.button);

        but.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                openCamera();
            }
        });

        //openCamera();
    }

    public void openCamera(){
        if (CroperinoFileUtil.verifyStoragePermissions(CameraActivity.this)) {
            Croperino.launchCamera(CameraActivity.this);
        }
    }

    private void prepareCamera() {
        Croperino.prepareCamera(CameraActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CroperinoConfig.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {


                    Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), CameraActivity.this, true, 70, 100, 0, 0);


                }else if(resultCode == Activity.RESULT_CANCELED){
                    //Native.instance.backToUnity(this);
                }
                break;
            case CroperinoConfig.REQUEST_CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK && null != data) {
                    Uri uri = Uri.fromFile(CroperinoFileUtil.getmFileTemp());

                    String picturePath = CroperinoFileUtil.getPath(CameraActivity.this ,uri);

                    Bitmap bm = Util.decodeSampledBitmapFromFile(picturePath);

                    ImageView iv = (ImageView) findViewById(R.id.imageView);
                    iv.setImageBitmap(bm);

                    Util.generarImagen(bm);
                    //Native.instance.backToUnity(this);
                }else if(resultCode == Activity.RESULT_CANCELED){
                    //Native.instance.backToUnity(this);
                }
                break;
            default:
                //Native.instance.backToUnity(this);
                break;
        }
    }


    private File persistImage(Context ctx, Bitmap bitmap) {
        File filesDir = ctx.getFilesDir();
        File imageFile = new File(CroperinoConfig.getsDirectory() + CroperinoConfig.getsImageName());

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();

        } catch (Exception e) {
            Log.e("Error writing bitmap", e.toString());
        }
        return imageFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CroperinoFileUtil.REQUEST_CAMERA) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.CAMERA)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        prepareCamera();
                    }
                }
            }
        } else if (requestCode == CroperinoFileUtil.REQUEST_EXTERNAL_STORAGE) {
            boolean wasReadGranted = false;
            boolean wasWriteGranted = false;

            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        wasReadGranted = true;
                    }
                }
                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        wasWriteGranted = true;
                    }
                }
            }

            if (wasReadGranted && wasWriteGranted) {
                Croperino.launchCamera(CameraActivity.this);
            }
        }
    }
}

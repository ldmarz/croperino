package com.mikelau.cropme;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;

public class GalleryActivity extends Activity {

    private Button btnSummon;
    private ImageView ivMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.galeria_prueba);


        new CroperinoConfig("IMG_" + System.currentTimeMillis() + ".jpg", "/Imfree/Pictures", "/sdcard/Imfree/Pictures");
        CroperinoFileUtil.verifyStoragePermissions(GalleryActivity.this);
        CroperinoFileUtil.setupDirectory(GalleryActivity.this);

        Button but = (Button) findViewById(R.id.button);

        but.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                openGallery();
            }
        });

//        openGallery();
    }

    public void openGallery(){
        if (CroperinoFileUtil.verifyStoragePermissions(GalleryActivity.this)) {
            Croperino.launchGallery(GalleryActivity.this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CroperinoConfig.REQUEST_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("lenin", String.valueOf(data));

                    CroperinoFileUtil.newGalleryFile(data, GalleryActivity.this);

                    Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), GalleryActivity.this, true, 70, 100, 0, 0);
//
                }else if(resultCode == Activity.RESULT_CANCELED){
                    Native.instance.backToUnity(this);
                }
                break;
            case CroperinoConfig.REQUEST_CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK && null != data) {
                    Uri uri = Uri.fromFile(CroperinoFileUtil.getmFileTemp());

                    String picturePath = CroperinoFileUtil.getPath(GalleryActivity.this,uri);

                    Bitmap bm = Util.decodeSampledBitmapFromFile(picturePath);

                    //Util.generarImagen(bm);

                    ImageView iv = (ImageView) findViewById(R.id.imageView);
                    iv.setImageBitmap(bm);

  //                  Native.instance.backToUnity(this);
                }else if(resultCode == Activity.RESULT_CANCELED){
//                    Native.instance.backToUnity(this);
                }
                break;
            default:
    //            Native.instance.backToUnity(this);
            break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

         if (requestCode == CroperinoFileUtil.REQUEST_EXTERNAL_STORAGE) {
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
                Croperino.launchGallery(GalleryActivity.this);
            }
        }
    }
}

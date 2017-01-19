package com.mikelau.cropme;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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

        openGallery();
    }

    public void openGallery(){
        if (CroperinoFileUtil.verifyStoragePermissions(GalleryActivity.this)) {
            Croperino.launchGallery(GalleryActivity.this);
        }
    }


    private void prepareCamera() {
        Croperino.prepareCamera(GalleryActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("lenin", "asdlkalsdklasdkalskdalskd");
        switch (requestCode) {
            case CroperinoConfig.REQUEST_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("lenin", String.valueOf(data));
                    Uri uri = data.getData();

                    CroperinoFileUtil.newGalleryFile(data, GalleryActivity.this);
                    Log.d("lenin",String.valueOf(CroperinoFileUtil.getmFileTemp()));

                    Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), GalleryActivity.this, true, 1, 1, 0, 0);
//
                }else{
                    Log.d("lenin", "no fue ok");
                }
                break;
                case CroperinoConfig.REQUEST_CROP_PHOTO:
                    if (resultCode == Activity.RESULT_OK && null != data) {
                    Log.d("lenin","result del cut");
                    Log.d("lenin","result data:" + data);
                    Uri uri = Uri.fromFile(CroperinoFileUtil.getmFileTemp());
                    Log.d("lenin", String.valueOf(uri));

                    Log.d("lenin", "uri: "+ String.valueOf(uri));
                    String[] projection  = {MediaStore.Images.Media.DATA};
                    Log.d("lenin", "proj" + String.valueOf(projection));

                    Cursor cursor  = getContentResolver().query(uri,projection,null, null, null);
                    Log.d("lenin", "cursor" + String.valueOf(cursor));
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    Log.d("lenin", "columnIndex" + String.valueOf(columnIndex));
                    String picturePath = cursor.getString(columnIndex); // returns null

                    Log.d("lenin", "picturepath" + String.valueOf(picturePath));

                    Bitmap bm = Util.decodeSampledBitmapFromFile(picturePath);
                    Util.generarImagen(bm);
                    cursor.close();

                    Native.sendMessage("pickDone", "");
                    Native.instance.backToUnity(this);

                        //Do saving / uploading of photo method here.
                        //The image file can always be retrieved via CroperinoFileUtil.getmFileTemp()
                    }
                break;
        }
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
                Croperino.launchGallery(GalleryActivity.this);
            }
        }
    }
}

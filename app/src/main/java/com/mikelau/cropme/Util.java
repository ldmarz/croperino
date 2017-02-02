package com.mikelau.cropme;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mikelau.cropme.Native;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by lmartinez on 19/1/17.
 */

public class Util {
    public static void generarImagen(Bitmap bm){
        final String METHOD_CALLBACK_UNITY = "pickDone";

        if (bm == null) return;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bm != null)
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        JSONObject json_object = new JSONObject();
        try {
            json_object.put("width", bm.getWidth());
            json_object.put("height", bm.getHeight());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(json_object);


        Native.instance.setPluginData(byteArray);
        Native.sendMessage(METHOD_CALLBACK_UNITY,"imagen_cargada");


    }

    public static Bitmap decodeSampledBitmapFromFile(String path) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        //options.inJustDecodeBounds = true;
        Bitmap bm = BitmapFactory.decodeFile(path, options);

        Bitmap resized = Bitmap.createScaledBitmap(bm, 420, 600, true);

        return resized;
    }
}

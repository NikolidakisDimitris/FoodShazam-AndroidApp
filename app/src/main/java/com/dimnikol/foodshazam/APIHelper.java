package com.dimnikol.foodshazam;

import android.graphics.Bitmap;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIHelper {

    //TODO: The external URL should be commented out. The other one is just for testing reasons
    //URL for Actual WebServer
    private static final String URL = "http://snf-868919.vm.okeanos.grnet.gr:5001/uploader_food";

    //URL to run locally the API
//    private static final String URL = "http://10.0.2.2:5000/uploader_food";

    private static final String DEFAULT_FILE_NAME = "FILE";

    private static final OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                                 .connectTimeout(0, TimeUnit.SECONDS)
                                 .readTimeout(0, TimeUnit.SECONDS)
                                 .writeTimeout(0, TimeUnit.SECONDS)
                                 .build();
    }

    public static String uploadBitmap(Bitmap bitmap) {
        if (client != null && bitmap != null) {
            try {

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);

                RequestBody requestBody
                        = new MultipartBody.Builder()
                                           .setType(MultipartBody.FORM)
                                           .addFormDataPart("file", DEFAULT_FILE_NAME, RequestBody.create(MediaType.parse("image/png"), bos.toByteArray()))
                                           .build();

                Request request = new Request.Builder()
                        .url(URL)
                        .post(requestBody)
                        .build();

                final Response response = client.newCall(request).execute();

                if (response != null && response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (Exception ex) {
                Log.e("FoodShazam > APIHelper", ex.getMessage());
            }
        }

        return null;
    }


}

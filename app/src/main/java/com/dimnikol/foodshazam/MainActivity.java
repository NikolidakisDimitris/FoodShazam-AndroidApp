package com.dimnikol.foodshazam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;

public class MainActivity extends AppCompatActivity {

    // Constants:
    public static final int CAMERA_CODE = 123;
    private static final String URL_STRING = "http://snf-868919.vm.okeanos.grnet.gr:5001/uploader_food";


    private ImageView imageView;

    private Button cameraButton;
    private Response jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainPage", "Inside on the onCreate ");

        //create the Button and the image View
        cameraButton = (Button) findViewById(R.id.cameraButton);
        imageView = (ImageView) findViewById(R.id.filler_Image);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera(view);
            }
        });
    }


    public void openCamera(View view) {
        Log.d("FoodShazam", "Opening Camera");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_CODE);
        Log.d("FoodShazam", "The Camera is open ");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == CAMERA_CODE) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                sentFotoToWS(bitmap);


                imageView.setImageBitmap(bitmap);
            }
        }
    }


    protected void sentFotoToWS(final Bitmap bitmap) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "multipart/form-data");

        RequestParams params = new RequestParams();

        params.put("image", bitmap);
        client.post(URL_STRING, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("FoooShazam", "Success. Response =  " + response.toString());
                Gson gson = new Gson();
                jsonResponse = (Response) gson.fromJson(String.valueOf(response), Response.class);
                Log.e("FoodShazam", "Successfull parsing of object");
                Log.e("FoodShazam", "Food " + jsonResponse.getFood());
                Log.e("FoodShazam", "Ingredient " + jsonResponse.getIngredient());
                Log.e("FoodShazam", "Recipie " + jsonResponse.getRecipe());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.e("FoooShazam", "Fail " + e.toString());
                Log.e("FoooShazam", "statusCode " + statusCode);
                jsonResponse = new Response();
                jsonResponse.setStatus("Failed to excecute the request");
            }
        });
    }
}

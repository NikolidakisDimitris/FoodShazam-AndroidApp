package com.dimnikol.foodshazam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.dimnikol.foodshazam.Utils.buildTextToDisplay;
import static com.dimnikol.foodshazam.Utils.getPrefix;
import static com.dimnikol.foodshazam.Utils.openCamera;
import static com.dimnikol.foodshazam.Utils.openGallery;
import static com.dimnikol.foodshazam.Utils.resetTextView;

public class MainActivity extends AppCompatActivity implements TaskCallback {

    // Constants:
    public static final int CAMERA_CODE = 1;
    public static final int GALLERY_CODE = 2;
    private static final String URL_STRING = "http://snf-868919.vm.okeanos.grnet.gr:5001/uploader_food";

    //The image that fills the first screen
    private ImageView fillerView;

    private Button cameraButton;
    private Button galleryButton;
    private Response wsResponse;
    private TextView wsDataToDisplay;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(getPrefix(this), "initialize the buttons and views");


        //create the Button and the image View
        cameraButton = (Button) findViewById(R.id.cameraButton);
        fillerView = (ImageView) findViewById(R.id.filler_Image);
        wsDataToDisplay = (TextView) findViewById(R.id.wsData);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(getPrefix(MainActivity.this), "Ready to open the camera");
                openCamera(MainActivity.this);
            }
        });
        galleryButton = (Button) findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(getPrefix(MainActivity.this), "Ready to open the gallery");
                openGallery(MainActivity.this);
            }
        });
    }


    private void process(Bitmap bitmap) {
        Toast.makeText(MainActivity.this, "Sending picture", Toast.LENGTH_LONG).show();
        resetTextView(wsDataToDisplay);

        fillerView.setImageBitmap(bitmap);
        sentFotoToWS(bitmap);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(getPrefix(this), " Ready to call the avtivity with code " + requestCode);


        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case CAMERA_CODE:
                    final Bitmap image = (Bitmap) data.getExtras().get("data");
                    process(image);
                    break;
                case GALLERY_CODE:
                    try {

                        final Uri imageUri = data.getData();
                        final InputStream imageStream;
                        imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        process(selectedImage);

                    } catch (FileNotFoundException e) {
                        Log.d(getPrefix(this), "Oups something went wrong with the image loading");
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    protected void sentFotoToWS(final Bitmap bitmap) {
        new UploadFileTask(this).execute(bitmap);
    }


//    protected void sentFotoToWS(final Bitmap bitmap) {
//
//        Gson gson = new Gson();
//
//        String mockResponse = "{\n" +
//                "  \"exception\": null, \n" +
//                "  \"food\": \"hamburger\", \n" +
//                "  \"ingredient\": [\n" +
//                "    \"1 pound lean ground beef\", \n" +
//                "    \"1 tablespoon Worcestershire sauce\", \n" +
//                "    \"1 tablespoon liquid smoke flavoring\", \n" +
//                "    \"1 teaspoon garlic powder\", \n" +
//                "    \"1 tablespoon olive oil\", \n" +
//                "    \"seasoned salt to taste \"\n" +
//                "  ], \n" +
//                "  \"recipe\": \"Preheat a grill for high heat.\\nIn a medium bowl, lightly mix together the ground beef, Worcestershire sauce, liquid smoke and garlic powder. Form into 3 patties, handling the meat minimally. Brush both sides of each patty with some oil, and season with seasoned salt.\\nPlace the patties on the grill grate, and cook for about 5 minutes per side, until well done.\\n\", \n" +
//                "  \"status\": \"OK\"\n" +
//                "}";
//
//
//        wsResponse = (Response) gson.fromJson(mockResponse, Response.class);
//
//        System.out.println("The WS Response is :");
//        System.out.println(wsResponse);
//
//        wsResponse = (Response) gson.fromJson(mockResponse, Response.class);
//        Log.d(getPrefix(this), "The status code is " + wsResponse.getStatus());
//        Log.d(getPrefix(this), "The exeption is " + wsResponse.getException());
//        Log.d(getPrefix(this), "The food is " + wsResponse.getFood());
//        Log.d(getPrefix(this), "The recipie is " + wsResponse.getRecipe());
//        Log.d(getPrefix(this), "The ingredient is " + wsResponse.getIngredient());
//    }

    @Override
    public void onTaskScheduled() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskCompleted(Response response) {
        progressBar.setVisibility(View.GONE);
        Log.d(getPrefix(this), "Ready to display the data");
        wsDataToDisplay.setBackgroundColor(Color.parseColor("#4F020000"));
        String textToDisplay = buildTextToDisplay(response);
        wsDataToDisplay.setText(textToDisplay);
        Log.d(getPrefix(this), "The data has been displayed");
    }

    //TODO: This needs implementation to actully perform a post request and parse tbe responseObject
//    protected void sentFotoToWS(final Bitmap bitmap) {
//
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.addHeader("Content-Type", "multipart/form-data");
//
//        RequestParams params = new RequestParams();
//
//        params.put("image", bitmap);
//        client.post(URL_STRING, params, new JsonHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.e("FoooShazam", "Success. Response =  " + response.toString());
//                Gson gson = new Gson();
//                wsResponse = (Response) gson.fromJson(String.valueOf(response), Response.class);
//                Log.e("FoodShazam", "Successfull parsing of object");
//                Log.e("FoodShazam", "Food " + wsResponse.getFood());
//                Log.e("FoodShazam", "Ingredient " + wsResponse.getIngredient());
//                Log.e("FoodShazam", "Recipie " + wsResponse.getRecipe());
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
//                Log.e("FoooShazam", "Fail " + e.toString());
//                Log.e("FoooShazam", "statusCode " + statusCode);
//                wsResponse = new Response();
//                wsResponse.setStatus("Failed to excecute the request");
//            }
//        });
//    }
}


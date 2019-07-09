package com.dimnikol.foodshazam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    // Constants:
    public static final int CAMERA_CODE = 1;
    public static final int GALERY_CODE = 2;
    private static final String URL_STRING = "http://snf-868919.vm.okeanos.grnet.gr:5001/uploader_food";


    private ImageView imageView;

    private Button cameraButton;
    private Button galleryButton;
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
        galleryButton = (Button) findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(view);
            }
        });
    }


    public void openCamera(View view) {
        Log.d("FoodShazam", "Opening Camera");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_CODE);
        Log.d("FoodShazam", "The Camera is open ");

    }

    public void openGallery(View view) {
        Log.d("FoodShazam", "Opening Gallery");
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("FoodShazam ", " Ready to call the avtivity with code " + requestCode);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == CAMERA_CODE) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                sentFotoToWS(bitmap);
                imageView.setImageBitmap(bitmap);
            } else if (requestCode == GALERY_CODE) {
                Log.d("FoodShazam", "Go to open the Gallery");

                Uri selectImage = data.getData();
                imageView.setImageURI(selectImage);
            }
        }
    }


    protected void sentFotoToWS(final Bitmap bitmap) {
        Gson gson = new Gson();

        String mockResponse = "{\n" +
                "  \"exception\": null, \n" +
                "  \"food\": \"hamburger\", \n" +
                "  \"ingredient\": [\n" +
                "    \"1 pound lean ground beef\", \n" +
                "    \"1 tablespoon Worcestershire sauce\", \n" +
                "    \"1 tablespoon liquid smoke flavoring\", \n" +
                "    \"1 teaspoon garlic powder\", \n" +
                "    \"1 tablespoon olive oil\", \n" +
                "    \"seasoned salt to taste \"\n" +
                "  ], \n" +
                "  \"recipe\": \"Preheat a grill for high heat.\\nIn a medium bowl, lightly mix together the ground beef, Worcestershire sauce, liquid smoke and garlic powder. Form into 3 patties, handling the meat minimally. Brush both sides of each patty with some oil, and season with seasoned salt.\\nPlace the patties on the grill grate, and cook for about 5 minutes per side, until well done.\\n\", \n" +
                "  \"status\": \"OK\"\n" +
                "}";


        jsonResponse = (Response) gson.fromJson(mockResponse, Response.class);

        System.out.println("Antikeimeno");
        System.out.println(jsonResponse);

        jsonResponse = (Response) gson.fromJson(mockResponse, Response.class);
        Log.d("FoodShazam", "The status code is " + jsonResponse.getStatus());
        Log.d("FoodShazam", "The exeption is " + jsonResponse.getException());
        Log.d("FoodShazam", "The food is " + jsonResponse.getFood());
        Log.d("FoodShazam", "The recipie is " + jsonResponse.getRecipe());
        Log.d("FoodShazam", "The ingredient is " + jsonResponse.getIngredient());
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
//                jsonResponse = (Response) gson.fromJson(String.valueOf(response), Response.class);
//                Log.e("FoodShazam", "Successfull parsing of object");
//                Log.e("FoodShazam", "Food " + jsonResponse.getFood());
//                Log.e("FoodShazam", "Ingredient " + jsonResponse.getIngredient());
//                Log.e("FoodShazam", "Recipie " + jsonResponse.getRecipe());
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
//                Log.e("FoooShazam", "Fail " + e.toString());
//                Log.e("FoooShazam", "statusCode " + statusCode);
//                jsonResponse = new Response();
//                jsonResponse.setStatus("Failed to excecute the request");
//            }
//        });
//    }
}


package com.dimnikol.foodshazam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.dimnikol.foodshazam.Utils.buildFoodToDisplay;
import static com.dimnikol.foodshazam.Utils.buildTextToDisplay;
import static com.dimnikol.foodshazam.Utils.getPrefix;
import static com.dimnikol.foodshazam.Utils.openCamera;
import static com.dimnikol.foodshazam.Utils.openGallery;
import static com.dimnikol.foodshazam.Utils.resetTextView;

public class MainActivity extends AppCompatActivity implements TaskCallback {

    // Constants:
    public static final int CAMERA_CODE = 1;
    public static final int GALLERY_CODE = 2;

    //The image that fills the first screen
    private ImageView fillerView;
    private TextView welcomeMsg;

    private Button cameraButton;
    private Button galleryButton;
    private Response wsResponse;
    private TextView wsDataToDisplay;
    private ProgressBar progressBar;
    private Button recipeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(getPrefix(this), "initialize the buttons and views");

        //create the Button and the image View
        fillerView = (ImageView) findViewById(R.id.filler_Image);
        wsDataToDisplay = (TextView) findViewById(R.id.wsData);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //button to open the camera
        cameraButton = (Button) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(getPrefix(MainActivity.this), "Ready to open the camera");
                openCamera(MainActivity.this);
            }
        });

        //button to open the gallery
        galleryButton = (Button) findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(getPrefix(MainActivity.this), "Ready to open the gallery");
                openGallery(MainActivity.this);
            }
        });

        recipeButton = (Button) findViewById(R.id.recipeButton);
        recipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wsResponse != null && wsDataToDisplay != null) {
                    Log.d(getPrefix(MainActivity.this), "The Response want't null");
                    Log.d(getPrefix(MainActivity.this), "Ready to display the recipe");
                    String recipe = buildTextToDisplay(wsResponse);
                    wsDataToDisplay.setText(R.string.wsData);
                    wsDataToDisplay.setText(recipe);
                }
            }
        });
    }


    private void process(Bitmap bitmap) {
        Toast.makeText(MainActivity.this, "Sending picture", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onTaskScheduled() {
        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void onTaskCompleted(Response response) {
        // remove the progress bar
        progressBar.setVisibility(View.GONE);

        //Display the data on the screen, and make darker the background
        wsDataToDisplay.setBackgroundColor(Color.parseColor("#4F020000"));
        wsResponse = response;

        //The response of the API is not null
        if (response != null) {
            Log.d(getPrefix(MainActivity.this), "The Response want't null");
            Log.d(getPrefix(this), "Ready to display the data");

            if (response.getException() == null) {

                String textToDisplay = buildFoodToDisplay(response);
                wsDataToDisplay.setMovementMethod(new ScrollingMovementMethod());
                wsDataToDisplay.setText(textToDisplay);
                Log.d(getPrefix(this), "The data has been displayed");

                //Remove the welcomeMsg, and appear a recipe button
                welcomeMsg = (TextView) findViewById(R.id.welcomeMsg);
                welcomeMsg.setVisibility(View.GONE);

                recipeButton.setVisibility(View.VISIBLE);
                recipeButton.setActivated(true);
            } else if (response.getException().equals("OS ERROR")) {
                Log.d(getPrefix(MainActivity.this), "The Response was OS ERROR, the type of the file was wrong");
                String textToDisplay = "Ups! You uploaded a wrong file type :( \n Please try again ";
                wsDataToDisplay.setMovementMethod(new ScrollingMovementMethod());
                wsDataToDisplay.setText(textToDisplay);
                Log.d(getPrefix(MainActivity.this), "The error message has been displayed");

                //Remove the welcomeMsg, and appear a recipe button
                welcomeMsg = (TextView) findViewById(R.id.welcomeMsg);
                welcomeMsg.setVisibility(View.GONE);

            }
        }
        //In case of null Response
        else {
            Log.d(getPrefix(MainActivity.this), "The Response was null");
            String textToDisplay = "Ups! Something went wrong :( \n Please try again ";
            wsDataToDisplay.setMovementMethod(new ScrollingMovementMethod());
            wsDataToDisplay.setText(textToDisplay);
            Log.d(getPrefix(MainActivity.this), "The error message has been displayed");

            //Remove the welcomeMsg, and appear a recipe button
            welcomeMsg = (TextView) findViewById(R.id.welcomeMsg);
            welcomeMsg.setVisibility(View.GONE);
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
    }
}

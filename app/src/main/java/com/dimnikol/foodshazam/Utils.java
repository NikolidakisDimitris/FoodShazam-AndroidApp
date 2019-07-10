package com.dimnikol.foodshazam;

import android.content.Intent;
import android.graphics.Color;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import static com.dimnikol.foodshazam.Constants.FOOD_SHAZAM;
import static com.dimnikol.foodshazam.Constants.UTILS;
import static com.dimnikol.foodshazam.MainActivity.CAMERA_CODE;
import static com.dimnikol.foodshazam.MainActivity.GALLERY_CODE;

public class Utils {

    private Utils() {
    }


    public static void openCamera(MainActivity activity) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(cameraIntent, CAMERA_CODE);
    }

    public static void openGallery(MainActivity activity) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.setType("image/*");
        activity.startActivityForResult(galleryIntent, GALLERY_CODE);
    }


    public static String getPrefix(Object object) {
        String className = object.getClass().getSimpleName();
        return FOOD_SHAZAM + className ;
    }


    public static void resetTextView(TextView view) {
        if (view != null) {
            Log.d(UTILS + " > resetTextV", "Setting the text to null, and the background to transparent ");
            view.setText(null);
            view.setBackgroundColor(Color.parseColor("#00000000"));
        }
    }

    //Create String to display.
    public static String buildTextToDisplay(Response response) {

        String food = "The food is: " + response.getFood() + "\n";

        String text = "\n" + "If you want to cook it by your self, you can follow the next recipe" + "\n";

        String ingredients = "\n" + "The ingredients you need are: " + "\n";
        for (String current : response.getIngredient()) {
            ingredients += current + "\n";
        }

        String recipe = "\n" + "Steps" + "\n" + response.getRecipe();

        String textToDisplay = food + text + ingredients + recipe;

        return textToDisplay;
    }


}

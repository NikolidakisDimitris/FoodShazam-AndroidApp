package com.dimnikol.foodshazam;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;

public class UploadFileTask extends AsyncTask<Bitmap, String, Response> {

    private final TaskCallback callback;

    public UploadFileTask(TaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        if (callback != null) {
            callback.onTaskScheduled();
        }
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);

        if (callback != null) {
            callback.onTaskCompleted(response);
        }
    }

    @Override
    protected Response doInBackground(Bitmap... bitmaps) {
        if (bitmaps != null && bitmaps.length == 1) {
            final String rs = APIHelper.uploadBitmap(bitmaps[0]);

            if (rs != null && !rs.isEmpty()) {
                Gson gson = new Gson();
                return gson.fromJson(rs, Response.class);
            }
        }

        return null;
    }
}

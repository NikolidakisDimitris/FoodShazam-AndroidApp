package com.dimnikol.foodshazam;

public interface TaskCallback {

    public void onTaskScheduled();
    public void onTaskCompleted(Response response);


}

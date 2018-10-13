package com.alistairholmes.devjournal;

import android.app.Application;

import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;

public class InstabugClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //initialing instabug
        new Instabug.Builder(this,"YOUR_API_KEY")
                .setInvocationEvents(InstabugInvocationEvent.SHAKE, InstabugInvocationEvent.SCREENSHOT_GESTURE,
                        InstabugInvocationEvent.FLOATING_BUTTON, InstabugInvocationEvent.TWO_FINGER_SWIPE_LEFT)
                .build();
    }
}

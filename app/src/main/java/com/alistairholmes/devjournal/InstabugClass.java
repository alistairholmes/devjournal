package com.alistairholmes.devjournal;

import android.app.Application;

import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;

public class InstabugClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //initialing instabug
        new Instabug.Builder(this,"b38656bc256ef220cffbc4f47218bbd9")
                .setInvocationEvents(InstabugInvocationEvent.SHAKE, InstabugInvocationEvent.SCREENSHOT_GESTURE,
                        InstabugInvocationEvent.FLOATING_BUTTON, InstabugInvocationEvent.TWO_FINGER_SWIPE_LEFT)
                .build();
    }
}

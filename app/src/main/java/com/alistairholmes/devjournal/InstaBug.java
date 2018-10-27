package com.alistairholmes.devjournal;

import android.app.Application;

import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;

public class InstaBug extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        new Instabug.Builder(this, "<Your Token>")
                .setInvocationEvents(InstabugInvocationEvent.SHAKE, InstabugInvocationEvent.SCREENSHOT)
                .build();

    }
}

package com.alistairholmes.devjournal.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alistairholmes.devjournal.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mToolbar = findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            mToolbar.setNavigationOnClickListener(view -> onBackPressed());
        }

        LinearLayout [] containers = new LinearLayout[] {
                findViewById(R.id.container_about_app_website),
                findViewById(R.id.container_about_app_github),
                findViewById(R.id.container_about_app),
                findViewById(R.id.container_about_rate),
                findViewById(R.id.container_about_app_privacy),
                findViewById(R.id.container_about_donate),
                findViewById(R.id.container_about_bug),
                findViewById(R.id.container_about_author),
                findViewById(R.id.container_about_website),
                findViewById(R.id.container_about_instagram),
                findViewById(R.id.container_about_library1),
                findViewById(R.id.container_about_library2),
                findViewById(R.id.container_about_library3),
                findViewById(R.id.container_about_library4),
                findViewById(R.id.container_about_library5),
                findViewById(R.id.container_about_library6),
                findViewById(R.id.container_about_library10),
                findViewById(R.id.container_about_library11)};
        for (LinearLayout r : containers) {
            r.setOnClickListener((View.OnClickListener) this);
        }

        PackageManager manager = getApplicationContext().getPackageManager();

        try {
            PackageInfo info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
        }catch (PackageManager.NameNotFoundException e){
            return;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.container_about_app_website:
                goToURL("https://devjournal.ml");
                break;

            case R.id.container_about_app_privacy:
                goToURL("https://digitalnomadjobs.ml/privacy_policy.html");
                break;

            case R.id.container_about_author:
                goToURL("https://github.com/alistairholmes");
                break;

            case R.id.container_about_website:
                goToURL("https://alistair.ml");
                break;

            case R.id.container_about_instagram:
                goToURL("https://www.instagram.com/alistair.holmes/");
                break;

            case R.id.container_about_rate:
                goToURL("https://play.google.com/store/apps/details?id=com.alistairholmes.devjournal");
                break;

            case R.id.container_about_donate:
                goToURL("https://paypal.me/alistairholmes?locale.x=en_US");
                break;

            case R.id.container_about_bug:
                goToURL("https://github.com/alistairholmes/DevJournal/issues");
                break;

            case R.id.container_about_library1:
                goToURL("https://github.com/square/retrofit");
                break;

            case R.id.container_about_library2:
                goToURL("https://github.com/bumptech/glide");
                break;

            case R.id.container_about_library3:
                goToURL("https://developer.android.com/jetpack/androidx");
                break;

            case R.id.container_about_library4:
                goToURL("https://github.com/airbnb/lottie-android");
                break;

            case R.id.container_about_library5:
                goToURL("https://developer.android.com/topic/libraries/architecture/room");
                break;

            case R.id.container_about_library6:
                goToURL("https://github.com/ReactiveX/RxJava");
                break;

            case R.id.container_about_library10:
                goToURL("https://github.com/google/gson");
                break;

            case R.id.container_about_library11:
                goToURL("https://github.com/facebook/stetho");
                break;

        }
    }

    public void goToURL(String link) {
        Uri uri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }
}

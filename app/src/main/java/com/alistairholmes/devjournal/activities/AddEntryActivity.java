package com.alistairholmes.devjournal.activities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.alistairholmes.devjournal.R;
import com.alistairholmes.devjournal.database.AppDatabase;
import com.alistairholmes.devjournal.database.JournalEntry;
import com.alistairholmes.devjournal.utils.AlarmReceiver;
import com.alistairholmes.devjournal.viewmodels.AddEntryViewModel;
import com.alistairholmes.devjournal.viewmodels.AddEntryViewModelFactory;
import com.alistairholmes.devjournal.viewmodels.JournalViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.List;

public class AddEntryActivity extends AppCompatActivity {

    // Extra for the entry ID to be received in the intent
    public static final String EXTRA_ENTRY_ID = "extraEntryId";
    // Extra for the entry ID to be received after rotation
    public static final String INSTANCE_ENTRY_ID = "instanceEntryId";
    // Constant for default entry id to be used when not in update mode
    private static final int DEFAULT_ENTRY_ID = -1;
    // Constant for logging
    private static final String TAG = AddEntryActivity.class.getSimpleName();

    // Fields for views
    EditText titleEditText;
    EditText descriptionEditText;
    FloatingActionButton fabButton;
    private int mEntryId = DEFAULT_ENTRY_ID;

    // Member variable for the Database
    private AppDatabase mDb;

    // Member Variables for Notifications
    private NotificationManager mNotificationManager;

    // Constants for notification channels
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";


    protected void onCreate(Bundle savedInstanceState) {
        //must be called before setContentView(...)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        initViews();

        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_ENTRY_ID)) {
            mEntryId = savedInstanceState.getInt(INSTANCE_ENTRY_ID, DEFAULT_ENTRY_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ENTRY_ID)) {

            if (mEntryId == DEFAULT_ENTRY_ID) {
                // populate the UI
                mEntryId = intent.getIntExtra(EXTRA_ENTRY_ID, DEFAULT_ENTRY_ID);
                // Declare a AddTaskViewModelFactory using mDb and mTaskId
                AddEntryViewModelFactory factory = new AddEntryViewModelFactory(mDb, mEntryId);
                // Declare a AddTaskViewModel variable and initialize it by calling ViewModelProviders.of
                // for that use the factory created above AddTaskViewModel
                final AddEntryViewModel  viewModel =
                        ViewModelProviders.of(this, factory).get(AddEntryViewModel.class);
                // Observe the LiveData object in the ViewModel.
                viewModel.getEntries().observe(this, new Observer<JournalEntry>() {
                    @Override
                    public void onChanged(JournalEntry journalEntry) {
                        Log.d(TAG, "Receiving update from LiveData.");
                        populateUI(journalEntry);
                    }
                });
            }
            }

        // Reference to switch toggle
        SwitchCompat switchReminder = findViewById(R.id.switch_reminder);

        // Set up reminder
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent,
                PendingIntent.FLAG_NO_CREATE) != null);

        switchReminder.setChecked(alarmUp);

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create a notification channel
        createNotificationChannel();

        switchReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String toastMessage;
                if(isChecked){
                    //Set the toast message for the "on" case.
                    toastMessage = "A reminder has been set.";
                    long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                    long triggerTime = SystemClock.elapsedRealtime()
                            + repeatInterval;

                    //If the Toggle is turned on, set the repeating alarm with a 15 minute interval
                    if (alarmManager != null) {
                        alarmManager.setInexactRepeating
                                (AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                        triggerTime, repeatInterval, notifyPendingIntent);
                    }
                } else {

                    if (alarmManager != null) {
                        alarmManager.cancel(notifyPendingIntent);
                    }
                    //Cancel notification if the alarm is turned off
                    mNotificationManager.cancelAll();
                    //Set the toast message for the "off" case.
                    toastMessage = "No reminder set";
                }

                //Show a toast to say the alarm is turned on or off.
                Toast.makeText(AddEntryActivity.this, toastMessage, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

// Default Settings
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        // Read Settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        // Reminder
        String reminderPref = sharedPref.getString("reminder_frequency", "-1");
        // Theme
        String themePref = sharedPref.getString("theme_switch", "3");

        // Toast.makeText(this, themePref.toString(), Toast.LENGTH_LONG).show(); // Testing Purposes

        switch (themePref) {
            case "1": getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Light
                break;
            case "2": getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES); // Dark
                break;
            case "3": getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                break;
            case "-1": getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_ENTRY_ID, mEntryId);
        super.onSaveInstanceState(outState);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        titleEditText = findViewById(R.id.editTextTitle);
        descriptionEditText = findViewById(R.id.editTextEntryDescription);
        fabButton = findViewById(R.id.fab_update);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param entry the journalEntry to populate the UI
     */
    private void populateUI(JournalEntry entry) {
        // return if the entry is null
        if (entry == null) {
            return;
        }

        // use the variable entry to populate the UI
        titleEditText.setText(entry.getTitle());
        descriptionEditText.setText(entry.getDescription());

    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new entry data into the underlying database.
     */
    public void onSaveButtonClicked() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        Date date = new Date();


        final JournalEntry entry = new JournalEntry(title,description, date);
        com.alistairholmes.devjournal.utils.AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // insert the entry only if mEntryId matches DEFAULT_ENTRY_ID
                // Otherwise update it
                // call finish in any case
                if (mEntryId == DEFAULT_ENTRY_ID) {
                    // insert new task
                    JournalViewModel.insert(entry);
                } else {
                    //update task
                    entry.setId(mEntryId);
                    mDb.journalDao().updateEntry(entry);
                }
                finish();

            }
        });
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel() {
        // Create a notification manager object.
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "To Do reminder notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.MAGENTA);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifies every 15 minutes to do your task");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}

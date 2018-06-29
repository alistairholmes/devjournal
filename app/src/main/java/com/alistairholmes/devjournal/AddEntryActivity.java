package com.alistairholmes.devjournal;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.alistairholmes.devjournal.database.AppDatabase;
import com.alistairholmes.devjournal.database.JournalEntry;

import java.util.Date;

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
    Button mButton;

    private int mEntryId = DEFAULT_ENTRY_ID;

    // Member variable for the Database
    private AppDatabase mDb;

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
            mButton.setText("Update");
            if (mEntryId == DEFAULT_ENTRY_ID) {
                // populate the UI
                // Assign the value of EXTRA_ENTRY_ID in the intent to mEntryId
                // Use DEFAULT_ENTRY_ID as the default
                mEntryId = intent.getIntExtra(EXTRA_ENTRY_ID, DEFAULT_ENTRY_ID);
                // Get the diskIO Executor from the instance of AppExecutors and
                // call the diskIO execute method with a new Runnable and implement its run method
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Use the loadEntryById method to retrieve the entry with id mEntryId and
                        // assign its value to a final JournalEntry variable
                        final JournalEntry entry = mDb.journalDao().loadEntryById(mEntryId);
                        // CCall the populateUI method with the retrieve entries
                        // Remember to wrap it in a call to runOnUiThread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                populateUI(entry);
                            }
                        });
                    }
                });
            }
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
        titleEditText = findViewById(R.id.editTextEntryTitle);
        descriptionEditText = findViewById(R.id.editTextEntryDescription);

        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
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

        // use the variable task to populate the UI
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
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // insert the entry only if mEntryId matches DEFAULT_ENTRY_ID
                // Otherwise update it
                // call finish in any case
                if (mEntryId == DEFAULT_ENTRY_ID) {
                    // insert new task
                    mDb.journalDao().insertEntry(entry);
                } else {
                    //update task
                    entry.setId(mEntryId);
                    mDb.journalDao().updateEntry(entry);
                }
                finish();
            }
        });
    }
}

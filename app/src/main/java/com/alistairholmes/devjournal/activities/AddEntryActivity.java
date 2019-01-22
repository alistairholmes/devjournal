package com.alistairholmes.devjournal;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.alistairholmes.devjournal.database.AppDatabase;
import com.alistairholmes.devjournal.database.JournalEntry;
import com.alistairholmes.devjournal.viewmodels.AddEntryViewModel;
import com.alistairholmes.devjournal.viewmodels.AddEntryViewModelFactory;
import com.alistairholmes.devjournal.viewmodels.JournalViewModel;

import java.util.Date;

public class AddEntryActivity extends FragmentActivity {

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
                        ViewModelProviders.of(this).get(AddEntryViewModel.class);
                // Observe the LiveData object in the ViewModel.
                viewModel.getEntries().observe((LifecycleOwner) this, new Observer<JournalEntry>() {
                    @Override
                    public void onChanged(JournalEntry journalEntry) {
                        Log.d(TAG, "Receiving update from LiveData.");
                        populateUI(journalEntry);
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
        com.alistairholmes.devjournal.AppExecutors.getInstance().diskIO().execute(new Runnable() {
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
}

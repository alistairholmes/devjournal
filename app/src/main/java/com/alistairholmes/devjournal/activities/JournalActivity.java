package com.alistairholmes.devjournal.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.paging.PagedList;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.alistairholmes.devjournal.activities.AddEntryActivity;
import com.alistairholmes.devjournal.utils.AppExecutors;
import com.alistairholmes.devjournal.adapters.JournalAdapter;
import com.alistairholmes.devjournal.R;
import com.alistairholmes.devjournal.database.AppDatabase;
import com.alistairholmes.devjournal.database.JournalEntry;
import com.alistairholmes.devjournal.viewmodels.JournalViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Calendar;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class JournalActivity extends AppCompatActivity implements com.alistairholmes.devjournal.adapters.JournalAdapter.ItemClickListener{

    // Constant for logging
    private static final String TAG = JournalActivity.class.getSimpleName();

    // Firebase Auth for signing out
    private FirebaseAuth mAuth;

    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private com.alistairholmes.devjournal.adapters.JournalAdapter mAdapter;

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //must be called before setContentView(...)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Default Settings
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        // Read Settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        // User Name
        TextView username = findViewById(R.id.tv_username);
        username.setText(sharedPref.getString("username", ""));
        // Reminder
        String reminderPref = sharedPref.getString("reminder_frequency", "-1");
        // Theme
        String themePref = sharedPref.getString("theme_switch", "3");

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

        // Get current time
        Calendar c = Calendar.getInstance(); // Get current time
        // Gets the current hour of the day from the calendar created ( from 1 to 24 )
        int hr1 = c.get(Calendar.HOUR_OF_DAY);

        TextView welcomeMsg = findViewById(R.id.tv_welcome);

        if(hr1<12) {
            welcomeMsg.setText("Good Morning");
        } else if(hr1>12&& hr1<17) {
            welcomeMsg.setText("Good Afternoon");
        } else if(hr1>17&& hr1<20) {
            welcomeMsg.setText("Good Evening");
        }

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerView_journalEntries);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new JournalAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(mRecyclerView.getContext(),
                VERTICAL);
        mRecyclerView.addItemDecoration(decoration);


        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final int position = viewHolder.getAdapterPosition();
                        final List<JournalEntry> entries = mAdapter.getEntries();
                        mDb.journalDao().deleteEntry(entries.get(position));

                        View parentView = findViewById(R.id.coordinator_layout);
                        Snackbar snackbar = Snackbar.make(parentView, "Todo Item Deleted", Snackbar.LENGTH_LONG)
                                                    .setAction("UNDO", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            // Restore deleted note

                                                        }
                                                    });
                        snackbar.show();
                    }

                });
            }
        }).attachToRecyclerView(mRecyclerView);

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddEntryActivity.
         */
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddEntryActivity
                Intent addEntryIntent = new Intent(JournalActivity.this, AddEntryActivity.class);
                startActivity(addEntryIntent);
            }
        });

        // Get the search intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
           // doMySearch(query);
        }


        mDb = AppDatabase.getInstance(getApplicationContext());

        setupViewModel();

    }

    private void setupViewModel() {

        JournalViewModel viewModel = ViewModelProviders.of(this).get(JournalViewModel.class);
        viewModel.getEntries().observe(this, new Observer<List<JournalEntry>>() {

            @Override
                public void onChanged(@Nullable List<JournalEntry> journalEntries) {
                    Log.d(TAG, "Receiving database update from LiveData.");
                    mAdapter.setEntries(journalEntries);
                }
            });
        }


    @Override
    public void onItemClickListener(int itemId) {
        // Launch ViewEntryActivity adding the itemId as an extra in the intent
        // Launch ViewEntryActivity with itemId as extra for the key ViewEntryActivity.EXTRA_ENTRY_ID
        Intent intent = new Intent(JournalActivity.this, AddEntryActivity.class);
        intent.putExtra(AddEntryActivity.EXTRA_ENTRY_ID, itemId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.menu_actionbar, menu);

        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu.
     * @param item The menu item that was selected by the user
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            Intent intent = new Intent(JournalActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.menu_sign_out) {
            signOut();
            Intent intent = new Intent(JournalActivity.this, SignUpActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.menu_about) {
            Intent intent = new Intent(JournalActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Signs user out
    private void signOut() {
        mAuth.signOut();
    }
}

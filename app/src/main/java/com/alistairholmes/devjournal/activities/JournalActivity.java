package com.alistairholmes.devjournal.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.paging.PagedList;
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

import java.util.List;

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
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final int position = viewHolder.getAdapterPosition();
                        final List<JournalEntry> entries = mAdapter.getEntries();
                        mDb.journalDao().deleteEntry(entries.get(position));

                        View parentView = findViewById(R.id.coordinator_layout);
                        Snackbar snackbar = Snackbar.make(parentView, "Todo Item Deleted",
                                Snackbar.LENGTH_LONG);
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

        return super.onOptionsItemSelected(item);
    }

    // Signs user out
    private void signOut() {
        mAuth.signOut();
    }
}

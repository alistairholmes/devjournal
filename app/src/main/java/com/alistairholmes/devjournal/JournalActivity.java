package com.alistairholmes.devjournal;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alistairholmes.devjournal.database.AppDatabase;
import com.alistairholmes.devjournal.database.JournalEntry;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class JournalActivity extends AppCompatActivity implements JournalAdapter.ItemClickListener{

    // Constant for logging
    private static final String TAG = JournalActivity.class.getSimpleName();

    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private JournalAdapter mAdapter;


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
                        int position = viewHolder.getAdapterPosition();
                        List<JournalEntry> entries = mAdapter.getEntries();
                        mDb.journalDao().deleteEntry(entries.get(position));
                        retrieveEntries();
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
    }

    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddTaskActivity,
     * so this re-queries the database data for any changes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        retrieveEntries();
    }

    private void retrieveEntries() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<JournalEntry> entries = mDb.journalDao().loadAllEntries();
                // We will be able to simplify this once we learn more
                // about Android Architecture Components
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setEntries(entries);
                    }
                });
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


}

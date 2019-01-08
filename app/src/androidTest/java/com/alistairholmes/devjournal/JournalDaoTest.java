package com.alistairholmes.devjournal;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.alistairholmes.devjournal.database.AppDatabase;
import com.alistairholmes.devjournal.database.JournalDao;
import com.alistairholmes.devjournal.database.JournalEntry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class JournalDaoTest {

    // InstantTaskExecutorRule is a JUnit Test Rule that swaps the background executor used by the
    // Architecture Components with a different one which executes each task synchronously.
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private JournalDao mJournalDao;
    private AppDatabase mDatabase;

    // This creates the database
    // inMemoryDatabaseBuilder method helps us to create an in-memory room database instance
    @Before
    public void initDb() throws Exception {
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        mJournalDao = mDatabase.journalDao();
    }

    // This closes the database
    @After
    public void closeDb() throws Exception {
        mDatabase.close();
    }

    // For every test we are closing the database and re-creating it again before
    // running another test, this is called testing in isolation


    @Test
    public void onFetchingNotes_shouldGetEmptyList_IfTable_IsEmpty() throws Exception {
        List<JournalEntry> journalEntryList = mJournalDao.loadAllEntries();
        assertTrue(journalEntryList.isEmpty());
    }


}

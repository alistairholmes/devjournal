package com.alistairholmes.devjournal;

import android.content.Context;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.alistairholmes.devjournal.database.AppDatabase;
import com.alistairholmes.devjournal.database.JournalDao;
import com.alistairholmes.devjournal.database.JournalEntry;
import com.alistairholmes.devjournal.utils.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private JournalDao journalDao;
    private AppDatabase db;
    private static Date date = new GregorianCalendar(2019, Calendar.OCTOBER, 29).getTime();

    private static int JOURNAL_ID = 1;
    private static JournalEntry JOURNAL_DEMO =
            new JournalEntry(1, "Testing Database",
                    "Room tested espresso",
                    date);

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        journalDao = db.journalDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeNoteAndReadInList() throws Exception {
        this.db.journalDao().insertEntry(JOURNAL_DEMO);
        JournalEntry journalEntry = LiveDataTestUtil.getValue(this.db.journalDao().loadEntryById(JOURNAL_ID));
        assertTrue(journalEntry.getTitle().equals(JOURNAL_DEMO.getTitle()) && journalEntry.getId() == JOURNAL_ID);
    }

}

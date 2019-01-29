package com.alistairholmes.devjournal.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import java.util.List;

// Tutorial for this Repository class:
// https://google-developer-training.gitbooks.io/android-developer-advanced-course-practicals/content/unit-6-working-with-architecture-components/lesson-14-room,-livedata,-viewmodel

public class JournalRepository {

    private JournalDao mJournalDao;
    private LiveData<List<JournalEntry>> mJournalEntries;

    public JournalRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mJournalDao = db.journalDao();
        mJournalEntries = mJournalDao.getEntries();
    }

    public LiveData<List<JournalEntry>> getEntries() {
        return mJournalEntries;
    }

    public void insert(JournalEntry journalEntry) {
        new insertAsyncTask(mJournalDao).execute(journalEntry);
    }

    private static class insertAsyncTask extends AsyncTask<JournalEntry, Void, Void> {

        private JournalDao mAsyncTaskDao;

        insertAsyncTask(JournalDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final JournalEntry... journalEntries) {
            mAsyncTaskDao.insertEntry(journalEntries);
            return null;
        }
    }
}

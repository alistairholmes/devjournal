package com.alistairholmes.devjournal.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alistairholmes.devjournal.database.AppDatabase;
import com.alistairholmes.devjournal.database.JournalEntry;

public class AddEntryViewModel extends ViewModel {

    private LiveData<JournalEntry> entry;

    // Note: The constructor should receive the database and the taskId
    public AddEntryViewModel(AppDatabase database, int entryId) {
        entry = database.journalDao().loadEntryById(entryId);
    }

    public LiveData<JournalEntry> getEntries() {
        return entry;
    }
}

package com.alistairholmes.devjournal.viewmodels;

import android.app.Application;
import android.support.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.alistairholmes.devjournal.database.AppDatabase;
import com.alistairholmes.devjournal.database.JournalEntry;

import java.util.List;

public class JournalViewModel extends AndroidViewModel {

    private LiveData<List<JournalEntry>> entries;

    public JournalViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        entries = database.journalDao().loadAllEntries();
    }

    public LiveData<List<JournalEntry>> getEntries() {
        return entries;
    }
}

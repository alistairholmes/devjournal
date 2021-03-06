package com.alistairholmes.devjournal.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.alistairholmes.devjournal.database.JournalEntry;
import com.alistairholmes.devjournal.database.JournalRepository;

import java.util.List;

public class JournalViewModel extends AndroidViewModel {

    private static JournalRepository mRepository;
    private LiveData<List<JournalEntry>> entries;

    public JournalViewModel(@NonNull Application application) {
        super(application);
        mRepository = new JournalRepository(application);
        entries = mRepository.getEntries();
    }

    public LiveData<List<JournalEntry>> getEntries() {
        return entries;
    }

    public static void insert(JournalEntry journalEntry) {
        mRepository.insert(journalEntry);
    }
}

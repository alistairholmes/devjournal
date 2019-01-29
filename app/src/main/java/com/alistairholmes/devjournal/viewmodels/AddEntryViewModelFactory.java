package com.alistairholmes.devjournal.viewmodels;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory;

import com.alistairholmes.devjournal.database.AppDatabase;

public class AddEntryViewModelFactory extends NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mEntryId;

    public AddEntryViewModelFactory(AppDatabase database, int entryId ) {
        mDb = database;
        mEntryId = entryId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new AddEntryViewModel(mDb, mEntryId);
    }

}

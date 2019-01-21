package com.alistairholmes.devjournal.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface JournalDao {

    @Query("SELECT * FROM `Journal Entries` ORDER BY date DESC ")
    LiveData<List<JournalEntry>> loadAllEntries();

    @Insert
    void insertEntry(JournalEntry journalEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEntry(JournalEntry journalEntry);

    @Delete
    void deleteEntry(JournalEntry journalEntry);

    // Create a Query method named loadEntryById that receives an int id and returns a JournalEntry Object
    // The query for this method should get all the data for that id in the task table
    @Query("SELECT * FROM `Journal Entries` WHERE id = :id")
    LiveData<JournalEntry> loadEntryById(int id);
}

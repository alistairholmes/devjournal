package com.alistairholmes.devjournal.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface JournalDao {

    @Query("SELECT * FROM `Journal Entries`")
    List<JournalEntry> loadAllEntries();

    @Insert
    void insertEntry(JournalEntry journalEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEntry(JournalEntry journalEntry);

    @Delete
    void deleteEntry(JournalEntry journalEntry);

    // Create a Query method named loadEntryById that receives an int id and returns a JournalEntry Object
    // The query for this method should get all the data for that id in the task table
    @Query("SELECT * FROM `Journal Entries` WHERE id = :id")
    JournalEntry loadEntryById(int id);
}

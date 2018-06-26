package com.alistairholmes.devjournal.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Journal Entries")
public class JournalEntry {

    //Variables names for the columns in the database table
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    @ColumnInfo(name = "updated_at")
    private Date updated_at;

    @Ignore
    public JournalEntry(String title, String description, Date updated_at) {
        this.title = title;
        this.description = description;
        this.updated_at = updated_at;
    }

    public JournalEntry(int id, String title, String description, Date updated_at) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.updated_at = updated_at;
    }

    //Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public static Date getUpdatedAt() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}

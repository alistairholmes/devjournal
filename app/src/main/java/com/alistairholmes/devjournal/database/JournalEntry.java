package com.alistairholmes.devjournal.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Journal Entries")
public class JournalEntry {

    //Variables names for the columns in the database table
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private Date date;

    @Ignore
    public JournalEntry(String title, String description, Date date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public JournalEntry(int id, String title, String description, Date date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    //Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String s) {
        this.title = s;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

}

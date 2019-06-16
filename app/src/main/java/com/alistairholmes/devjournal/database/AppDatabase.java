package com.alistairholmes.devjournal.database;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;
import android.util.Log;

@Database(entities = {JournalEntry.class/*, EntryFTS.class*/}, version = 2, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "journal";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .addMigrations(new Migration(2,3) {
                            @Override
                            public void migrate(@NonNull SupportSQLiteDatabase database) {
                                database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `entriesFts` USING FTS4(`title`, `description`, `date`, content=`Journal Entries`)");
                                database.execSQL("INSERT INTO entriesFts(entriesFts) VALUES ('rebuild')");
                            }
                        })
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract JournalDao journalDao();
}

package com.example.profinal2019.Model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {DogEntity.class}, version = 1)
public abstract class DogDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "DogDatabase.db";
    public static volatile DogDatabase instance;
    public static final Object LOCK = new Object();

    public abstract DogDao dogDao();

    public static DogDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            DogDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return instance;
    }
}

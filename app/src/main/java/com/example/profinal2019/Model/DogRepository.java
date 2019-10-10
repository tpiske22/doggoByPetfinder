package com.example.profinal2019.Model;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DogRepository {
    public static DogRepository instance;

    public LiveData<List<DogEntity>> dogs;
    private DogDatabase database;
    private Executor executor = Executors.newSingleThreadExecutor();

    public static DogRepository getInstance(Context context) {
        if (instance == null) {
            instance = new DogRepository(context);
        }
        return instance;
    }

    private DogRepository(Context context) {
        this.database = DogDatabase.getInstance(context);
        this.dogs = getAllDogs();
    }

    public void insertDog(final DogEntity dog) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.dogDao().insertDog(dog);
            }
        });
    }

    public void insertDogs(final List<DogEntity> dogs) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.dogDao().insertDogs(dogs);
            }
        });
    }

    public void deleteDog(final DogEntity dog) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.dogDao().deleteDog(dog);
            }
        });
    }

    public DogEntity getDogById(int id) {
        return database.dogDao().getDogById(id);
    }

    public LiveData<List<DogEntity>> getAllDogs() {
        return database.dogDao().getAllDogs();
    }

    public void deleteAll() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.dogDao().deleteAll();
            }
        });
    }

    public int getCountById(int id) {
        return database.dogDao().getCountById(id);
    }
}

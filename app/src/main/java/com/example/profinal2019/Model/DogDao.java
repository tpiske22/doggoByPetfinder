package com.example.profinal2019.Model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDog(DogEntity dog);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDogs(List<DogEntity> dogs);

    @Delete
    void deleteDog(DogEntity dog);

    @Query("SELECT * FROM dogs WHERE id = :id")
    DogEntity getDogById(int id);

    @Query("SELECT * FROM dogs")
    LiveData<List<DogEntity>> getAllDogs();

    @Query("DELETE FROM dogs")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM dogs WHERE id = :id")
    int getCountById(int id);
}

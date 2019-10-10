package com.example.profinal2019.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.profinal2019.Model.DogEntity;
import com.example.profinal2019.Model.DogRepository;

import java.util.List;

public class SavedViewModel extends AndroidViewModel {

    private DogRepository repository;
    public LiveData<List<DogEntity>> dogs;

    public SavedViewModel(@NonNull Application application) {
        super(application);

        repository = DogRepository.getInstance(application.getApplicationContext());
        dogs = repository.getAllDogs();
    }

    public void insertDog(DogEntity dog) { repository.insertDog(dog); }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void deleteDog(DogEntity dog) {
        repository.deleteDog(dog);
    }
}

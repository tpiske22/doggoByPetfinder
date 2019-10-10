package com.example.profinal2019.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.profinal2019.Model.DogEntity;
import com.example.profinal2019.Model.DogRepository;

public class DetailViewModel extends AndroidViewModel {

    private DogRepository repository;

    public DetailViewModel(@NonNull Application application) {
        super(application);

        repository = DogRepository.getInstance(application.getApplicationContext());
    }

    public void insertDog(DogEntity dog) { repository.insertDog(dog); }

    public void deleteDog(DogEntity dog) { repository.deleteDog(dog); }

    public int getCountById(int id) { return repository.getCountById(id); }
}

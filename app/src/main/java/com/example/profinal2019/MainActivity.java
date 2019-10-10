package com.example.profinal2019;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.example.profinal2019.Model.DogEntity;

// FOR THAT CRYPTIC ERROR ABOUT YOUR GRADLE DEPENDENCIES
// run ./gradlew clean

public class MainActivity extends AppCompatActivity {

    public static final String LAST_TOKEN_REQUEST = "LAST_TOKEN_REQUEST";
    public static final String API_ACCESS_TOKEN = "API_ACCESS_TOKEN";
    public static final String HI_RES_IMAGES = "HI_RES_IMAGES";
    public static final String SEARCH_SIZE = "SEARCH_SIZE";
    public static final String SEARCH_GENDER = "SEARCH_GENDER";
    public static final String SEARCH_AGE = "SEARCH_AGE";
    public static final String SEARCH_ZIP = "SEARCH_ZIP";
    public static final String FIRST_LAUNCH = "FIRST_LAUNCH";

    private SearchFragment searchFragment;
    private SavedFragment savedFragment;
    private SettingsFragment settingsFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    displaySearch();
                    return true;
                case R.id.navigation_saved:
                    displaySaved();
                    return true;
                case R.id.navigation_settings:
                    displaySettings();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        searchFragment = SearchFragment.newInstance();
        savedFragment = SavedFragment.newInstance();
        settingsFragment = SettingsFragment.newInstance();

        initializeFragments();
    }

    private void displaySearch() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.show(searchFragment);
        ft.hide(savedFragment);
        ft.hide(settingsFragment);
        ft.commit();
    }

    private void displaySaved() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(searchFragment);
        ft.show(savedFragment);
        ft.hide(settingsFragment);
        ft.commit();
    }

    private void displaySettings() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(searchFragment);
        ft.hide(savedFragment);
        ft.show(settingsFragment);
        ft.commit();
    }

    private void initializeFragments() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragmentFrame, searchFragment, "SEARCH_FRAGMENT");
        ft.add(R.id.fragmentFrame, savedFragment, "SAVED_FRAGMENT");
        ft.add(R.id.fragmentFrame, settingsFragment, "SETTINGS_FRAGMENT");
        ft.hide(savedFragment); ft.hide(settingsFragment);
        ft.commit();
    }

    public void showDogDetails(DogEntity dog) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.SERIALIZED_DOG, dog);
        startActivity(intent);
    }

    public void insertDog(DogEntity dog) {
        savedFragment.insertDog(dog);
    }
}
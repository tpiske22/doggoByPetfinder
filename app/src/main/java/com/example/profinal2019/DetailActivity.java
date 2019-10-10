package com.example.profinal2019;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.profinal2019.Model.DogEntity;
import com.example.profinal2019.ViewModel.DetailViewModel;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class DetailActivity extends AppCompatActivity {

    FloatingActionButton saveButton;
    ImageView detailDogImage;
    TextView nameText;
    TextView descriptionText;
    TextView breedText;
    TextView genderText;
    TextView ageText;
    TextView sizeText;
    TextView desexedLabel;
    TextView desexedText;
    TextView houseTrainedText;
    TextView specialNeedsText;
    TextView organizationIDText;
    TextView phoneText;
    TextView streetText;
    TextView cityZipText;
    Button callButton;
    Button mapButton;
    Button petFinderPageButton;

    private DogEntity dog;
    private boolean isDogSaved;
    private DetailViewModel viewModel;

    public static final String SERIALIZED_DOG = "SERIALIZED_DOG";
    private static final int PERMISSION_REQUEST_PHONE = 245;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);

        dog = (DogEntity)getIntent().getSerializableExtra(SERIALIZED_DOG);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setImageResource(isDogSaved ? R.drawable.ic_remove_black_24dp : R.drawable.ic_add_black_24dp);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDogSaved) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                    builder.setMessage(R.string.dialog_delete_dog_message);
                    builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            viewModel.deleteDog(dog);
                            setSavedStatus(!isDogSaved);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    viewModel.insertDog(dog);

                    setSavedStatus(!isDogSaved);
                }
            }
        });

        Executor executor = Executors.newSingleThreadExecutor();
        try {
            FutureTask<Boolean> future = new FutureTask<>(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    return viewModel.getCountById(dog.getId()) > 0;
                }
            });
            executor.execute(future);
            setSavedStatus(future.get());
        }
        catch (Exception e) { setSavedStatus(false); }

        detailDogImage = findViewById(R.id.detailDogImage);
        Glide.with(this)
                .load(dog.getPictureUrl())
                .apply(new RequestOptions()
                        .override(detailDogImage.getWidth(), detailDogImage.getHeight())
                        .fitCenter()
                        .circleCrop()
                        .error(R.drawable.doggo_placeholder))
                .into(detailDogImage);

        nameText = findViewById(R.id.nameText);
        nameText.setText(dog.getName());

        descriptionText = findViewById(R.id.descriptionText);
        descriptionText.setText(dog.getDescription());

        breedText = findViewById(R.id.breedText);
        breedText.setText(dog.getPrimaryBreed() + (dog.isMixed() ? " Mix" : ""));

        genderText = findViewById(R.id.genderText);
        genderText.setText(dog.getGender());

        ageText = findViewById(R.id.ageText);
        ageText.setText(dog.getAge());

        sizeText = findViewById(R.id.sizeText);
        sizeText.setText(dog.getSize());

        desexedLabel = findViewById(R.id.desexedLabel);
        desexedLabel.setText(getResources().getString(
                dog.getGender().equalsIgnoreCase("male") ? R.string.label_neutered : R.string.label_spayed));

        desexedText = findViewById(R.id.desexedText);
        desexedText.setText(getResources().getString(
                dog.isDesexed() ? R.string.label_yes : R.string.label_no));

        houseTrainedText = findViewById(R.id.houseTrainedText);
        houseTrainedText.setText(getResources().getString(
                dog.isHouseTrained() ? R.string.label_yes : R.string.label_no));

        specialNeedsText = findViewById(R.id.specialNeedsText);
        specialNeedsText.setText(getResources().getString(
                dog.hasSpecialNeeds() ? R.string.label_yes : R.string.label_no));

        organizationIDText = findViewById(R.id.organizationIDText);
        organizationIDText.setText(dog.getOrganizationID());

        phoneText = findViewById(R.id.phoneText);
        phoneText.setText(dog.getPhone());

        streetText = findViewById(R.id.streetText);
        streetText.setText(dog.getAddress());

        cityZipText = findViewById(R.id.cityZipText);
        cityZipText.setText(dog.getCity() + ", " + dog.getState() + " " + dog.getZip());

        callButton = findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dog.getPhone().equalsIgnoreCase("Not Provided")) {
                    return;
                }
                else {
                    // https://stackoverflow.com/questions/4816683/how-to-make-a-phone-call-programmatically
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_PHONE);
                    }
                    else {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + dog.getPhone()));
                        DetailActivity.this.startActivity(intent);
                    }
                }
            }
        });
        callButton.setText(getResources().getText(
                dog.getPhone().equalsIgnoreCase("Not Provided") ? R.string.label_na : R.string.label_call));

        mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dog.getAddress().equalsIgnoreCase("Not Provided") ||
                        dog.getCity().equalsIgnoreCase("Not Provided") ||
                        dog.getState().equalsIgnoreCase("Not Provided")) {
                    return;
                }
                else {
                    // https://developers.google.com/maps/documentation/urls/android-intents
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + streetText.getText().toString() + " " +
                            cityZipText.getText().toString());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });
        mapButton.setText(getResources().getText(
                (dog.getAddress().equalsIgnoreCase("Not Provided") ||
                        dog.getCity().equalsIgnoreCase("Not Provided") ||
                        dog.getState().equalsIgnoreCase("Not Provided")) ? R.string.label_na : R.string.label_map));

        petFinderPageButton = findViewById(R.id.petFinderPageButton);
        petFinderPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dog.getPetFinderUrl().equalsIgnoreCase("Not Provided")) {
                    return;
                }
                else {
                    // https://stackoverflow.com/questions/2201917/how-can-i-open-a-url-in-androids-web-browser-from-my-application
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dog.getPetFinderUrl()));
                    startActivity(browserIntent);
                }
            }
        });
        petFinderPageButton.setText(getResources().getText(
                dog.getPetFinderUrl().equalsIgnoreCase("Not Provided") ? R.string.label_na : R.string.label_to_site));

    }

    private void setSavedStatus(boolean status) {
        isDogSaved = status;
        saveButton.setImageResource(isDogSaved ? R.drawable.ic_remove_black_24dp : R.drawable.ic_add_black_24dp);
    }
}

package com.example.profinal2019;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.profinal2019.Model.DogEntity;
import com.example.profinal2019.UI.DogAdapter;

public class SettingsFragment extends Fragment {

    private EditText zipEntry;
    private FloatingActionButton getZipButton;
    private Spinner sizeSpinner;
    private Spinner genderSpinner;
    private Spinner ageSpinner;
    private Switch hiResSwitch;

    public SettingsFragment() { }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hiResSwitch = view.findViewById(R.id.hiResSwitch);
        hiResSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(MainActivity.HI_RES_IMAGES, false));
        hiResSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean(MainActivity.HI_RES_IMAGES, isChecked).apply();
            }
        });

        // populate spinners and set them to persisted settings values
        ////
        sizeSpinner = view.findViewById(R.id.sizeSpinner);
        ArrayAdapter<CharSequence> sizesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sizes_array, R.layout.support_simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(sizesAdapter);
        sizeSpinner.setSelection(PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(MainActivity.SEARCH_SIZE, 0));
        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit().putInt(MainActivity.SEARCH_SIZE, position).apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        genderSpinner = view.findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.genders_array, R.layout.support_simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setSelection(PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(MainActivity.SEARCH_GENDER, 0));
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit().putInt(MainActivity.SEARCH_GENDER, position).apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        ageSpinner = view.findViewById(R.id.ageSpinner);
        ArrayAdapter<CharSequence> ageAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.ages_array, R.layout.support_simple_spinner_dropdown_item);
        ageSpinner.setAdapter(ageAdapter);
        ageSpinner.setSelection(PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(MainActivity.SEARCH_AGE, 0));
        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit().putInt(MainActivity.SEARCH_AGE, position).apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // set zip to currently saved zip or current geolocation
        ////
        zipEntry = view.findViewById(R.id.zipEntry);
        zipEntry.setText(Integer.toString(PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(MainActivity.SEARCH_ZIP, 0)));

        // https://stackoverflow.com/questions/19217582/implicit-submit-after-hitting-done-on-the-keyboard-at-the-last-edittext
        zipEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    int zip = Integer.parseInt(zipEntry.getText().toString());

                    if (zip < 10000 || zip > 99999) {
                        showPopup(R.string.dialog_bad_zip_message);
                        zipEntry.getText().clear();
                    }
                    else {
                        PreferenceManager.getDefaultSharedPreferences(getActivity())
                                .edit().putInt(MainActivity.SEARCH_ZIP, zip).apply();
                    }

                    // https://stackoverflow.com/questions/3553779/android-dismiss-keyboard/3553966
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(zipEntry.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        getZipButton = view.findViewById(R.id.getZipButton);
        getZipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int zip = SearchFragment.getZip(SettingsFragment.this.getActivity());

                if (zip == -1) {
                    showPopup(R.string.dialog_zip_malfunction_message);
                }
                else {
                    zipEntry.setText(Integer.toString(PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getInt(MainActivity.SEARCH_ZIP, 0)));
                }
            }
        });
    }

    private void showPopup(int rString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(rString);
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

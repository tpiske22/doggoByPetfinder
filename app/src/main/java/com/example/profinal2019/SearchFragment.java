package com.example.profinal2019;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.profinal2019.Model.DogEntity;
import com.example.profinal2019.UI.DogAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment implements DogAdapter.DogWalkable {

    public static String api_access_token;
    public static final int PERMISSION_REQUEST_LOCATION = 250;
    private static final int API_TOKEN_TIMEOUT = 20000; // milliseconds
    private static final int API_RESPONSE_TIMEOUT = 5000; // milliseconds

    RecyclerView recyclerView;
    FloatingActionButton refreshButton;

    List<DogEntity> dogs = new ArrayList<>();
    DogAdapter adapter;

    List<String> sizes = new ArrayList<>();
    List<String> genders = new ArrayList<>();
    List<String> ages = new ArrayList<>();

    public SearchFragment() { }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sizes.add("No Filter"); sizes.add("Small"); sizes.add("Medium"); sizes.add("Large");
        genders.add("No Filter"); genders.add("Female"); genders.add("Male");
        ages.add("No Filter"); ages.add("Baby"); ages.add("Young"); ages.add("Adult"); ages.add("Senior");

        // begin search after welcome message on first launch
        if (!PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(MainActivity.FIRST_LAUNCH, true)) {
            searchForDoggos();
        }
        adapter = new DogAdapter(this, dogs);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(
                recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);

        refreshButton = view.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchForDoggos();
            }
        });

        if (PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(MainActivity.FIRST_LAUNCH, true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_welcome_message);
            builder.setPositiveButton(R.string.dialog_thanks, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    searchForDoggos();
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .edit().putBoolean(MainActivity.FIRST_LAUNCH, false).apply();
        }
    }

    @Override
    public void walkDetailDogToMain(DogEntity dog) {
        MainActivity main = (MainActivity)getActivity();
        main.showDogDetails(dog);
    }

    private void searchForDoggos() {
        long lastAccess = PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong(MainActivity.LAST_TOKEN_REQUEST, 0);

        // check that your access token hasn't expired
        if ((System.currentTimeMillis()/1000 - lastAccess) >= 3600) {
            AccessTokenRetriever tokenRetriever = new AccessTokenRetriever();
            tokenRetriever.execute();
        } else {
            if (api_access_token == null) { api_access_token = PreferenceManager.getDefaultSharedPreferences(
                    getActivity()).getString(MainActivity.API_ACCESS_TOKEN, ""); }

            DoggoResultsRetriever resultsRetriever = new DoggoResultsRetriever();
            resultsRetriever.execute();
        }
    }

    private class AccessTokenRetriever extends AsyncTask<Void, Void, JSONObject> {

        AlertDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_token_message);
            builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            // https://stackoverflow.com/questions/4205980/java-sending-http-parameters-via-post-method-easily
            try {
                String msg = "grant_type=client_credentials&" +
                        "client_id=" + "v4hBevckPdca7VdwW7sL4VgZdhqoG1aymT4iTEjIYy27KhTkru" + "&" +
                        "client_secret=" + "GLgKgyg2GHukVPVNdwCODHA57m4k1pvVg2EdyweA";
                byte[] postMsg = msg.getBytes(StandardCharsets.UTF_8);
                int postDataLength = postMsg.length;

                String request = "https://api.petfinder.com/v2/oauth2/token";
                URL url = new URL(request);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                connection.setUseCaches(false);
                connection.setReadTimeout(API_TOKEN_TIMEOUT);

                DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
                writer.write(postMsg);

                Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

                StringBuilder builder = new StringBuilder();
                for (int c; (c = reader.read()) >= 0;)
                    builder.append((char)c);

                return new JSONObject(builder.toString());
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            dismissDialog();
            if (jsonObject != null) {
                try {
                    api_access_token = jsonObject.getString("access_token");
                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .edit().putString(MainActivity.API_ACCESS_TOKEN, api_access_token).apply();

                    long currentTime = System.currentTimeMillis() / 1000;
                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .edit().putLong(MainActivity.LAST_TOKEN_REQUEST, currentTime).apply();

                    // execute a dog results retriever after user dismisses welcome message
                    DoggoResultsRetriever retriever = new DoggoResultsRetriever();
                    retriever.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                showErrorDialog();
            }
        }

        private void dismissDialog() { if (dialog != null) dialog.dismiss(); }
    }

    private class DoggoResultsRetriever extends AsyncTask<Void, Void, JSONObject> {

        private AlertDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_refresh_message);
            builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    cancel(true);
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... TheVoidz) {
            try {
                URL url = createSearchURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Authorization", "Bearer " + api_access_token);
                connection.setReadTimeout(API_RESPONSE_TIMEOUT);

                Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

                StringBuilder builder = new StringBuilder();
                for (int c; (c = reader.read()) >= 0;)
                    builder.append((char)c);

                return new JSONObject(builder.toString());
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            if (json != null) {
                convertJSONToDogs(json);
                adapter.notifyDataSetChanged();
                dismissDialog();
            }
            else {
                dismissDialog();
                showErrorDialog();
            }
        }

        private void dismissDialog() { if (dialog != null) dialog.dismiss(); }

        private void convertJSONToDogs(JSONObject results) {

            dogs.clear();
            try {
                JSONArray animals = results.getJSONArray("animals");

                for (int i = 0; i < animals.length(); i++) {
                    JSONObject dog = animals.getJSONObject(i);

                    JSONObject breeds = dog.getJSONObject("breeds");
                    JSONObject attributes = dog.getJSONObject("attributes");
                    JSONObject address = dog.getJSONObject("contact").getJSONObject("address");

                    String photo = "";
                    JSONArray photos = dog.getJSONArray("photos");
                    boolean isHiRes = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(MainActivity.HI_RES_IMAGES, false);
                    if (photos.length() > 0) { photo = photos.getJSONObject(0).getString(isHiRes ? "large" : "medium"); }

                    dogs.add(new DogEntity(dog.getInt("id"), getJSONString(dog, "name"), getJSONString(dog, "description"),
                            photo, getJSONString(breeds, "primary"), breeds.getBoolean("mixed"), getJSONString(dog, "age"),
                            dog.getString("gender"), dog.getString("size"), attributes.getBoolean("spayed_neutered"),
                            attributes.getBoolean("house_trained"), attributes.getBoolean("special_needs"),
                            getJSONString(dog, "organization_id"), getJSONString(dog.getJSONObject("contact"), "phone"),
                            getJSONString(address, "address1"), getJSONString(address, "city"), getJSONString(address, "state"),
                            getJSONString(address, "postcode"), getJSONString(dog, "url")));
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String getJSONString(JSONObject json, String field) {
            try {
                if (json.getString(field) == null ||
                        json.getString(field).trim().equalsIgnoreCase("null")) { return "Not Provided"; }
                else { return json.getString(field); }
            }
            catch (JSONException e) {
                e.printStackTrace();
                return "Not Provided";
            }
        }
    }

    public void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_fetch_error_message);
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public URL createSearchURL() {
        String search = "https://api.petfinder.com/v2/animals?type=dog&limit=100&distance=5";

        int sizePos = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(MainActivity.SEARCH_SIZE, 0);
        int genderPos = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(MainActivity.SEARCH_GENDER, 0);
        int agePos = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(MainActivity.SEARCH_AGE, 0);
        String size = sizes.get(sizePos);
        String gender = genders.get(genderPos);
        String age = ages.get(agePos);

        if (!size.equals("No Filter")) { search += ("&size=" + size); }
        if (!gender.equals("No Filter")) { search += ("&gender=" + gender); }
        if (!age.equals("No Filter")) { search += ("&age=" + age); }

        int zip = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(MainActivity.SEARCH_ZIP, -1);

        if (zip == -1) { zip = getZip(getActivity()); }
        if (zip == -1) { zip = 60637; } // default to Hyde Park
        search += ("&location=" + zip);

        try { return new URL(search); }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getZip(Activity activity) {
        // https://stackoverflow.com/questions/40142331/how-to-request-location-permission-at-runtime
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        } else {
            try {
                // https://stackoverflow.com/questions/2227292/how-to-get-latitude-and-longitude-of-the-mobile-device-in-android
                LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                int zip = Integer.parseInt(addresses.get(0).getPostalCode());

                PreferenceManager.getDefaultSharedPreferences(activity)
                        .edit().putInt(MainActivity.SEARCH_ZIP, zip).apply();
                return zip;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 60637;   // default to Hyde Park
    }
}

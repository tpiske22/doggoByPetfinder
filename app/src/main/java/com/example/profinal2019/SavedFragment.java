package com.example.profinal2019;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.profinal2019.Model.DogEntity;
import com.example.profinal2019.UI.DogAdapter;
import com.example.profinal2019.ViewModel.SavedViewModel;

import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment implements DogAdapter.DogWalkable {

    RecyclerView recyclerView;
    FloatingActionButton deleteAllButton;
    TextView noDogsText;

    SavedViewModel viewModel;
    List<DogEntity> dogs = new ArrayList<>();
    DogAdapter adapter;

    public SavedFragment() { }

    public static SavedFragment newInstance() {
        SavedFragment fragment = new SavedFragment();
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
        return inflater.inflate(R.layout.fragment_saved, container, false);
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

        initializeViewModel();

        deleteAllButton = view.findViewById(R.id.deleteAllButton);
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.dialog_delete_all_message);
                builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.deleteAll();
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
        });

        noDogsText = view.findViewById(R.id.noDogsText);
    }

    @Override
    public void walkDetailDogToMain(DogEntity dog) {
        MainActivity main = (MainActivity)getActivity();
        main.showDogDetails(dog);
    }

    private void initializeViewModel() {

        final Observer<List<DogEntity>> notesObserver =
                new Observer<List<DogEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<DogEntity> dogEntities) {
                        dogs.clear();
                        dogs.addAll(dogEntities);

                        if (dogs.size() > 0) { noDogsText.setVisibility(View.GONE); }
                        else { noDogsText.setVisibility(View.VISIBLE); }

                        if (adapter == null) {
                            adapter = new DogAdapter(SavedFragment.this, dogs);
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                };

        viewModel = ViewModelProviders.of(this).get(SavedViewModel.class);
        viewModel.dogs.observe(this, notesObserver);
    }

    public void insertDog(DogEntity dog) {
        viewModel.insertDog(dog);
    }

    public void deleteDog(DogEntity dog) {
        viewModel.deleteDog(dog);
    }
}

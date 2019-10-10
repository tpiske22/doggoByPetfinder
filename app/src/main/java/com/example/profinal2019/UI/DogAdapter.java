package com.example.profinal2019.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.profinal2019.MainActivity;
import com.example.profinal2019.Model.DogEntity;
import com.example.profinal2019.R;
import com.example.profinal2019.SavedFragment;
import com.example.profinal2019.SearchFragment;

import java.util.List;

public class DogAdapter extends RecyclerView.Adapter<DogAdapter.ViewHolder> {

    private Fragment fragment;
    private List<DogEntity> dogs;

    public DogAdapter(Fragment fragment, List<DogEntity> dogs) {
        this.fragment = fragment;
        this.dogs = dogs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View dogView = inflater.inflate(R.layout.doggo_list_item, parent, false);

        return new ViewHolder(dogView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final DogEntity dog = dogs.get(position);

        viewHolder.nameText.setText(dog.getName());
        viewHolder.breedText.setText(dog.getPrimaryBreed() + (dog.isMixed() ? " Mix" : ""));
        viewHolder.cityText.setText(dog.getCity() + ", " + dog.getState());

        // https://stackoverflow.com/questions/44198856/glide-does-not-resolve-its-method
        Glide.with(fragment)
                .load(dog.getPictureUrl())
                .apply(new RequestOptions()
                .override(viewHolder.dogImage.getWidth(), viewHolder.dogImage.getHeight())
                .fitCenter()
                .circleCrop()
                .error(R.drawable.doggo_placeholder))
                .into(viewHolder.dogImage);

        viewHolder.viewDogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DogWalkable dogWalker = (DogWalkable)fragment;
                dogWalker.walkDetailDogToMain(dog);
            }
        });

        viewHolder.viewDogButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());

                if (fragment instanceof SavedFragment) {
                    final SavedFragment savedFragment = (SavedFragment) fragment;

                    builder.setMessage(R.string.dialog_delete_dog_message);
                    builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            savedFragment.deleteDog(dog);
                            dialog.dismiss();
                        }
                    });
                }
                else if (fragment instanceof SearchFragment) {
                    final MainActivity mainActivity = (MainActivity) fragment.getActivity();

                    builder.setMessage(R.string.dialog_save_dog_message);
                    builder.setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mainActivity.insertDog(dog);
                            dialog.dismiss();
                        }
                    });
                }
                builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() { return dogs.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {

        FloatingActionButton viewDogButton;
        ImageView dogImage;
        TextView nameText;
        TextView breedText;
        TextView cityText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewDogButton = itemView.findViewById(R.id.viewDogButton);
            dogImage = itemView.findViewById(R.id.detailDogImage);
            nameText = itemView.findViewById(R.id.nameText);
            breedText = itemView.findViewById(R.id.breedLabel);
            cityText = itemView.findViewById(R.id.cityZipText);
        }
    }

    public interface DogWalkable {
        void walkDetailDogToMain(DogEntity dog);
    }
}

package bzu.proj.BZUCHAT.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import bzu.proj.BZUCHAT.databinding.ActivitySetProfileBinding;
import bzu.proj.BZUCHAT.model.User;

public class setProfile extends AppCompatActivity {

    ActivitySetProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImage;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySetProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Objects.requireNonNull(getSupportActionBar()).hide();

        // get instance of firebase auth and database and storage
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // set onclick listener on image view
        binding.imageView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 222);
        });


        //dialog box to show progress
        dialog = new ProgressDialog(this);
        dialog.setMessage("Setting up profile...");
        dialog.setCancelable(false);

        // set onclick listener on continue button
        binding.continueBtn.setOnClickListener(v -> {
            //get name from edit text
            String name = binding.nameBox.getText().toString().trim();
            if(name.isEmpty()) {
                binding.nameBox.setError("Name Required");
                return;
            }

            // show dialog box
            dialog.show();

            // upload image to firebase storage
            if(selectedImage != null) {

                StorageReference reference = storage.getReference().child("Profiles").child(Objects.requireNonNull(auth.getUid()));
                reference.putFile(selectedImage).addOnCompleteListener(task -> {

                    if(task.isSuccessful()) {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();

                            String uid = auth.getUid();
                            String phone = auth.getCurrentUser().getPhoneNumber();
                            String name1 = binding.nameBox.getText().toString();

                            User user = new User(uid, name1, phone, imageUrl);

                            database.getReference()
                                    .child("users")
                                    .child(uid)
                                    .setValue(user)
                                    .addOnSuccessListener(aVoid -> {
                                        dialog.dismiss();
                                        Intent intent = new Intent(setProfile.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    });
                        });
                    }


                });


            } else {
                // upload data without image

                String uid = auth.getUid();
                String phone = Objects.requireNonNull(auth.getCurrentUser()).getPhoneNumber();

                User user = new User(uid, name, phone, "No Image");

                assert uid != null;
                database.getReference()
                        .child("users")
                        .child(uid)
                        .setValue(user)
                        .addOnSuccessListener(aVoid -> {
                            dialog.dismiss();
                            Intent intent = new Intent(setProfile.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        });
            }


        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 222 && resultCode == RESULT_OK && data != null) {
            if(data.getData() != null) {

                binding.imageView.setImageURI(data.getData());
                selectedImage = data.getData();

//                Uri uri = data.getData(); // image uri
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//
//                StorageReference reference = storage.getReference().child("Profiles").child(Objects.requireNonNull(auth.getUid()));
//
//                reference.putFile(uri).addOnCompleteListener(task -> {
//                    if(task.isSuccessful()) {
//                        reference.getDownloadUrl().addOnSuccessListener(uri1 -> {
//                            String filePath = uri1.toString();
//                            HashMap<String, Object> obj = new HashMap<>();
//                            obj.put("image", filePath);
//                            database.getReference().child("users")
//                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
//                                    .updateChildren(obj).addOnSuccessListener(aVoid -> {
//
//                                    });
//                        });
//                    }
//                });


//                binding.imageView.setImageURI(data.getData());
//                selectedImage = data.getData();
            }

        }

    }


}
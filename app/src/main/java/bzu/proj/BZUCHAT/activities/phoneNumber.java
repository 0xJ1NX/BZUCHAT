package bzu.proj.BZUCHAT.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import bzu.proj.BZUCHAT.databinding.ActivityPhoneNumberBinding;

public class phoneNumber extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null) {
            Intent intent = new Intent(phoneNumber.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        getSupportActionBar().hide();

        binding.phoneBox.requestFocus();

        binding.continueBtn.setOnClickListener(v -> {

            // Check if phone number is empty
            if (binding.phoneBox.getText().toString().trim().isEmpty()) {
                binding.phoneBox.setError("Enter a valid phone number");
                return;
            }


            Intent intent = new Intent(phoneNumber.this, otp.class);
            String phoneNumber = binding.phoneBox.getText().toString().trim();
            intent.putExtra("phoneNumber", phoneNumber);
            startActivity(intent);
        });


    }
}
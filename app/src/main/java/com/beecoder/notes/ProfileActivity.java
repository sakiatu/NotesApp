package com.beecoder.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileActivity extends AppCompatActivity {
    private Button update;
    private ImageView profileImage;
    private EditText name_edt;
    private FirebaseUser user;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();

        update = findViewById(R.id.button_update);
        profileImage = findViewById(R.id.profile_image);
        name_edt = findViewById(R.id.name);
        progressBar = findViewById(R.id.progressBar);
        update.setOnClickListener(v -> updateProfileInfo());
        progressBar.setVisibility(View.GONE);
        name_edt.setText(user.getDisplayName());
        name_edt.setSelection(user.getDisplayName().length());
        Glide.with(this).load(user.getPhotoUrl()).circleCrop().into(profileImage);
    }

    private void updateProfileInfo() {
        update.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(name_edt.getText().toString())
                .build();

        user.updateProfile(request)
                .addOnSuccessListener(a -> {
                    update.setEnabled(true);
                    Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    update.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}

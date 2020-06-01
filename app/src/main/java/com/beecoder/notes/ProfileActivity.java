package com.beecoder.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ProfileActivity extends AppCompatActivity {
    private Button update;
    private ImageView profileImage;
    private EditText name_edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        update = findViewById(R.id.button_update);
        profileImage = findViewById(R.id.profile_image);
        name_edt = findViewById(R.id.name);

        update.setOnClickListener(v-> updateProfileInfo());

    }

    private void updateProfileInfo() {

    }
}

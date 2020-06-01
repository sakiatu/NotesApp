package com.beecoder.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class LoginRegisterActivity extends AppCompatActivity {
    private Button loginOrRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        loginOrRegister = findViewById(R.id.login_reg);
        loginOrRegister.setOnClickListener(v->login());
    }

    private void login() {

    }
}

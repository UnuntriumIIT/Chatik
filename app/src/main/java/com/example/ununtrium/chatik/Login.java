package com.example.ununtrium.chatik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        Button login = findViewById(R.id.login_BUTTON);
        final EditText nickname = findViewById(R.id.login_ET);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Chat.class);
                if (nickname.getText().toString().isEmpty()) Toast.makeText(getApplicationContext(), "Please, insert your nickname!", Toast.LENGTH_SHORT).show();
                else {
                    i.putExtra("LOGIN", nickname.getText().toString());
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}

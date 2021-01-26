package com.example.florim.thirremjeshtrin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class RegisterAs extends AppCompatActivity {
    Button registerAsUser;
    Button registerAsRepairman;
    Button loginLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_as);

        registerAsRepairman = (Button)findViewById(R.id.btnRegisterAsRepairman);
        registerAsUser = (Button)findViewById(R.id.btnRegisterAsUser);
        loginLink = (Button)findViewById(R.id.btnLinkToLoginScreen);

        registerAsUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterAsCustomUser.class);
                startActivity(i);
                finish();
            }
        });
        registerAsRepairman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterAsRepairman.class);
                startActivity(i);
                finish();
            }
        });
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
                finish();
            }
        });
    }
}

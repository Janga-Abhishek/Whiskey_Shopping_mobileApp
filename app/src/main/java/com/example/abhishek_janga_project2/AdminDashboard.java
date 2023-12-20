package com.example.abhishek_janga_project2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        Button showList=findViewById(R.id.showWhiskies);
        Button logoutButton = findViewById(R.id.logout);
        Button addnew = findViewById(R.id.newbtn);
        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(getApplicationContext(), MainActivityAdmin.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        showList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                startActivity(intent);            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

    }

}
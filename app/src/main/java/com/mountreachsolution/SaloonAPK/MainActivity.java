package com.mountreachsolution.SaloonAPK;

import android.content.Intent;
import android.os.Bundle;

import android.widget.RadioButton;


import androidx.appcompat.app.AppCompatActivity;

import com.mountreachsolution.SaloonAPK.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.btnOpenMirror.setOnClickListener(v -> {
            String gender = "male";
            int checked = b.genderGroup.getCheckedRadioButtonId();
            if (checked != -1) {
                RadioButton rb = findViewById(checked);
                gender = rb.getText().toString().toLowerCase();
            }
            String service = b.spService.getSelectedItem().toString().toLowerCase();

            Intent i = new Intent(this, CameraActivity.class);
            i.putExtra("gender", gender);
            i.putExtra("service", service);
            startActivity(i);
        });
    }
}

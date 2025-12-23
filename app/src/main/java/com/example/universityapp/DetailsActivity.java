package com.example.universityapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universityapp.model.Item;

public class DetailsActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone;
    private Button btnWebsite;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tvName = findViewById(R.id.tvDetailsTitle); // Reusing ID
        tvEmail = findViewById(R.id.tvDetailsBody); // Reusing ID as Email
        tvPhone = findViewById(R.id.tvPhone); // New ID
        btnWebsite = findViewById(R.id.btnMoreInfo);

        if (getIntent().hasExtra("item")) {
            item = (Item) getIntent().getSerializableExtra("item");
            if (item != null) {
                tvName.setText(item.getTitle()); // Name
                tvEmail.setText("Email: " + item.getBody());

                if (tvPhone != null) {
                    tvPhone.setText("Phone: " + item.getPhone());
                    tvPhone.setVisibility(View.VISIBLE);
                }

                if (item.getUrl() != null && !item.getUrl().isEmpty()) {
                    btnWebsite.setText("Visit Website: " + item.getUrl());
                    btnWebsite.setOnClickListener(v -> {
                        Intent intent = new Intent(DetailsActivity.this, WebViewActivity.class);
                        intent.putExtra("url", item.getUrl());
                        startActivity(intent);
                    });
                } else {
                    btnWebsite.setVisibility(View.GONE);
                }
            }
        }
    }
}

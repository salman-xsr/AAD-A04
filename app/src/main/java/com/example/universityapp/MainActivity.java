package com.example.universityapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universityapp.adapters.ItemAdapter;
import com.example.universityapp.db.DatabaseHelper;
import com.example.universityapp.model.Item;
import com.example.universityapp.network.ApiClient;
import com.example.universityapp.network.ApiService;
import com.example.universityapp.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private ProgressBar progressBar;
    private SharedPrefManager sharedPrefManager;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPrefManager = new SharedPrefManager(this);
        applyTheme(); // Apply saved theme

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressBar);

        // Initialize empty adapter
        adapter = new ItemAdapter(this, new ArrayList<>(), item -> {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra("item", item);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        if (isNetworkAvailable()) {
            fetchFromApi();
        } else {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
            fetchFromDb();
        }
    }

    private void fetchFromApi() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Item>> call = apiService.getItems();

        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Item> items = response.body();
                    adapter.updateData(items);

                    // Save to DB
                    new Thread(() -> {
                        for (Item item : items) {
                            databaseHelper.insertItem(item);
                        }
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                fetchFromDb();
            }
        });
    }

    private void fetchFromDb() {
        List<Item> items = databaseHelper.getAllItems();
        if (!items.isEmpty()) {
            adapter.updateData(items);
            Toast.makeText(this, R.string.offline_data_loaded, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    // Menu Handling
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            sharedPrefManager.setLoggedIn(false);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_refresh) {
            loadData();
            return true;
        } else if (id == R.id.theme_light) {
            updateTheme(SharedPrefManager.THEME_LIGHT);
            return true;
        } else if (id == R.id.theme_dark) {
            updateTheme(SharedPrefManager.THEME_DARK);
            return true;
        } else if (id == R.id.theme_custom) {
            updateTheme(SharedPrefManager.THEME_CUSTOM);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateTheme(int theme) {
        sharedPrefManager.saveTheme(theme);
        recreate(); // Restart activity to apply theme
    }

    private void applyTheme() {
        int theme = sharedPrefManager.getFormattedTheme();
        switch (theme) {
            case SharedPrefManager.THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                setTheme(R.style.Theme_UniversityApp);
                break;
            case SharedPrefManager.THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                setTheme(R.style.Theme_UniversityApp_Dark);
                break;
            case SharedPrefManager.THEME_CUSTOM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                setTheme(R.style.Theme_UniversityApp_Custom);
                break;
        }
    }
}


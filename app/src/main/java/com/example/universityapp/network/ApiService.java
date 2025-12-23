package com.example.universityapp.network;

import com.example.universityapp.model.Item;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("users") // Changed from 'posts' to 'users' for real data
    Call<List<Item>> getItems();
}

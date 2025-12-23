package com.example.universityapp.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Item implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String title; // Mapping 'name' to our existing 'title' field for minimal refactor

    @SerializedName("email")
    private String body; // Mapping 'email' to our existing 'body' field

    @SerializedName("website")
    private String url;

    @SerializedName("phone")
    private String phone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        if (url != null && !url.startsWith("http")) {
            return "http://" + url;
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

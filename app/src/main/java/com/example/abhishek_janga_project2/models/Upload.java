package com.example.abhishek_janga_project2.models;
import com.google.firebase.firestore.Exclude;

public class Upload {
    private String name;
    private String imageUrl;
    private String description;
    private String cost;
    private String type;
    private String key;
    public Upload() {
        // Default constructor required for calls to DataSnapshot.getValue(Upload.class)
    }

    public Upload(String name, String imageUrl, String description,String cost, String type) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.description=description;
        this.cost=cost;
        this.type=type;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }


    public String getCost() {
        return cost;
    }
    public void setCost(String cost) {
        this.cost = cost;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }




    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}

package com.nft.cloud.Models;

public class AllNftModel {
    String image , name , key , skills ,likes_users_ids,user_id;
    int likes_counter;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public int getLikes_counter() {
        return likes_counter;
    }

    public void setLikes_counter(int likes_counter) {
        this.likes_counter = likes_counter;
    }

    public String getLikes_users_ids() {
        return likes_users_ids;
    }

    public void setLikes_users_ids(String likes_users_ids) {
        this.likes_users_ids = likes_users_ids;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}

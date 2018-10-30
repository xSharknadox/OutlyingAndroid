package com.example.ihor.outlying1.Classes;

/**
 * Created by Ihor on 17.08.2018.
 */

public class OrderRestaurantInfo {
    private long restaurantDepartmnetId;
    private String name;
    private byte[] logo;

    public OrderRestaurantInfo() {
    }

    public long getRestaurantDepartmnetId() {
        return restaurantDepartmnetId;
    }

    public void setRestaurantDepartmnetId(long restaurantDepartmnetId) {
        this.restaurantDepartmnetId = restaurantDepartmnetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }
}

package com.example.ihor.outlying1.Classes;

/**
 * Created by Ihor on 25.06.2018.
 */

public class Restaurant {
    private long id;
    private String name;

    public Restaurant(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

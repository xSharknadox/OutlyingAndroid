package com.example.ihor.outlying1.Classes;

/**
 * Created by Ihor on 09.08.2018.
 */

public class OrderDishObject {
    private long dishId;
    private String dishName;
    private int numberOfDish;
    private double price;

    public OrderDishObject() {
    }

    public long getDishId() {
        return dishId;
    }

    public void setDishId(long dishId) {
        this.dishId = dishId;
    }

    public int getNumberOfDish() {
        return numberOfDish;
    }

    public void setNumberOfDish(int numberOfDish) {
        this.numberOfDish = numberOfDish;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }
}

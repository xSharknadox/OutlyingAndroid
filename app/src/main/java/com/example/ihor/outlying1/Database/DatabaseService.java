package com.example.ihor.outlying1.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.Time;

import com.example.ihor.outlying1.Classes.DishObject;
import com.example.ihor.outlying1.Classes.OrderDishObject;
import com.example.ihor.outlying1.Classes.OrderObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ihor on 13.08.2018.
 */

public class DatabaseService {
    private SQLiteDatabase database;
    private DatabaseHandler databaseHandler;

    private Context context;

    private static String TABLE_ORDERS= "orders";
    private static String COLUMN_ORDER_ID_ORDER = "id_order";
    private static String COLUMN_ORDER_ID_RESTAURANT_DEPARTMENT = "id_restaurant_department";
    private static String COLUMN_ORDER_DATE = "date";
    private static String COLUMN_ORDER_NUMBER_OF_PEOPLE = "number_of_people";
    private static String COLUMN_ORDER_ALL_SUM = "all_sum";
    private static String COLUMN_ORDER_LOGO = "logo";
    private static String COLUMN_ORDER_RESTAURANT_NAME = "restaurant_name";

    private static String TABLE_ORDER_DISHES = "order_dishes";
    private static String COLUMN_ORDER_DISHES_ID_ORDER_DISHES = "id_order_dishes";
    private static String COLUMN_ORDER_DISHES_ID_DISH = "id_dish";
    private static String COLUMN_ORDER_DISHES_ID_ORDER = "id_order";
    private static String COLUMN_ORDER_DISHES_NAME = "name";
    private static String COLUMN_ORDER_DISHES_PRICE = "price";
    private static String COLUMN_ORDER_DISHES_INGREDIENTS = "ingredients";
    private static String COLUMN_ORDER_DISHES_UNITS = "units";
    private static String COLUMN_ORDER_DISHES_NUMBER_OF_DISH = "number_of_dish";
    private static String COLUMN_ORDER_DISHES_HOW_MUCH = "how_much";


    public DatabaseService(Context context) {
        this.context = context;
        databaseHandler = new DatabaseHandler(context);
    }

    public DatabaseService open(){
        database = databaseHandler.getWritableDatabase();
        return this;
    }

    public void close(){
        databaseHandler.close();
    }

    public boolean checkOrderExist(int restaurantDepartmentId){
        Cursor cursor = database.query(TABLE_ORDERS, new String[]{COLUMN_ORDER_ID_ORDER}, (COLUMN_ORDER_ID_RESTAURANT_DEPARTMENT + "=?"), new String[]{restaurantDepartmentId+""}, null, null, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public int getOrderId(int restaurantDepartmentId){
        Cursor cursor = database.query(TABLE_ORDERS, new String[]{COLUMN_ORDER_ID_ORDER}, (COLUMN_ORDER_ID_RESTAURANT_DEPARTMENT + "=?"), new String[]{restaurantDepartmentId+""}, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_ID_ORDER));
        }
        return -1;
    }

    public boolean checkDishOfOrder(int restaurantDepartmentId, int dishId){
        Cursor cursor = database.query(TABLE_ORDERS, new String[]{COLUMN_ORDER_ID_ORDER}, (COLUMN_ORDER_ID_RESTAURANT_DEPARTMENT + "=?"), new String[]{restaurantDepartmentId+""}, null, null, null);
        if (cursor.moveToFirst()) {
            int orderId = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_ID_ORDER));
            cursor = database.query(TABLE_ORDER_DISHES, new String[]{COLUMN_ORDER_DISHES_ID_DISH}, (COLUMN_ORDER_DISHES_ID_DISH + "=? AND "+COLUMN_ORDER_DISHES_ID_ORDER+"=?"), new String[]{dishId+"", orderId+""}, null, null, null);
            if (cursor.moveToFirst()) {
                return true;
            }
        }
        return false;
    }

    public void addNewOrder(int restaurantDepartmentId, String date, byte[] logo, String restaurantName, String logoFolderPath){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ORDER_ID_RESTAURANT_DEPARTMENT, restaurantDepartmentId);
        contentValues.put(COLUMN_ORDER_DATE, date);
        contentValues.put(COLUMN_ORDER_NUMBER_OF_PEOPLE, 1);
        contentValues.put(COLUMN_ORDER_ALL_SUM, 0);
        contentValues.put(COLUMN_ORDER_LOGO, SavePicture(logoFolderPath, logo));
        contentValues.put(COLUMN_ORDER_RESTAURANT_NAME, restaurantName);
        database.insert(TABLE_ORDERS,null, contentValues);
    }

    public void updateOrderDepartment(int orderId, int restaurantDepartmentId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ORDER_ID_RESTAURANT_DEPARTMENT, restaurantDepartmentId);
        database.update(TABLE_ORDERS, contentValues, COLUMN_ORDER_ID_ORDER + "=" + orderId, null);
    }

    public void updateOrderDate(int orderId, String date){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ORDER_DATE, date);
        database.update(TABLE_ORDERS, contentValues, COLUMN_ORDER_ID_ORDER + "=" + orderId, null);
    }

    public void updateOrderNumberOfPeople(int orderId, int numberOfPeople){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ORDER_NUMBER_OF_PEOPLE, numberOfPeople);
        database.update(TABLE_ORDERS, contentValues, COLUMN_ORDER_ID_ORDER + "=" + orderId, null);
    }

    public void updateOrderAllSum(int orderId, double allSum){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ORDER_ALL_SUM, allSum);
        database.update(TABLE_ORDERS, contentValues, COLUMN_ORDER_ID_ORDER + "=" + orderId, null);
    }

    public void deleteOrder(int orderId){
        database.delete(TABLE_ORDER_DISHES, COLUMN_ORDER_DISHES_ID_ORDER+"="+orderId, null);
        database.delete(TABLE_ORDERS, COLUMN_ORDER_ID_ORDER+"="+orderId,null);
    }

    public void addNewOrderDish(int dishId, int orderId, String name, double price, String ingredients, String units, double howMuch){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ORDER_DISHES_ID_DISH, dishId);
        contentValues.put(COLUMN_ORDER_DISHES_ID_ORDER, orderId);
        contentValues.put(COLUMN_ORDER_DISHES_NAME, name);
        contentValues.put(COLUMN_ORDER_DISHES_PRICE, price);
        contentValues.put(COLUMN_ORDER_DISHES_INGREDIENTS, ingredients);
        contentValues.put(COLUMN_ORDER_DISHES_UNITS, units);
        contentValues.put(COLUMN_ORDER_DISHES_NUMBER_OF_DISH, 1);
        contentValues.put(COLUMN_ORDER_DISHES_HOW_MUCH, howMuch);
        database.insert(TABLE_ORDER_DISHES,null, contentValues);
    }

    public void plusNumberOfOrderDish(int restaurantDeparmentId, int dishId){
        Cursor cursor = database.query(TABLE_ORDERS, new String[]{COLUMN_ORDER_ID_ORDER}, (COLUMN_ORDER_ID_RESTAURANT_DEPARTMENT + "=?"), new String[]{restaurantDeparmentId+""}, null, null, null);
        int orderId;
        int ordersDishId;
        int numberOfDishes;
        if (cursor.moveToFirst()){
            orderId = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_ID_ORDER));
            cursor = database.query(TABLE_ORDER_DISHES, new String[]{COLUMN_ORDER_DISHES_ID_ORDER_DISHES, COLUMN_ORDER_DISHES_NUMBER_OF_DISH}, (COLUMN_ORDER_DISHES_ID_DISH + "=? AND "+COLUMN_ORDER_DISHES_ID_ORDER+"=?"), new String[]{dishId+"", orderId+""}, null, null, null);
            if (cursor.moveToFirst()){
                ordersDishId = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_DISHES_ID_ORDER_DISHES));
                numberOfDishes = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_DISHES_NUMBER_OF_DISH));
                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_ORDER_DISHES_NUMBER_OF_DISH, numberOfDishes+1);
                database.update(TABLE_ORDER_DISHES, contentValues, COLUMN_ORDER_DISHES_ID_ORDER_DISHES + "=" + ordersDishId, null);
            }
        }
    }

    public void minusNumberOfOrderDish(int restaurantDeparmentId, int dishId){
        Cursor cursor = database.query(TABLE_ORDERS, new String[]{COLUMN_ORDER_ID_ORDER}, (COLUMN_ORDER_ID_RESTAURANT_DEPARTMENT + "=?"), new String[]{restaurantDeparmentId+""}, null, null, null);
        int orderId;
        int ordersDishId;
        int numberOfDishes;
        if (cursor.moveToFirst()){
            orderId = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_ID_ORDER));
            cursor = database.query(TABLE_ORDER_DISHES, new String[]{COLUMN_ORDER_DISHES_ID_ORDER_DISHES, COLUMN_ORDER_DISHES_NUMBER_OF_DISH}, (COLUMN_ORDER_DISHES_ID_DISH + "=? AND "+COLUMN_ORDER_DISHES_ID_ORDER+"=?"), new String[]{dishId+"", orderId+""}, null, null, null);
            if (cursor.moveToFirst()){
                ordersDishId = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_DISHES_ID_ORDER_DISHES));
                numberOfDishes = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_DISHES_NUMBER_OF_DISH));
                if (numberOfDishes > 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(COLUMN_ORDER_DISHES_NUMBER_OF_DISH, numberOfDishes-1);
                    database.update(TABLE_ORDER_DISHES, contentValues, COLUMN_ORDER_DISHES_ID_ORDER_DISHES + "=" + ordersDishId, null);
                }
            }
        }
    }

    public void plusNumberOfOrderDishAtConfirm(int orderId, int dishId){
        Cursor cursor = database.query(TABLE_ORDER_DISHES, new String[]{COLUMN_ORDER_DISHES_ID_ORDER_DISHES, COLUMN_ORDER_DISHES_NUMBER_OF_DISH}, (COLUMN_ORDER_DISHES_ID_DISH + "=? AND "+COLUMN_ORDER_DISHES_ID_ORDER+"=?"), new String[]{dishId+"", orderId+""}, null, null, null);
        if (cursor.moveToFirst()){
            int ordersDishId = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_DISHES_ID_ORDER_DISHES));
            int numberOfDishes = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_DISHES_NUMBER_OF_DISH));
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ORDER_DISHES_NUMBER_OF_DISH, numberOfDishes+1);
            database.update(TABLE_ORDER_DISHES, contentValues, COLUMN_ORDER_DISHES_ID_ORDER_DISHES + "=" + ordersDishId, null);
        }
    }

    public void minusNumberOfOrderDishAtConfirm(int orderId, int dishId){
        int ordersDishId;
        int numberOfDishes;
        Cursor cursor = database.query(TABLE_ORDER_DISHES, new String[]{COLUMN_ORDER_DISHES_ID_ORDER_DISHES, COLUMN_ORDER_DISHES_NUMBER_OF_DISH}, (COLUMN_ORDER_DISHES_ID_DISH + "= ? AND "+COLUMN_ORDER_DISHES_ID_ORDER+"= ?"), new String[]{dishId+"", orderId+""}, null, null, null);
        if (cursor.moveToFirst()){
            ordersDishId = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_DISHES_ID_ORDER_DISHES));
            numberOfDishes = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_DISHES_NUMBER_OF_DISH));
            if (numberOfDishes > 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_ORDER_DISHES_NUMBER_OF_DISH, numberOfDishes-1);
                database.update(TABLE_ORDER_DISHES, contentValues, COLUMN_ORDER_DISHES_ID_ORDER_DISHES + "=" + ordersDishId, null);
            }
            else {
                deleteDishOfOrder(orderId, dishId);
            }
        }
    }


    public void deleteDishOfOrder(int orderId, int dishId){
        Cursor cursor = database.query(TABLE_ORDER_DISHES, new String[]{COLUMN_ORDER_DISHES_ID_ORDER_DISHES}, (COLUMN_ORDER_DISHES_ID_DISH + "=? AND "+COLUMN_ORDER_DISHES_ID_ORDER+"=?"), new String[]{dishId+"", orderId+""}, null, null, null);
        int orderDishesId;
        if (cursor.moveToFirst()) {
            orderDishesId = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_DISHES_ID_ORDER_DISHES));
            database.delete(TABLE_ORDER_DISHES, COLUMN_ORDER_DISHES_ID_ORDER_DISHES+"="+orderDishesId,null);
        }
    }

    public int getUserDishAmount(int restaurantDeparmentId, int dishId){
        Cursor cursor = database.query(TABLE_ORDERS, new String[]{COLUMN_ORDER_ID_ORDER}, (COLUMN_ORDER_ID_RESTAURANT_DEPARTMENT + "=?"), new String[]{restaurantDeparmentId+""}, null, null, null);
        int orderId;
        int ordersDishId;
        int numberOfDishes=-1;
        if (cursor.moveToFirst()) {
            orderId = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_ID_ORDER));
            cursor = database.query(TABLE_ORDER_DISHES, new String[]{COLUMN_ORDER_DISHES_ID_ORDER_DISHES, COLUMN_ORDER_DISHES_NUMBER_OF_DISH}, (COLUMN_ORDER_DISHES_ID_DISH + "=? AND " + COLUMN_ORDER_DISHES_ID_ORDER + "=?"), new String[]{dishId + "", orderId + ""}, null, null, null);
            if (cursor.moveToFirst()) {
                ordersDishId = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_DISHES_ID_ORDER_DISHES));
                numberOfDishes = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_DISHES_NUMBER_OF_DISH));
            }
        }
        return numberOfDishes;
    }

    public List<OrderObject> getAllOrders(String folderToOpen){
        List<OrderObject> orders = new ArrayList<>();
        Cursor cursor = database.query(TABLE_ORDERS, new String[]{COLUMN_ORDER_ID_ORDER, COLUMN_ORDER_ID_RESTAURANT_DEPARTMENT, COLUMN_ORDER_DATE, COLUMN_ORDER_LOGO, COLUMN_ORDER_RESTAURANT_NAME, COLUMN_ORDER_NUMBER_OF_PEOPLE}, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                OrderObject orderObject = new OrderObject();
                orderObject.setOrderId(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_ID_ORDER)));
                orderObject.setDepartmentId(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_ID_RESTAURANT_DEPARTMENT)));
                orderObject.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_DATE)));
                orderObject.setLogo(OpenPicture(folderToOpen, cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_LOGO))));
                orderObject.setRestaurantName(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_RESTAURANT_NAME)));
                orderObject.setNumberOfPeople(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_NUMBER_OF_PEOPLE)));
                orders.add(orderObject);
            }while(cursor.moveToNext());
        }
        return orders;
    }

    public OrderDishObject[] getAllOrderDishes(int orderId){
        OrderDishObject[] dishes;
        Cursor cursor = database.query(TABLE_ORDER_DISHES, new String[]{COLUMN_ORDER_DISHES_ID_ORDER_DISHES, COLUMN_ORDER_DISHES_ID_DISH, COLUMN_ORDER_DISHES_NAME, COLUMN_ORDER_DISHES_PRICE, COLUMN_ORDER_DISHES_INGREDIENTS, COLUMN_ORDER_DISHES_UNITS, COLUMN_ORDER_DISHES_NUMBER_OF_DISH, COLUMN_ORDER_DISHES_HOW_MUCH}, (COLUMN_ORDER_DISHES_ID_ORDER+" = ?"), new String[]{orderId+""}, null, null, null);
        dishes = new OrderDishObject[cursor.getCount()];
        int x=0;
        if(cursor.moveToFirst()){
            do{
                OrderDishObject dishObject = new OrderDishObject();
                dishObject.setDishId(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_DISHES_ID_DISH)));
                dishObject.setDishName(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_DISHES_NAME)));
                dishObject.setPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_ORDER_DISHES_PRICE)));
                dishObject.setNumberOfDish(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_DISHES_NUMBER_OF_DISH)));
                dishes[x]=dishObject;
                x++;
            }while(cursor.moveToNext());
        }
        return dishes;
    }

    private String SavePicture(String folderToSave, byte[]logo)
    {
        OutputStream fOut = null;
        Time time = new Time();
        time.setToNow();
        String logosName = Integer.toString(time.year) + Integer.toString(time.month) + Integer.toString(time.monthDay) + Integer.toString(time.hour) + Integer.toString(time.minute) + Integer.toString(time.second) +".jpg";
        try {
            File file = new File(folderToSave, logosName); // создать уникальное имя для файла основываясь на дате сохранения
            fOut = new FileOutputStream(file);
            Bitmap bitmap = BitmapFactory.decodeByteArray(logo, 0, logo.length);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // сохранять картинку в jpeg-формате с 85% сжатия.
            fOut.flush();
            fOut.close();
        }
        catch (Exception e) // здесь необходим блок отслеживания реальных ошибок и исключений, общий Exception приведен в качестве примера
        {
            return e.getMessage();
        }
        return logosName;
    }

    private byte[] OpenPicture(String folderToSave, String logosName)
    {
        InputStream fIn = null;
        int data;
        List<Byte> logoBytesList = new ArrayList<>();
        try {
            File file = new File(folderToSave, logosName); // создать уникальное имя для файла основываясь на дате сохранения
            fIn = new FileInputStream(file);
            data = fIn.read();
            logoBytesList.add((byte)data);
            while(data != -1) {
                logoBytesList.add((byte)data);
                data = fIn.read();
            }
            fIn.close();
        }
        catch (Exception e) // здесь необходим блок отслеживания реальных ошибок и исключений, общий Exception приведен в качестве примера
        {
        }
        byte[] logosBytes = new byte[logoBytesList.size()];
        for (int x=0; x<logoBytesList.size(); x++) {
            logosBytes[x] = logoBytesList.get(x);
        }
        return logosBytes;
    }
}

package com.example.ihor.outlying1.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ihor on 17.08.2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static String DB_NAME = "outlying1.db";

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

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_ORDERS_TABLE = "CREATE TABLE "+TABLE_ORDERS + "("
                + COLUMN_ORDER_ID_ORDER + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
                + COLUMN_ORDER_ID_RESTAURANT_DEPARTMENT +" INTEGER, "
                + COLUMN_ORDER_DATE + " TEXT, "
                + COLUMN_ORDER_NUMBER_OF_PEOPLE + " INTEGER, "
                + COLUMN_ORDER_ALL_SUM + " REAL, "
                + COLUMN_ORDER_LOGO + " TEXT,"
                + COLUMN_ORDER_RESTAURANT_NAME + " TEXT"+ ")";
        String CREATE_ORDER_DISHES_TABLE = "CREATE TABLE "+TABLE_ORDER_DISHES + "("
                + COLUMN_ORDER_DISHES_ID_ORDER_DISHES + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
                + COLUMN_ORDER_DISHES_ID_DISH +" INTEGER,"
                + COLUMN_ORDER_DISHES_ID_ORDER + " INTEGER,"
                + COLUMN_ORDER_DISHES_NAME + " TEXT,"
                + COLUMN_ORDER_DISHES_PRICE + " REAL,"
                + COLUMN_ORDER_DISHES_INGREDIENTS + " TEXT,"
                + COLUMN_ORDER_DISHES_UNITS + " TEXT,"
                + COLUMN_ORDER_DISHES_NUMBER_OF_DISH + " INTEGER,"
                + COLUMN_ORDER_DISHES_HOW_MUCH + " REAL" + ")";
        sqLiteDatabase.execSQL(CREATE_ORDERS_TABLE);
        sqLiteDatabase.execSQL(CREATE_ORDER_DISHES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_DISHES);

        onCreate(sqLiteDatabase);
    }
}

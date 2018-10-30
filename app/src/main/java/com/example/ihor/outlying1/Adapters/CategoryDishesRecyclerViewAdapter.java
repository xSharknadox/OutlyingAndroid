package com.example.ihor.outlying1.Adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ihor.outlying1.Classes.DishObject;
import com.example.ihor.outlying1.Classes.OrderRestaurantInfo;
import com.example.ihor.outlying1.Database.DatabaseService;
import com.example.ihor.outlying1.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * Created by Ihor on 28.06.2018.
 */

public class CategoryDishesRecyclerViewAdapter extends RecyclerView.Adapter<CategoryDishesRecyclerViewAdapter.CategoryDishesView> {

    private DishObject[] dishes;
    private OrderRestaurantInfo orderRestaurantInfo;
    private Context context;

    public CategoryDishesRecyclerViewAdapter(DishObject[] dishes, OrderRestaurantInfo orderRestaurantInfo, Context context) {
        this.dishes = dishes;
        this.orderRestaurantInfo = orderRestaurantInfo;
        this.context = context;
    }

    @Override
    public CategoryDishesView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_dishes_recycler_item, parent, false);
        CategoryDishesView categoryDishesView = new CategoryDishesView(view);
        return categoryDishesView;
    }

    @Override
    public void onBindViewHolder(final CategoryDishesView holder, final int position) {

        holder.dishImage.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(dishes[position].getPhoto(), 0, dishes[position].getPhoto().length)));
        holder.dishName.setText(dishes[position].getName());
        holder.dishIngredients.setText("Інгредієнти: "+ dishes[position].getIngredients());
        if(dishes[position].getHowMuch()%2==0) {
            holder.dishAmount.setText(((int)(dishes[position].getHowMuch())) + " " + dishes[position].getUnits());
        }else {
            holder.dishAmount.setText(dishes[position].getHowMuch() + " " + dishes[position].getUnits());
        }
        if(dishes[position].getHowMuch()%2==0) {
            holder.dishPrice.setText(((int)(dishes[position].getPrice()))+" грн");
        }else {
            holder.dishPrice.setText(dishes[position].getPrice()+" грн");
        }
        holder.userAmount.setText("0");
        final DatabaseService databaseService = new DatabaseService(holder.itemView.getContext());
        holder.plusDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseService.open();
                if(databaseService.checkOrderExist((int)orderRestaurantInfo.getRestaurantDepartmnetId())){
                    if(databaseService.checkDishOfOrder((int)orderRestaurantInfo.getRestaurantDepartmnetId(), (int)dishes[position].getDishId())){
                        databaseService.plusNumberOfOrderDish((int)orderRestaurantInfo.getRestaurantDepartmnetId(), (int)dishes[position].getDishId());
                    }else {
                        databaseService.addNewOrderDish((int)dishes[position].getDishId(), databaseService.getOrderId((int)orderRestaurantInfo.getRestaurantDepartmnetId()),dishes[position].getName(),dishes[position].getPrice(),dishes[position].getIngredients(), dishes[position].getUnits(), dishes[position].getHowMuch());
                    }
                }
                else {
                    Date currentTime = Calendar.getInstance().getTime();
                    String date = currentTime.getDay()+"."+(currentTime.getMonth()+1)+"."+currentTime.getYear()+","+currentTime.getHours()+":"+currentTime.getMinutes();
                    databaseService.addNewOrder((int)orderRestaurantInfo.getRestaurantDepartmnetId(), date, orderRestaurantInfo.getLogo(),orderRestaurantInfo.getName(), context.getExternalCacheDir().toString());
                    databaseService.addNewOrderDish((int)dishes[position].getDishId(), databaseService.getOrderId((int)orderRestaurantInfo.getRestaurantDepartmnetId()),dishes[position].getName(),dishes[position].getPrice(),dishes[position].getIngredients(), dishes[position].getUnits(), dishes[position].getHowMuch());
                }
                holder.userAmount.setText(databaseService.getUserDishAmount((int)orderRestaurantInfo.getRestaurantDepartmnetId(), (int)dishes[position].getDishId())+"");
                databaseService.close();
            }
        });
        holder.minusDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseService.open();
                if(databaseService.checkOrderExist((int)orderRestaurantInfo.getRestaurantDepartmnetId())){
                    if(databaseService.checkDishOfOrder((int)orderRestaurantInfo.getRestaurantDepartmnetId(), (int)dishes[position].getDishId())){
                        databaseService.minusNumberOfOrderDish((int)orderRestaurantInfo.getRestaurantDepartmnetId(), (int)dishes[position].getDishId());
                        holder.userAmount.setText(databaseService.getUserDishAmount((int)orderRestaurantInfo.getRestaurantDepartmnetId(), (int)dishes[position].getDishId())+"");
                    }
                    }
                databaseService.close();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishes.length;
    }

    public class CategoryDishesView extends RecyclerView.ViewHolder {
        TextView dishName, dishIngredients, dishAmount, dishPrice, userAmount;
        ImageView dishImage;
        Button plusDish, minusDish;
        public CategoryDishesView(View itemView) {
            super(itemView);
            dishImage = (ImageView) itemView.findViewById(R.id.category_dishes_item_image);
            dishName = (TextView) itemView.findViewById(R.id.category_dishes_item_name);
            dishIngredients = (TextView) itemView.findViewById(R.id.category_dishes_item_ingredients);
            dishAmount = (TextView) itemView.findViewById(R.id.category_dishes_item_amount);
            dishPrice = (TextView) itemView.findViewById(R.id.category_dishes_item_price);
            userAmount = (TextView) itemView.findViewById(R.id.category_dishes_item_user_amount);
            plusDish = (Button) itemView.findViewById(R.id.category_dishes_item_plus_button);
            minusDish = (Button) itemView.findViewById(R.id.category_dishes_item_minus_button);
        }
    }
}

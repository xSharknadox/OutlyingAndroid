package com.example.ihor.outlying1.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ihor.outlying1.Classes.OrderDishObject;
import com.example.ihor.outlying1.Database.DatabaseService;
import com.example.ihor.outlying1.R;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Ihor on 28.06.2018.
 */

public class OrderConfirmRecyclerViewAdapter extends RecyclerView.Adapter<OrderConfirmRecyclerViewAdapter.OrderConfirmView> {

    private OrderDishObject[] orderDishes;
    private long orderId;
    private String typeOfOrder;
    private TextView allSum;
    private double totalPrice;
    private DatabaseService databaseService;

    public OrderConfirmRecyclerViewAdapter(OrderDishObject[] orderDishes, String typeOfOrder, long orderId, TextView allSum, double totalPrice, DatabaseService databaseService) {
        this.orderDishes = orderDishes;
        this.orderId = orderId;
        this.typeOfOrder = typeOfOrder;
        this.allSum = allSum;
        this.totalPrice = totalPrice;
        this.databaseService = databaseService;
    }

    @Override
    public OrderConfirmView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_confirm_recycler_item, parent, false);
        OrderConfirmView orderConfirmView = new OrderConfirmView(view);
        return orderConfirmView;
    }

    @Override
    public void onBindViewHolder(final OrderConfirmView holder, int position) {
        final long dishId = orderDishes[position].getDishId();
        final int itemPosition = position;
        holder.dishName.setText(orderDishes[position].getDishName());
        holder.dishPortions.setText("Порції: "+orderDishes[position].getNumberOfDish()+" шт");
        holder.dishPrice.setText("Ціна: "+orderDishes[position].getPrice()+" грн");
        holder.priceOfAllDishes.setText((orderDishes[position].getPrice()*orderDishes[position].getNumberOfDish())+" грн");
        holder.plusDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(typeOfOrder.equals("server_order")){
                    new plusDishInOrder(orderId, dishId).execute(holder.context.getResources().getString(R.string.server_address)+"/plus_order_dish");
                }else {
                    databaseService.open();
                    databaseService.plusNumberOfOrderDishAtConfirm((int)orderId, (int)orderDishes[itemPosition].getDishId());
                    databaseService.close();
                }
                holder.dishPortions.setText("Порції: "+(orderDishes[itemPosition].getNumberOfDish()+1)+" шт");
                orderDishes[itemPosition].setNumberOfDish(orderDishes[itemPosition].getNumberOfDish()+1);
                holder.priceOfAllDishes.setText((orderDishes[itemPosition].getPrice()*orderDishes[itemPosition].getNumberOfDish())+" грн");
                totalPrice+=orderDishes[itemPosition].getPrice();
                allSum.setText("Сума заказу: "+totalPrice+" грн");
            }
        });
        holder.minusDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(typeOfOrder.equals("server_order")){
                    new minusDishInOrder(orderId, dishId).execute(holder.context.getResources().getString(R.string.server_address)+"/minus_order_dish");
                }else {
                    databaseService.open();
                    databaseService.minusNumberOfOrderDishAtConfirm((int)orderId, (int)orderDishes[itemPosition].getDishId());
                    databaseService.close();
                }
                if(orderDishes[itemPosition].getNumberOfDish()>0){
                    holder.dishPortions.setText("Порції: "+(orderDishes[itemPosition].getNumberOfDish()-1)+" шт");
                    orderDishes[itemPosition].setNumberOfDish(orderDishes[itemPosition].getNumberOfDish()-1);
                    holder.priceOfAllDishes.setText((orderDishes[itemPosition].getPrice()*orderDishes[itemPosition].getNumberOfDish())+" грн");
                    totalPrice-=orderDishes[itemPosition].getPrice();
                    allSum.setText("Сума заказу: "+totalPrice+" грн");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderDishes.length;
    }

    public class OrderConfirmView extends RecyclerView.ViewHolder {
        Context context;
        TextView dishName, dishPrice, dishPortions, priceOfAllDishes;
        Button plusDish, minusDish;
        public OrderConfirmView(View itemView) {
            super(itemView);
            context = itemView.getContext();
            dishName = itemView.findViewById(R.id.order_confirm_list_item_name);
            dishPrice = itemView.findViewById(R.id.order_confirm_list_item_price_text);
            dishPortions = itemView.findViewById(R.id.order_confirm_list_item_portions);
            priceOfAllDishes = itemView.findViewById(R.id.order_confirm_list_item_sum_price);
            plusDish = itemView.findViewById(R.id.order_confirm_list_item_plus_button);
            minusDish = itemView.findViewById(R.id.order_confirm_list_item_minus_button);
        }
    }

    private class plusDishInOrder extends AsyncTask<String, Void, String>{
        private long orderId, dishId;

        public plusDishInOrder(long orderId, long dishId) {
            this.orderId = orderId;
            this.dishId = dishId;
        }

        @Override
        protected String doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.postForEntity(url[0],new long[]{orderId, dishId, 1}, Void.class);
            return null;
        }
    }

    private class minusDishInOrder extends AsyncTask<String, Void, String>{
        private long orderId, dishId;

        public minusDishInOrder(long orderId, long dishId) {
            this.orderId = orderId;
            this.dishId = dishId;
        }

        @Override
        protected String doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.postForEntity(url[0],new long[]{orderId, dishId, 1}, Void.class);
            return null;
        }
    }
}

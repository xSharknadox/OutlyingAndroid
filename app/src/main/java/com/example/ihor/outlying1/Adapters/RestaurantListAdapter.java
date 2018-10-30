package com.example.ihor.outlying1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ihor.outlying1.Activities.RestaurantMenuActivity;
import com.example.ihor.outlying1.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Ihor on 25.06.2018.
 */

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {

    private ArrayList<Map<String, Object>> restaurants;

    public RestaurantListAdapter( ArrayList<Map<String, Object>> restaurants) {
        this.restaurants = restaurants;
    }

    @Override
    public int getItemViewType(int position){
        if(position==0||(restaurants.get(position).get("list_category").toString()=="title")) {
            return 0;
        }
        if(restaurants.get(position).get("list_category").toString()=="restaurant"){
            return 1;
        }
        return 2;
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType==0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_list_view_category_title, parent, false);
            RestaurantViewHolder restaurantViewHolder = new RestaurantViewHolder(view, viewType);
            return restaurantViewHolder;
        }
        if(viewType==1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restauraurants_list_view_item, parent, false);
            RestaurantViewHolder restaurantViewHolder = new RestaurantViewHolder(view, viewType);
            return restaurantViewHolder;
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restauraurants_list_view_item, parent, false);
            RestaurantViewHolder restaurantViewHolder = new RestaurantViewHolder(view, viewType);
            return restaurantViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(final RestaurantViewHolder holder, int position) {
        Map map = restaurants.get(position);
        String str;
        if((position==0)||(map.get("list_category")=="title")) {
            str = map.get("list_category_title").toString();
            holder.restaurantName.setText(str);
        }
        if(map.get("list_category")=="restaurant"){
            str = map.get("restaurant_name").toString();
            holder.restaurantName.setText(str);
            holder.tagRestaurantId = Long.parseLong(map.get("restaurant_id").toString());
        }
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder{
        TextView restaurantName;
        long tagRestaurantId;
        public RestaurantViewHolder(final View itemView, int viewType) {
            super(itemView);
            if(viewType==0){
                restaurantName = itemView.findViewById(R.id.restaurants_listview_category_title);
            }
            if(viewType==1){
                restaurantName = itemView.findViewById(R.id.restaurants_listview_item_restaurant_name);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(itemView.getContext(), RestaurantMenuActivity.class);
                        intent.putExtra("tagRestaurantId", tagRestaurantId);
                        itemView.getContext().startActivity(intent);
                    }
                });
            }
        }
    }
}

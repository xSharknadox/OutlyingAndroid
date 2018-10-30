package com.example.ihor.outlying1.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ihor.outlying1.Activities.CategoryDishesActivity;
import com.example.ihor.outlying1.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Ihor on 27.06.2018.
 */

public class RestaurantMenuRecyclerViewAdapter extends RecyclerView.Adapter<RestaurantMenuRecyclerViewAdapter.RestaurantMenuViewHolder> {

    private ArrayList<Map<String, Object>> restaurantMenuItems;
    private long tagRestaurantId;

    public RestaurantMenuRecyclerViewAdapter(ArrayList<Map<String, Object>> restaurantMenuItems, long tagRestaurantId) {
        this.restaurantMenuItems = restaurantMenuItems;
        this.tagRestaurantId = tagRestaurantId;
    }

    @Override
    public RestaurantMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_menu_recycler_view_item, parent, false);
        RestaurantMenuViewHolder restaurantMenuViewHolder = new RestaurantMenuViewHolder(view);
        return restaurantMenuViewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantMenuViewHolder holder, int position) {
        Map map = restaurantMenuItems.get(position);
        holder.menuItemImage.setImageResource(R.drawable.ic_test);
        holder.menuItemName.setText(map.get("menu_item_name").toString());
        holder.menuCategoryId = Long.parseLong(map.get("menu_item_id").toString());
    }

    @Override
    public int getItemCount() {
        return restaurantMenuItems.size();
    }

    public class RestaurantMenuViewHolder extends RecyclerView.ViewHolder {
        ImageView menuItemImage;
        TextView menuItemName;
        long menuCategoryId;
        public RestaurantMenuViewHolder(final View itemView) {
            super(itemView);
            menuItemImage = (ImageView) itemView.findViewById(R.id.restaurant_menu_recycler_view_item_image);
            menuItemName = (TextView) itemView.findViewById(R.id.restaurant_menu_recycler_view_item_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), CategoryDishesActivity.class);
                    intent.putExtra("tagRestaurantId", tagRestaurantId);
                    intent.putExtra("menuCategoryId", menuCategoryId);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}

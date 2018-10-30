package com.example.ihor.outlying1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ihor.outlying1.Activities.MainActivity;
import com.example.ihor.outlying1.Activities.OrderConfirmActivity;
import com.example.ihor.outlying1.Classes.OrderObject;
import com.example.ihor.outlying1.Classes.RestaurantAddress;
import com.example.ihor.outlying1.Database.DatabaseService;
import com.example.ihor.outlying1.Fragments.OrdersFragment;
import com.example.ihor.outlying1.R;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Ihor on 28.06.2018.
 */

public class OrdersRecyclerViewAdapter extends RecyclerView.Adapter<OrdersRecyclerViewAdapter.OrderViewHolder> {

    final String ATTRIBUTE_ORDER_ID = "order_id";
    final String ATTRIBUTE_DEPARTMENT_ID = "department_id";
    final String ATTRIBUTE_ORDER_NAME = "order_name";
    final String ATTRIBUTE_DATE = "order_date";
    final String ATTRIBUTE_DEPARTMENT_ADDRESS = "department_address";
    final String ATTRIBUTE_DEPARTMENT_LOGO = "department_logo";
    final String ATTRIBUTE_NAME_TYPE_ORDER = "type_order";
    final String ATTRIBUTE_NUMBER_OF_PEOPLE = "number_of_people";

    private ArrayList<Map<String, Object>> userOrders;
    private DatabaseService databaseService;
    private List<byte[]> ordersLogos;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OrdersFragment.MyInterface mMyInterface;


    public OrdersRecyclerViewAdapter(ArrayList<Map<String, Object>> userOrders, DatabaseService databaseService, List<byte[]> ordersLogos, SwipeRefreshLayout swipeRefreshLayout, OrdersFragment.MyInterface mMyInterface) {
        this.userOrders = userOrders;
        this.databaseService = databaseService;
        this.ordersLogos = ordersLogos;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.mMyInterface = mMyInterface;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycler_view_item, parent, false);
        OrderViewHolder orderViewHolder = new OrderViewHolder(view, mMyInterface);
        return orderViewHolder;
    }

    @Override
    public void onBindViewHolder(final OrderViewHolder holder, final int position) {
        final Map map = userOrders.get(position);

        holder.orderTime.setText("Дата і час отримання: "+map.get(ATTRIBUTE_DATE).toString());
        holder.orderId = Long.parseLong(map.get(ATTRIBUTE_ORDER_ID).toString());
        holder.orderDateStr = map.get(ATTRIBUTE_DATE).toString();
        holder.restaurantDepartmentId = Long.parseLong(map.get(ATTRIBUTE_DEPARTMENT_ID).toString());
        holder.restaurantNameStr = map.get(ATTRIBUTE_ORDER_NAME).toString();
        holder.restaurantAddressStr = map.get(ATTRIBUTE_DEPARTMENT_ADDRESS).toString();
        holder.numberOfPeople = Integer.parseInt(map.get(ATTRIBUTE_NUMBER_OF_PEOPLE).toString());
        holder.typeOfOrder = map.get(ATTRIBUTE_NAME_TYPE_ORDER).toString();

        holder.restaurantName.setText(map.get(ATTRIBUTE_ORDER_NAME).toString());
        holder.restaurantIcon.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(ordersLogos.get(position), 0, ordersLogos.get(position).length)));
        holder.orderNumber.setText("Номер замовлення: "+map.get(ATTRIBUTE_ORDER_ID).toString());

        if(map.get(ATTRIBUTE_NAME_TYPE_ORDER)=="local_database_order") {
            new getAddressOfDepartment(holder, Long.parseLong(map.get(ATTRIBUTE_DEPARTMENT_ID).toString())).execute(holder.context.getResources().getString(R.string.server_address)+"/get_root_department_address");
        }else
        {
            holder.restaurantAddress.setText("Адреса: "+map.get(ATTRIBUTE_DEPARTMENT_ADDRESS).toString());
        }
        holder.cancelOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeRefreshLayout.setRefreshing(true);
                if(map.get(ATTRIBUTE_NAME_TYPE_ORDER)=="local_database_order") {
                    databaseService.open();
                    databaseService.deleteOrder(Integer.parseInt(map.get(ATTRIBUTE_ORDER_ID).toString()));
                    databaseService.close();
                }
                else {
                    new deleteOrderFromServer(Long.parseLong(map.get(ATTRIBUTE_ORDER_ID).toString())).execute(holder.context.getResources().getString(R.string.server_address)+"/delete_order");
                }
                holder.mMyInterface.onClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userOrders.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private OrdersFragment.MyInterface mMyInterface;
        Context context;
        TextView restaurantName, orderNumber, restaurantAddress, orderTime;
        ImageView restaurantIcon;
        Button cancelOrderButton;
        long orderId, restaurantDepartmentId;
        String orderDateStr, restaurantNameStr, restaurantAddressStr, typeOfOrder;
        int numberOfPeople;
        public OrderViewHolder(final View itemView, OrdersFragment.MyInterface myInterface) {
            super(itemView);
            mMyInterface = myInterface;
            context = itemView.getContext();
            restaurantName = (TextView) itemView.findViewById(R.id.orders_recycler_item_name);
            restaurantIcon = (ImageView) itemView.findViewById(R.id.orders_recycler_item_icon);
            orderNumber = (TextView) itemView.findViewById(R.id.orders_recycler_item_number);
            restaurantAddress = (TextView) itemView.findViewById(R.id.orders_recycler_item_address);
            orderTime = (TextView) itemView.findViewById(R.id.orders_recycler_item_time);
            cancelOrderButton = (Button) itemView.findViewById(R.id.orders_recycler_item_cancel_button);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), OrderConfirmActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("restaurantDepartmentId", restaurantDepartmentId);
                    intent.putExtra("orderDate", orderDateStr);
                    intent.putExtra("restaurantName", restaurantNameStr);
                    intent.putExtra("restaurantAddress", restaurantAddressStr);
                    intent.putExtra("numberOfPeople", numberOfPeople);
                    intent.putExtra("typeOfOrder", typeOfOrder);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    private class getAddressOfDepartment extends AsyncTask<String, Void, String>{
        private OrderViewHolder holder;
        private long restaurantDepartmentId;

        public getAddressOfDepartment(OrderViewHolder holder, long restaurantDepartmentId) {
            this.holder = holder;
            this.restaurantDepartmentId = restaurantDepartmentId;
        }

        @Override
        protected String doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<RestaurantAddress> restaurantAddressResponseEntity = restTemplate.postForEntity(url[0], restaurantDepartmentId, RestaurantAddress.class);
            RestaurantAddress restaurantAddress = restaurantAddressResponseEntity.getBody();
            String address = restaurantAddress.getCountry()+", "+restaurantAddress.getRegion()+", "+restaurantAddress.getCity()+", "+restaurantAddress.getStreet()+", "+restaurantAddress.getHouse();
            return address;
        }

        @Override
        protected void onPostExecute(String address){
            holder.restaurantAddress.setText("Адреса: "+address);
            holder.restaurantAddressStr = address;
        }
    }

    private class deleteOrderFromServer extends AsyncTask<String, Void, Void>{
        private long orderId;

        public deleteOrderFromServer(long orderId) {
            this.orderId = orderId;
        }

        @Override
        protected Void doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.postForEntity(url[0], orderId, Void.class);
            return null;
        }
    }
}

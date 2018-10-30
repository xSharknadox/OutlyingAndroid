package com.example.ihor.outlying1.Fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ihor.outlying1.Adapters.OrdersRecyclerViewAdapter;
import com.example.ihor.outlying1.Classes.OrderObject;
import com.example.ihor.outlying1.Database.DatabaseService;
import com.example.ihor.outlying1.R;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersFragment extends Fragment {
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseService databaseService;
    private List<byte[]> ordersLogos;

    final String ATTRIBUTE_ORDER_ID = "order_id";
    final String ATTRIBUTE_DEPARTMENT_ID = "department_id";
    final String ATTRIBUTE_ORDER_NAME = "order_name";
    final String ATTRIBUTE_DATE = "order_date";
    final String ATTRIBUTE_DEPARTMENT_ADDRESS = "department_address";
    final String ATTRIBUTE_DEPARTMENT_LOGO = "department_logo";
    final String ATTRIBUTE_NAME_TYPE_ORDER = "type_order";
    final String ATTRIBUTE_NUMBER_OF_PEOPLE = "number_of_people";

    private String logosDir;

    public interface MyInterface {
        void onClick();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_orders, container, false);
        databaseService = new DatabaseService(view.getContext());
        logosDir = view.getContext().getExternalCacheDir().toString();
        new GetOrdersTask().execute(getResources().getString(R.string.server_address)+"/orders", logosDir);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.orders_swipe_refreshLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK, getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetOrdersTask().execute(getResources().getString(R.string.server_address)+"/orders", logosDir);
            }
        });

        return view;
    }

    private class GetOrdersTask extends AsyncTask<String, Void, ArrayList<Map<String, Object>>>{

        @Override
        protected ArrayList<Map<String, Object>> doInBackground(String... url) {
            SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            long userId = myPreferences.getLong("userId",-1);
            ResponseEntity<OrderObject[]> ordersResponseEntity =  restTemplate.postForEntity(url[0], userId, OrderObject[].class);
            OrderObject[] ordersMas = ordersResponseEntity.getBody();

            ArrayList<Map<String, Object>> orders = new ArrayList<Map<String, Object>>();
            ordersLogos = new ArrayList<>();
            Map<String, Object> map;
            if(ordersMas.length>0) {
                for (OrderObject orderObject: ordersMas) {
                    map = new HashMap<String, Object>();
                    map.put(ATTRIBUTE_NAME_TYPE_ORDER, "server_order");
                    map.put(ATTRIBUTE_ORDER_ID, orderObject.getOrderId());
                    map.put(ATTRIBUTE_DEPARTMENT_ID, orderObject.getDepartmentId());
                    map.put(ATTRIBUTE_ORDER_NAME, orderObject.getRestaurantName());
                    map.put(ATTRIBUTE_DATE, orderObject.getDate());
                    map.put(ATTRIBUTE_DEPARTMENT_ADDRESS, orderObject.getDepartmentAddress());
                    map.put(ATTRIBUTE_NUMBER_OF_PEOPLE, orderObject.getNumberOfPeople());
                    ordersLogos.add(orderObject.getLogo());
                    orders.add(map);
                }
            }
            databaseService.open();
            List<OrderObject> databaseOrders = databaseService.getAllOrders(url[1]);
            databaseService.close();
            if(databaseOrders.size()>0) {
                for (OrderObject orderObject: databaseOrders) {
                    map = new HashMap<String, Object>();
                    map.put(ATTRIBUTE_NAME_TYPE_ORDER, "local_database_order");
                    map.put(ATTRIBUTE_ORDER_ID, orderObject.getOrderId());
                    map.put(ATTRIBUTE_DEPARTMENT_ID, orderObject.getDepartmentId());
                    map.put(ATTRIBUTE_ORDER_NAME, orderObject.getRestaurantName());
                    map.put(ATTRIBUTE_DATE, orderObject.getDate());
                    map.put(ATTRIBUTE_DEPARTMENT_ADDRESS, orderObject.getDepartmentAddress());
                    map.put(ATTRIBUTE_NUMBER_OF_PEOPLE, orderObject.getNumberOfPeople());
                    ordersLogos.add(orderObject.getLogo());
                    orders.add(map);
                }
            }
            return orders;
        }

        @Override
        protected void onPostExecute(ArrayList<Map<String, Object>> orders) {

            RecyclerView recyclerView = view.findViewById(R.id.orders_recycler_view_orders);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
            recyclerView.setLayoutManager(layoutManager);
            OrdersRecyclerViewAdapter adapter = new OrdersRecyclerViewAdapter(orders, databaseService, ordersLogos, swipeRefreshLayout, new MyInterface(){
                @Override
                public void onClick(){
                    new GetOrdersTask().execute(getResources().getString(R.string.server_address)+"/orders", logosDir);
                }
            });
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetOrdersTask().execute(getResources().getString(R.string.server_address)+"/orders", logosDir);
    }




}

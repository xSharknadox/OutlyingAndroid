package com.example.ihor.outlying1.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ihor.outlying1.Adapters.CategoryDishesRecyclerViewAdapter;
import com.example.ihor.outlying1.Classes.DishObject;
import com.example.ihor.outlying1.Classes.OrderRestaurantInfo;
import com.example.ihor.outlying1.R;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryDishesActivity extends AppCompatActivity {

    private Bundle extras;
    private OrderRestaurantInfo orderRestaurantInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_dishes);

        extras = getIntent().getExtras();

        new GetDishesTask(this).execute(getResources().getString(R.string.server_address)+"/category_dishes",getResources().getString(R.string.server_address)+"/get_first_department");

    }

    private class GetDishesTask extends AsyncTask<String, Void, DishObject[]>{
        private Context context;

        public GetDishesTask(Context context) {
            this.context = context;
        }

        @Override
        protected DishObject[] doInBackground(String... url) {
            long[] ids = {extras.getLong("tagRestaurantId"), extras.getLong("menuCategoryId")};
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<DishObject[]> dishObjectResponseEntity = restTemplate.postForEntity(url[0], ids, DishObject[].class);
            DishObject[] dishes = dishObjectResponseEntity.getBody();

            SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(CategoryDishesActivity.this);

            String[] address = {extras.getLong("tagRestaurantId")+"", myPreferences.getString("userCountry",""), myPreferences.getString("userRegion", ""), myPreferences.getString("userCity","")};
            ResponseEntity<OrderRestaurantInfo> orderRestaurantInfoResponseEntity = restTemplate.postForEntity(url[1], address, OrderRestaurantInfo.class);
            orderRestaurantInfo = orderRestaurantInfoResponseEntity.getBody();

            return dishes;
        }

        @Override
        protected void onPostExecute(DishObject[] dishes) {

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.category_dishes_recycler_view);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            CategoryDishesRecyclerViewAdapter adapter = new CategoryDishesRecyclerViewAdapter(dishes, orderRestaurantInfo, context);
            recyclerView.setAdapter(adapter);
        }
    }
}

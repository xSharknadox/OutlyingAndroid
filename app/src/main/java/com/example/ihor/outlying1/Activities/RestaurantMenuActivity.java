package com.example.ihor.outlying1.Activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dgreenhalgh.android.simpleitemdecoration.grid.GridDividerItemDecoration;
import com.example.ihor.outlying1.Adapters.RestaurantMenuRecyclerViewAdapter;
import com.example.ihor.outlying1.Classes.MenuItemObject;
import com.example.ihor.outlying1.R;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RestaurantMenuActivity extends AppCompatActivity {

    final String ATTRIBUTE_NAME_MENUITEMIMAGE = "menu_item_image";
    final String ATTRIBUTE_NAME_MENUITEMNAME = "menu_item_name";
    final String ATTRIBUTE_NAME_MENUITEMID = "menu_item_id";
    private Bundle extras;
    private long tagRestaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);

        new GetMenuTask(this).execute(getResources().getString(R.string.server_address)+"/menu");

    }

    private class GetMenuTask extends AsyncTask<String, Void, ArrayList<Map<String, Object>>>{
        private Context rootContext;

        public GetMenuTask(Context rootContext) {
            this.rootContext = rootContext;
        }

        @Override
        protected ArrayList<Map<String, Object>> doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            extras = getIntent().getExtras();
            tagRestaurantId = extras.getLong("tagRestaurantId");
            ResponseEntity<MenuItemObject[]> responseMenuEntity =  restTemplate.postForEntity(url[0], tagRestaurantId, MenuItemObject[].class);
            MenuItemObject[] menuObjects = responseMenuEntity.getBody();

            //Пофіксити щоб замість мапи відправляти просто масив menuObjects
            ArrayList<Map<String, Object>> menu = new ArrayList<Map<String, Object>>();
            Map<String, Object> map = new HashMap<String, Object>();
            for (MenuItemObject menuItem : menuObjects) {
                map = new HashMap<String, Object>();
                map.put(ATTRIBUTE_NAME_MENUITEMID, menuItem.getId());
                map.put(ATTRIBUTE_NAME_MENUITEMNAME, menuItem.getCategory());
                map.put(ATTRIBUTE_NAME_MENUITEMIMAGE, R.drawable.ic_test);
                menu.add(map);
            }

            return menu;
        }

        @Override
        protected void onPostExecute(ArrayList<Map<String, Object>> menu) {

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.restaurant_menu_recycler_view);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(rootContext, 2);
            recyclerView.setLayoutManager(layoutManager);
            RestaurantMenuRecyclerViewAdapter adapter = new RestaurantMenuRecyclerViewAdapter(menu, tagRestaurantId);
            recyclerView.setAdapter(adapter);
            Drawable horizontalDivider = ContextCompat.getDrawable(rootContext, R.drawable.line_divider_restaurant_menu);
            Drawable verticalDivider = ContextCompat.getDrawable(rootContext, R.drawable.line_divider_restaurant_menu);
            recyclerView.addItemDecoration(new GridDividerItemDecoration(horizontalDivider, verticalDivider, 2));
        }
    }
}

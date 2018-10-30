package com.example.ihor.outlying1.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ihor.outlying1.Adapters.RestaurantListAdapter;
import com.example.ihor.outlying1.Classes.RestaurantNameObject;
import com.example.ihor.outlying1.R;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RestaurantsFragment extends Fragment {

    final String ATTRIBUTE_NAME_RESTAURANTID = "restaurant_id";
    final String ATTRIBUTE_NAME_RESTAURANTIMAGE = "restaurant_image";
    final String ATTRIBUTE_NAME_LISTCATEGORY = "list_category";
    final String ATTRIBUTE_NAME_LISTCATEGORY_TITLE = "list_category_title";
    final String ATTRIBUTE_NAME_RESTAURANTNAME="restaurant_name";

    private Location mLocation;
    private LocationManager locationManager;
    private Geocoder geocoder;
    private String provider = LocationManager.NETWORK_PROVIDER;
    private List<Address> addresses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurants, container, false);

        locationManager = (LocationManager) view.getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        geocoder = new Geocoder(view.getContext(), Locale.getDefault());
        locationManager.requestLocationUpdates(provider,  60*10, 50, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        if(mLocation==null) {
            mLocation = locationManager.getLastKnownLocation(provider);
            try {
                addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        new GetRestaurantsTask(view).execute(getResources().getString(R.string.server_address)+"/favorite_restaurants", getResources().getString(R.string.server_address)+"/all_restaurants");

        return view;
    }

    private class GetRestaurantsTask extends AsyncTask<String, Void, ArrayList<Map<String, Object>>>{
        private View rootView;

        public GetRestaurantsTask(View rootView) {
            this.rootView = rootView;
        }

        @Override
        protected ArrayList<Map<String, Object>> doInBackground(String... url) {
            SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            SharedPreferences.Editor myEditor = myPreferences.edit();
            myEditor.putString("userCountry", addresses.get(0).getCountryName());
            myEditor.putString("userRegion", addresses.get(0).getAdminArea());
            myEditor.putString("userCity", addresses.get(0).getLocality());
            myEditor.commit();

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            String[] requestValue = new String[]{myPreferences.getLong("userId",-1)+"", addresses.get(0).getCountryName(), addresses.get(0).getAdminArea(), addresses.get(0).getLocality()};
            ResponseEntity<RestaurantNameObject[]> responseFavoriteEntity =  restTemplate.postForEntity(url[0], requestValue, RestaurantNameObject[].class);
            RestaurantNameObject[] restaurantsFavorite = responseFavoriteEntity.getBody();

            requestValue = new String[]{addresses.get(0).getCountryName(), addresses.get(0).getAdminArea(), addresses.get(0).getLocality()};
            ResponseEntity<RestaurantNameObject[]> responseAllEntity =  restTemplate.postForEntity(url[1], requestValue, RestaurantNameObject[].class);
            RestaurantNameObject[] restaurantsAll = responseAllEntity.getBody();

            ArrayList<Map<String, Object>> userRestaurants = new ArrayList<Map<String, Object>>();
            Map<String, Object> map = new HashMap<String, Object>();
            if(restaurantsFavorite.length>0) {
                map.put(ATTRIBUTE_NAME_LISTCATEGORY_TITLE, "Favorite restaurants");
                map.put(ATTRIBUTE_NAME_LISTCATEGORY, "title");
                userRestaurants.add(map);
                for (RestaurantNameObject restaurant : restaurantsFavorite) {
                    map = new HashMap<String, Object>();
                    map.put(ATTRIBUTE_NAME_RESTAURANTID, restaurant.getId());
                    map.put(ATTRIBUTE_NAME_RESTAURANTNAME, restaurant.getName());
                    map.put(ATTRIBUTE_NAME_LISTCATEGORY, "restaurant");
                    userRestaurants.add(map);
                }
            }
            else if(restaurantsAll.length>0) {
                map = new HashMap<String, Object>();
                map.put(ATTRIBUTE_NAME_LISTCATEGORY_TITLE, "All restaurants");
                map.put(ATTRIBUTE_NAME_LISTCATEGORY, "title");
                userRestaurants.add(map);
                for (RestaurantNameObject restaurant : restaurantsAll) {
                    map = new HashMap<String, Object>();
                    map.put(ATTRIBUTE_NAME_RESTAURANTID, restaurant.getId());
                    map.put(ATTRIBUTE_NAME_RESTAURANTNAME, restaurant.getName());
                    map.put(ATTRIBUTE_NAME_LISTCATEGORY, "restaurant");
                    userRestaurants.add(map);
                }
            }else{
                map = new HashMap<String, Object>();
                map.put(ATTRIBUTE_NAME_LISTCATEGORY_TITLE, "In this district we didn`t have any restaurants");
                map.put(ATTRIBUTE_NAME_LISTCATEGORY, "title");
            }

            return userRestaurants;
        }

        @Override
        protected void onPostExecute(ArrayList<Map<String, Object>> userRestaurants) {

            RecyclerView recyclerViewRestaurants = (RecyclerView) rootView.findViewById(R.id.restaurants_recycleview_restaurants);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
            recyclerViewRestaurants.setLayoutManager(layoutManager);
            RestaurantListAdapter adapter = new RestaurantListAdapter(userRestaurants);
            recyclerViewRestaurants.setAdapter(adapter);
        }
    }

}

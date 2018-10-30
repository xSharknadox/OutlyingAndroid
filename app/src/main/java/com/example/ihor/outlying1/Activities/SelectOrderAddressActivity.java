package com.example.ihor.outlying1.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.ihor.outlying1.Classes.AddressUnit;
import com.example.ihor.outlying1.Classes.RestaurantAddress;
import com.example.ihor.outlying1.Database.DatabaseService;
import com.example.ihor.outlying1.R;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class SelectOrderAddressActivity extends AppCompatActivity {

    private String[] countriesData, regionsData, citiesData, streetsData, housesData;
    private int counrtyPosition, regionPosition, cityPosition, streetPosition, housePosition;
    private AddressUnit[] countriesUnit, regionsUnit, citiesUnit, streetsUnit, houseUnit;
    private RestaurantAddress rootAddress;
    private Bundle extras;

    private long orderId, restaurantDepartmentId;
    private String restaurantName, rootRestaurantAddressForCancel;
    private int numberOfPeople;
    private String date="";
    private String typeOfOrder="";
    private long rootRestaurantDepartmentIdForCancel;

    private Spinner countriesSpinner, regionsSpinner,citiesSpinner, streetsSpinner, housesSpinner ;

    private DatabaseService databaseService = new DatabaseService(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_order_address);

        extras = getIntent().getExtras();
        orderId = extras.getLong("orderId");
        restaurantDepartmentId = extras.getLong("restaurantDepartmentId");
        rootRestaurantDepartmentIdForCancel = restaurantDepartmentId;
        restaurantName = extras.getString("restaurantName");
        rootRestaurantAddressForCancel = extras.getString("restaurantAddress");
        numberOfPeople = extras.getInt("numberOfPeople");
        date = extras.getString("orderDate");
        typeOfOrder = extras.getString("typeOfOrder");


        countriesSpinner = (Spinner) findViewById(R.id.select_order_address_countries_spinner);
        countriesSpinner.setPrompt("Країна");


        regionsSpinner = (Spinner) findViewById(R.id.select_order_address_regions_spinner);
        regionsSpinner.setPrompt("Область");


        citiesSpinner = (Spinner) findViewById(R.id.select_order_address_cities_spinner);
        citiesSpinner.setPrompt("Місто");


        streetsSpinner = (Spinner) findViewById(R.id.select_order_address_streets_spinner);
        streetsSpinner.setPrompt("Вулиця");


        housesSpinner = (Spinner) findViewById(R.id.select_order_address_houses_spinner);
        housesSpinner.setPrompt("Дім");

        new getRootDepartmentAddressTask().execute(getResources().getString(R.string.server_address)+"/get_root_department_address");

        Button okButton = (Button) findViewById(R.id.select_order_address_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    new getDepartmentByAddress().execute(getResources().getString(R.string.server_address)+"/get_department_by_address");
            }
        });
    }

    private class getAllRestaurantsAddressesCountriesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<AddressUnit[]> responseCountriesEntity =  restTemplate.postForEntity(url[0], restaurantDepartmentId, AddressUnit[].class);
            countriesUnit = responseCountriesEntity.getBody();
            countriesData = new String[countriesUnit.length];
            for(int x=0; x < countriesUnit.length ; x++){
                countriesData[x] = countriesUnit[x].getName();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String sad) {
            ArrayAdapter<String> countriesAdapter = new ArrayAdapter<String>(SelectOrderAddressActivity.this, android.R.layout.simple_spinner_item, countriesData);
            countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            countriesSpinner.setAdapter(countriesAdapter);



            boolean hz = true;
            int x=0;
            while (hz){
                if(countriesData[x].equals(rootAddress.getCountry())){
                    counrtyPosition=x;
                    countriesSpinner.setSelection(counrtyPosition);
                    hz=false;
                }
                else {
                    counrtyPosition=0;
                }
                x++;
                if (x>=countriesData.length){
                    rootAddress.setCountryId(countriesUnit[counrtyPosition].getId());
                    rootAddress.setCountry(countriesUnit[counrtyPosition].getName());
                    countriesSpinner.setSelection(counrtyPosition);
                    hz=false;
                }
            }

            countriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    counrtyPosition = position;
                    rootAddress.setCountry(countriesData[counrtyPosition]);
                    rootAddress.setCountryId(countriesUnit[counrtyPosition].getId());
                    new getAllRestaurantsAddressesRegionsTask().execute(getResources().getString(R.string.server_address)+"/get_all_restaurant_regions");
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            new getAllRestaurantsAddressesRegionsTask().execute(getResources().getString(R.string.server_address)+"/get_all_restaurant_regions");
        }
    }

    private class getAllRestaurantsAddressesRegionsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            long[] request = {restaurantDepartmentId, countriesUnit[counrtyPosition].getId()};
            ResponseEntity<AddressUnit[]> responseRegionsEntity =  restTemplate.postForEntity(url[0], request, AddressUnit[].class);
            regionsUnit = responseRegionsEntity.getBody();
            regionsData = new String[regionsUnit.length];
            for(int x=0; x < regionsUnit.length ; x++){
                regionsData[x] = regionsUnit[x].getName();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String sad) {
            ArrayAdapter<String> regionsAdapter = new ArrayAdapter<String>(SelectOrderAddressActivity.this, android.R.layout.simple_spinner_item, regionsData);
            regionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            regionsSpinner.setAdapter(regionsAdapter);

            boolean hz = true;
            int x=0;
            while (hz){
                if(regionsData[x].equals(rootAddress.getRegion())){
                    regionPosition=x;
                    regionsSpinner.setSelection(regionPosition);
                    hz=false;
                }
                else {
                    regionPosition=0;
                }
                x++;
                if (x>=regionsData.length){
                    regionsSpinner.setSelection(regionPosition);
                    rootAddress.setRegionId(regionsUnit[regionPosition].getId());
                    rootAddress.setRegion(regionsData[regionPosition]);
                    hz=false;
                }
            }

            regionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    regionPosition = position;
                    rootAddress.setRegion(regionsData[regionPosition]);
                    rootAddress.setRegionId(regionsUnit[regionPosition].getId());

                    new getAllRestaurantsAddressesCitiesTask().execute(getResources().getString(R.string.server_address)+"/get_all_restaurant_cities");
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            new getAllRestaurantsAddressesCitiesTask().execute(getResources().getString(R.string.server_address)+"/get_all_restaurant_cities");
        }
    }

    private class getAllRestaurantsAddressesCitiesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            long[] request = {restaurantDepartmentId, countriesUnit[counrtyPosition].getId(), regionsUnit[regionPosition].getId()};
            ResponseEntity<AddressUnit[]> responseCitiesEntity =  restTemplate.postForEntity(url[0], request, AddressUnit[].class);
            citiesUnit = responseCitiesEntity.getBody();
            citiesData = new String[citiesUnit.length];
            for(int x=0; x < citiesUnit.length ; x++){
                citiesData[x] = citiesUnit[x].getName();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String sad) {
            ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(SelectOrderAddressActivity.this, android.R.layout.simple_spinner_item, citiesData);
            citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            citiesSpinner.setAdapter(citiesAdapter);

            boolean hz = true;
            int x=0;
            while (hz){
                if(citiesData[x].equals(rootAddress.getCity())){
                    cityPosition=x;
                    citiesSpinner.setSelection(cityPosition);
                    hz=false;
                }
                else {
                    cityPosition=0;
                }
                x++;
                if (x>=citiesData.length){
                    citiesSpinner.setSelection(cityPosition);
                    rootAddress.setCity(citiesUnit[cityPosition].getName());
                    rootAddress.setCityId(citiesUnit[cityPosition].getId());
                    hz=false;
                }
            }

            citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    cityPosition = position;
                    rootAddress.setCity(citiesData[cityPosition]);
                    rootAddress.setCityId(citiesUnit[cityPosition].getId());
                    new getAllRestaurantsAddressesStreetsTask().execute(getResources().getString(R.string.server_address)+"/get_all_restaurant_streets");
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            new getAllRestaurantsAddressesStreetsTask().execute(getResources().getString(R.string.server_address)+"/get_all_restaurant_streets");
        }
    }

    private class getAllRestaurantsAddressesStreetsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            long[] request = {restaurantDepartmentId, countriesUnit[counrtyPosition].getId(), regionsUnit[regionPosition].getId(), citiesUnit[cityPosition].getId()};
            ResponseEntity<AddressUnit[]> responseStreetsEntity =  restTemplate.postForEntity(url[0], request, AddressUnit[].class);
            streetsUnit = responseStreetsEntity.getBody();
            streetsData = new String[streetsUnit.length];
            for(int x=0; x < streetsUnit.length ; x++){
                streetsData[x] = streetsUnit[x].getName();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String sad) {
            ArrayAdapter<String> streetsAdapter = new ArrayAdapter<String>(SelectOrderAddressActivity.this, android.R.layout.simple_spinner_item, streetsData);
            streetsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            streetsSpinner.setAdapter(streetsAdapter);

            boolean hz = true;
            int x=0;
            while (hz){
                if(streetsData[x].equals(rootAddress.getStreet())){
                    streetPosition=x;
                    streetsSpinner.setSelection(streetPosition);
                    hz=false;
                }
                else {
                    streetPosition=0;
                }
                x++;
                if (x>=streetsData.length){
                    streetsSpinner.setSelection(streetPosition);
                    rootAddress.setStreet(streetsUnit[streetPosition].getName());
                    rootAddress.setStreetId(streetsUnit[streetPosition].getId());
                    hz=false;
                }
            }

            streetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    streetPosition = position;
                    rootAddress.setStreet(streetsData[streetPosition]);
                    rootAddress.setStreetId(streetsUnit[streetPosition].getId());
                    new getAllRestaurantsAddressesHousesTask().execute(getResources().getString(R.string.server_address)+"/get_all_restaurant_houses");
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            new getAllRestaurantsAddressesHousesTask().execute(getResources().getString(R.string.server_address)+"/get_all_restaurant_houses");
        }
    }

    private class getAllRestaurantsAddressesHousesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            long[] request = {restaurantDepartmentId, countriesUnit[counrtyPosition].getId(), regionsUnit[regionPosition].getId(), citiesUnit[cityPosition].getId(), streetsUnit[streetPosition].getId()};
            ResponseEntity<AddressUnit[]> responseHousesEntity =  restTemplate.postForEntity(url[0], request, AddressUnit[].class);
            houseUnit = responseHousesEntity.getBody();
            housesData = new String[houseUnit.length];
            for(int x=0; x < houseUnit.length ; x++){
                housesData[x] = houseUnit[x].getName();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String sad) {
            ArrayAdapter<String> housesAdapter = new ArrayAdapter<String>(SelectOrderAddressActivity.this, android.R.layout.simple_spinner_item, housesData);
            housesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            housesSpinner.setAdapter(housesAdapter);

            boolean hz = true;
            int x=0;
            while (hz){
                if(housesData[x].equals(rootAddress.getHouse())){
                    housePosition=x;
                    housesSpinner.setSelection(housePosition);
                    hz=false;
                }
                else {
                    housePosition=0;
                }
                x++;
                if (x>=housesData.length){
                    housesSpinner.setSelection(housePosition);
                    rootAddress.setHouse(houseUnit[housePosition].getName());
                    rootAddress.setHouseId(houseUnit[housePosition].getId());
                    hz=false;
                }
            }

            housesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    housePosition = position;
                    rootAddress.setHouse(housesData[housePosition]);
                    rootAddress.setHouseId(houseUnit[housePosition].getId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    private class getRootDepartmentAddressTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<RestaurantAddress> responseCountriesEntity =  restTemplate.postForEntity(url[0], restaurantDepartmentId, RestaurantAddress.class);
            rootAddress = responseCountriesEntity.getBody();
            return "";
        }

        @Override
        protected void onPostExecute(String sad) {
            new getAllRestaurantsAddressesCountriesTask().execute(getResources().getString(R.string.server_address)+"/get_all_restaurant_countries");
        }
    }

    private class getDepartmentByAddress extends AsyncTask<String, Void, Long>{

        @Override
        protected Long doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<Long> departmentResponseEntity = restTemplate.postForEntity(url[0], new long[]{restaurantDepartmentId,rootAddress.getCountryId(), rootAddress.getRegionId(), rootAddress.getCityId(), rootAddress.getStreetId(), rootAddress.getHouseId()}, Long.class);
            return departmentResponseEntity.getBody();
        }

        @Override
        protected void onPostExecute(Long id) {
            restaurantDepartmentId = id;
            if(typeOfOrder.equals("server_order")){
                new setDepatmentInOrder().execute(getResources().getString(R.string.server_address)+"/set_department_in_order");
            }else {
                databaseService.open();
                databaseService.updateOrderDepartment((int)orderId, (int)restaurantDepartmentId);
                databaseService.close();
                String address = countriesData[counrtyPosition]+", "+regionsData[regionPosition]+", "+citiesData[cityPosition]+", "+streetsData[streetPosition]+", "+housesData[housePosition];
                Intent intent = new Intent(SelectOrderAddressActivity.this, OrderConfirmActivity.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("restaurantDepartmentId", restaurantDepartmentId);
                intent.putExtra("orderDate", date);
                intent.putExtra("restaurantName", restaurantName);
                intent.putExtra("restaurantAddress", address);
                intent.putExtra("numberOfPeople", numberOfPeople);
                intent.putExtra("typeOfOrder", typeOfOrder);
                startActivity(intent);
                finish();
            }
        }
    }

    private class setDepatmentInOrder extends AsyncTask<String, Void, Long>{

        @Override
        protected Long doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.postForEntity(url[0], new long[]{orderId, restaurantDepartmentId}, Long.class);
            return null;
        }

        @Override
        protected void onPostExecute(Long id) {
            String address = countriesData[counrtyPosition]+", "+regionsData[regionPosition]+", "+citiesData[cityPosition]+", "+streetsData[streetPosition]+", "+housesData[housePosition];
            Intent intent = new Intent(SelectOrderAddressActivity.this, OrderConfirmActivity.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("restaurantDepartmentId", restaurantDepartmentId);
            intent.putExtra("orderDate", date);
            intent.putExtra("restaurantName", restaurantName);
            intent.putExtra("restaurantAddress", address);
            intent.putExtra("numberOfPeople", numberOfPeople);
            intent.putExtra("typeOfOrder", typeOfOrder);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(SelectOrderAddressActivity.this, OrderConfirmActivity.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("restaurantDepartmentId", rootRestaurantDepartmentIdForCancel);
        intent.putExtra("orderDate", date);
        intent.putExtra("restaurantName", restaurantName);
        intent.putExtra("restaurantAddress", rootRestaurantAddressForCancel);
        intent.putExtra("numberOfPeople", numberOfPeople);
        intent.putExtra("typeOfOrder", typeOfOrder);
        startActivity(intent);
        finish();
    }
}

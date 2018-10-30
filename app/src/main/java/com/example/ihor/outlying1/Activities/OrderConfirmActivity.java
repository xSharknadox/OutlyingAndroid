package com.example.ihor.outlying1.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.ihor.outlying1.Adapters.OrderConfirmRecyclerViewAdapter;
import com.example.ihor.outlying1.Classes.OrderDishObject;
import com.example.ihor.outlying1.Database.DatabaseService;
import com.example.ihor.outlying1.R;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;


public class OrderConfirmActivity extends AppCompatActivity {

    private int DIALOG_DATE = 1;
    private int DIALOG_TIME = 2;
    private int myYear;
    private int myMonth;
    private int myDay;
    private int myHour;
    private int myMinute;
    private TextView time;
    private boolean wasUpdate = false;
    private Context context = this;
    private double totalPrice = 0;

    private Bundle extras;

    private long orderId, restaurantDepartmentId;
    private String restaurantName, restaurantAddress;
    private int numberOfPeople;
    private String date="";
    private String dateExtras[];
    private TextView allSumText;

    private String typeOfOrder="";

    private DatabaseService databaseService = new DatabaseService(this);

    private TextView numberOfPeopleTextView;

    private Button confirmButton;

    private SharedPreferences myPreferences;

    private OrderDishObject[] rootOrderDishes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        extras = getIntent().getExtras();

        myPreferences = PreferenceManager.getDefaultSharedPreferences(OrderConfirmActivity.this);

        orderId = extras.getLong("orderId");
        restaurantDepartmentId = extras.getLong("restaurantDepartmentId");
        restaurantName = extras.getString("restaurantName");
        restaurantAddress = extras.getString("restaurantAddress");
        numberOfPeople = extras.getInt("numberOfPeople");
        typeOfOrder = extras.getString("typeOfOrder");

        dateExtras = extras.getString("orderDate").split("\\W");
        myDay = Integer.parseInt(dateExtras[0]);
        myMonth = Integer.parseInt(dateExtras[1]);
        myYear = Integer.parseInt(dateExtras[2]);
        myHour = Integer.parseInt(dateExtras[3]);
        myMinute = Integer.parseInt(dateExtras[4]);


        date+= setCorrectNumber(myDay) + "." +setCorrectNumber(myMonth) + "." + myYear + "," + setCorrectNumber(myHour)+ ":" + setCorrectNumber(myMinute);
        time = (TextView) findViewById(R.id.order_confirm_restaurant_time);
        time.setText("Замовлено на: "+date);
        Button changeTime = (Button) findViewById(R.id.order_confirm_restaurant_change_time_button);
        changeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_DATE);
            }
        });

        TextView orderNumberTextView = (TextView) findViewById(R.id.order_confirm_order_number);
        TextView restaurantNameTextView = (TextView) findViewById(R.id.order_confirm_restaurant_name);
        TextView restaurantAddressTextView = (TextView) findViewById(R.id.order_confirm_restaurant_address);
        numberOfPeopleTextView = (TextView) findViewById(R.id.order_confirm_number_of_people);
        allSumText= (TextView) findViewById(R.id.order_confirm_amount);

        orderNumberTextView.setText("Номер замовлення: "+orderId);
        restaurantNameTextView.setText(restaurantName);
        restaurantAddressTextView.setText("Адреса: "+restaurantAddress);
        numberOfPeopleTextView.setText("Кількість чоловік: "+numberOfPeople);

        Button changeOrderAddress = (Button) findViewById(R.id.order_confirm_restaurant_change_address_button);
        changeOrderAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderConfirmActivity.this, SelectOrderAddressActivity.class);
                intent.putExtra("restaurantDepartmentId", restaurantDepartmentId);
                intent.putExtra("orderId", orderId);
                intent.putExtra("restaurantDepartmentId", restaurantDepartmentId);
                intent.putExtra("orderDate", date);
                intent.putExtra("restaurantName", restaurantName);
                intent.putExtra("restaurantAddress", restaurantAddress);
                intent.putExtra("numberOfPeople", numberOfPeople);
                intent.putExtra("typeOfOrder", typeOfOrder);
                startActivity(intent);
                finish();
            }
        });

        Button changeOrderNumberOfPeople = (Button) findViewById(R.id.order_confirm_restaurant_change_number_of_people_button);
        changeOrderNumberOfPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View dialogView = layoutInflater.inflate(R.layout.dialog_change_number_of_people, null);

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setView(dialogView);

                final EditText userInput = (EditText) dialogView.findViewById(R.id.dialog_change_number_of_people_edit_text);

                dialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                numberOfPeople = Integer.parseInt(userInput.getText().toString());
                                numberOfPeopleTextView.setText("Кількість чоловік: "+numberOfPeople);
                                if(typeOfOrder.equals("server_order")){
                                    new setNumberOfPeopleInOrder().execute(getResources().getString(R.string.server_address)+"/set_number_of_people_in_order");
                                }else {
                                    databaseService.open();
                                    databaseService.updateOrderNumberOfPeople((int)orderId, numberOfPeople);
                                    databaseService.close();
                                }
                            }
                        })
                        .setNegativeButton("Відміна", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });

        confirmButton = (Button) findViewById(R.id.order_confirm_order_accept_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(typeOfOrder.equals("local_database_order")){
                    new ConfirmDatabaseOrder().execute(getResources().getString(R.string.server_address)+"/add_order",getResources().getString(R.string.server_address)+"/add_order_dish");
                }
            }
        });

        if(typeOfOrder.equals("server_order")){
            new GetOrderDishesTask(this).execute(getResources().getString(R.string.server_address)+"/order_dishes");
        }
        else {
            databaseService.open();
            rootOrderDishes = databaseService.getAllOrderDishes((int)orderId);
            databaseService.close();
            for (OrderDishObject orderDish: rootOrderDishes){
                totalPrice+=orderDish.getPrice()*orderDish.getNumberOfDish();
            }
            allSumText.setText("Сума заказу: "+totalPrice+" грн");
            RecyclerView recyclerView = findViewById(R.id.orders_confirm_recycler_view);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            OrderConfirmRecyclerViewAdapter adapter = new OrderConfirmRecyclerViewAdapter(rootOrderDishes, typeOfOrder, orderId, allSumText, totalPrice, databaseService);
            recyclerView.setAdapter(adapter);
        }


    }

    private String setCorrectNumber(int number){
        String correctNumber="";
        return correctNumber+= (number<10)? "0"+number: number;
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, DateListener, myYear, myMonth, myDay);
            return tpd;
        }

        if (id == DIALOG_TIME) {
            TimePickerDialog tpd = new TimePickerDialog(this, TimeListener, myHour, myMinute, true);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    TimePickerDialog.OnTimeSetListener TimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myHour = hourOfDay;
            myMinute = minute;
            date += "," + setCorrectNumber(myHour)+ ":" + setCorrectNumber(myMinute);
            time.setText("Замовлено на: "+date);
            if(typeOfOrder.equals("server_order")){
                new ChangeOrderDateTask().execute(getResources().getString(R.string.server_address)+"/change_date_of_order");
            }else {
                databaseService.open();
                databaseService.updateOrderDate((int)orderId, date);
                databaseService.close();
            }
        }
    };

    DatePickerDialog.OnDateSetListener DateListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear+1;
            myDay = dayOfMonth;
            showDialog(DIALOG_TIME);
            date=setCorrectNumber(myDay) + "." +setCorrectNumber(myMonth) + "." + myYear;
        }
    };

    private class ChangeOrderDateTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            String needDate="";
            if(myHour<10){
                needDate += myDay + "." + myMonth + "." + myYear + ",0"+ myHour + ":";
            }
            else {
                needDate += myDay + "." + myMonth + "." + myYear + ","+ myHour + ":";
            }
            if(myMinute<10){
                needDate += "0"+myMinute + ".";
            }
            else {
                needDate += myMinute + ".";
            }
            String[] data = new String[]{extras.getLong("orderId")+"", needDate};
            restTemplate.postForEntity(url[0], data, Void.class);
            wasUpdate = true;
            return null;
        }
    }

    private class GetOrderDishesTask extends AsyncTask<String, Void, OrderDishObject[]>{
        private Context context;

        public GetOrderDishesTask(Context context) {
            this.context = context;
        }

        @Override
        protected OrderDishObject[] doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            long orderId = extras.getLong("orderId");
            ResponseEntity<OrderDishObject[]> orderDishesResponceEntity = restTemplate.postForEntity(url[0], orderId, OrderDishObject[].class);
            rootOrderDishes = orderDishesResponceEntity.getBody();
            return rootOrderDishes;
        }

        @Override
        protected void onPostExecute(OrderDishObject[] orderDishes){
            for (OrderDishObject orderDish: orderDishes){
                totalPrice+=orderDish.getPrice()*orderDish.getNumberOfDish();
            }
            allSumText.setText("Сума заказу: "+totalPrice+" грн");
            RecyclerView recyclerView = findViewById(R.id.orders_confirm_recycler_view);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            OrderConfirmRecyclerViewAdapter adapter = new OrderConfirmRecyclerViewAdapter(orderDishes, typeOfOrder, orderId, allSumText, totalPrice, databaseService);
            recyclerView.setAdapter(adapter);
        }
    }

    private class setNumberOfPeopleInOrder extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.postForEntity(url[0], new long[]{orderId, numberOfPeople}, null);
            return null;
        }
    }

    private class ConfirmDatabaseOrder extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... url) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            ResponseEntity<Long> responseEntity= restTemplate.postForEntity(url[0], new String[]{myPreferences.getLong("userId", -1)+"", restaurantDepartmentId+"", date, numberOfPeople+"", totalPrice+""}, Long.class);
            long newIdOfOrder = responseEntity.getBody();
            databaseService.open();
            for(OrderDishObject orderDishObject: rootOrderDishes){
                restTemplate.postForEntity(url[1], new long[]{orderDishObject.getDishId(), newIdOfOrder, orderDishObject.getNumberOfDish()}, Void.class);
                databaseService.deleteDishOfOrder((int)orderId, (int)orderDishObject.getDishId());
            }
            databaseService.deleteOrder((int)orderId);
            databaseService.close();
            return null;
        }
    }

    /*@Override
    public void onBackPressed(){
        //Потрібно пофіксити на більш підходящий код
        if(wasUpdate){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }*/
}

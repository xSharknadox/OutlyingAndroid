package com.example.ihor.outlying1.Activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ihor.outlying1.Classes.UserObject;
import com.example.ihor.outlying1.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class LoginActivity extends AppCompatActivity{

    final String DIR_SD = "OutlyingFiles";
    final String FILENAME_SD = "UserData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView sign_up  = (TextView) findViewById(R.id.login_register_link_text);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        Button sign_in = (Button) findViewById(R.id.login_sing_in_button);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginTask().execute(getResources().getString(R.string.server_address)+"/login");
            }
        });
    }

    private class LoginTask extends AsyncTask<String, Void , UserObject>{

        @Override
        protected UserObject doInBackground(String... url) {
            EditText emailEditText = (EditText) findViewById(R.id.login_edit_text_email);
            EditText passwordEditText = (EditText) findViewById(R.id.login_edit_text_password);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            HttpHeaders headers = new HttpHeaders();
            headers.add("email", emailEditText.getText().toString());
            headers.add("password", passwordEditText.getText().toString());
            HttpEntity<String> request = new HttpEntity<String>(headers);
            UserObject response = restTemplate.postForObject(url[0], request, UserObject.class);
            return response;
        }

        @Override
        protected void onPostExecute(UserObject response) {
            if(response.getId()==-1){

            }else {

                SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor myEditor = myPreferences.edit();
                myEditor.putLong("userId", response.getId());
                myEditor.putString("userName", response.getName());
                myEditor.putString("userSurname", response.getSurname());
                String folderToSave = Environment.getExternalStorageDirectory().toString();
                String imageName = SavePicture(response.getPhoto(), folderToSave);
                myEditor.putString("userImagePath", folderToSave+"//"+imageName);
                myEditor.putLong("userBankAccount", response.getBankAccount());
                myEditor.commit();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    private String SavePicture(byte[] imageArray, String folderToSave)
    {
        OutputStream fOut = null;
        Time time = new Time();
        time.setToNow();
        String imageName = "image"+Integer.toString(time.year) + Integer.toString(time.month) + Integer.toString(time.monthDay) + Integer.toString(time.hour) + Integer.toString(time.minute) + Integer.toString(time.second) +".jpg";
        try {
            File file = new File(imageName); // создать уникальное имя для файла основываясь на дате сохранения
            fOut = new FileOutputStream(file);

            Bitmap bitmap =  BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // сохранять картинку в jpeg-формате с 85% сжатия.
            fOut.flush();
            fOut.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(),  file.getName()); // регистрация в фотоальбоме
        }
        catch (Exception e) // здесь необходим блок отслеживания реальных ошибок и исключений, общий Exception приведен в качестве примера
        {
            return e.getMessage();
        }
        return imageName;
    }
}
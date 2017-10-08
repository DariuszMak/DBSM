package com.example.dariusz.testapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    Toast toast;
    String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String password = "";
        sharedPreferences = getSharedPreferences("com.example.dariusz.testapp", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        context = getApplicationContext();


        password = sharedPreferences.getString("password", "default");

        Log.d("Password", password);

        if (password == "default") {
            Log.d("PasswordMessage", "Default password");
            message = "Hasło nie istnieje!";
        } else {
            Log.d("PasswordMessage", "Password OK");
            message = "Hasło istnieje";
        }

        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();


        editor.commit();
    }

    public void chceckPassword (View view)
    {
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
    }
}

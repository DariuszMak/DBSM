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
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    Toast toast;
    EditText textField;
    String message = "";
    String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textField = (EditText) findViewById(R.id.editTextPassword);

        sharedPreferences = getSharedPreferences("com.example.dariusz.testapp", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        context = getApplicationContext();


        password = sharedPreferences.getString("password", "default");

//        Log.d("Password", password);

        if (password.equals("default")) {
//            Log.d("PasswordMessage", "Default password");
            message = "Hasło nie istnieje!";
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
            //System.exit(0);

        } else {
//            Log.d("PasswordMessage", "Password OK");
            message = "Hasło istnieje";
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
        }


    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        finish();
//    }

    public void chceckPassword(View view) {
        String tempPassword = textField.getText().toString();
        if (password.equals(tempPassword)) {
            Intent intent = new Intent(this, MessageActivity.class);
            startActivity(intent);
            System.exit(0);
        }

    }
}

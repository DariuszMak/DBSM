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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    EditText textField;
    String message = "";
    String password = "";
    MD5 md5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        md5 = new MD5();

        textField = (EditText) findViewById(R.id.editTextPassword);

        sharedPreferences = getSharedPreferences("com.example.dariusz.testapp", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
//        editor.clear();
//        editor.commit();

        context = getApplicationContext();


        password = sharedPreferences.getString("password", "default");

//        Log.d("Password", password);

        if (password.equals("default")) {
//            Log.d("PasswordMessage", "Default password");
            message = "Hasło nie istnieje!";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
            //System.exit(0);

        } else {
//            Log.d("PasswordMessage", "Password OK");
            message = "Hasło istnieje";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

//        Log.d("Hash", md5.createHash("Hasło"));

    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    public void chceckPassword(View view) {
        String tempPassword = textField.getText().toString();
        if (password.equals(md5.createHash(tempPassword))) {
            Toast.makeText(context, "Hasło poprawne!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MessageActivity.class);
            startActivity(intent);
            System.exit(0);
        } else {
            Toast.makeText(context, "Hasło niepoprawne!\nSpróbuj ponownie", Toast.LENGTH_SHORT).show();
        }

    }


}



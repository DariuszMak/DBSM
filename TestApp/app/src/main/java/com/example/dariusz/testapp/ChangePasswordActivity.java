package com.example.dariusz.testapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    EditText textField;
    EditText textField2;
    MD5 md5;
    AES aes;
    String password = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        md5 = new MD5();
        aes = new AES();

        textField = (EditText) findViewById(R.id.editTextChangePassword);
        textField2 = (EditText) findViewById(R.id.editTextChangePassword2);

        sharedPreferences = getSharedPreferences("com.example.dariusz.testapp", Context.MODE_PRIVATE);
        password = sharedPreferences.getString("password", "default");
        editor = sharedPreferences.edit();

        context = getApplicationContext();
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    public void changePassword(View view) {
        Log.d("method called", "changerPassword");
        String tempNewPassword = textField.getText().toString().trim();
        String tempNewPassword2 = textField2.getText().toString().trim();

        if (tempNewPassword.equals(tempNewPassword2)) {

            if (isValidPassword(tempNewPassword) && !password.equals(md5.createHash(tempNewPassword))) {
                Toast.makeText(context, "Password OK!", Toast.LENGTH_SHORT).show();
                String key = sharedPreferences.getString("password", "default");
                String rightMessage = aes.decrypt(sharedPreferences.getString("message", ""), key);
                String passHash = md5.createHash(tempNewPassword);
                String encryptedMessage = aes.encrypt(rightMessage, passHash);
                editor.putString("password", passHash);

                editor.putString("message", encryptedMessage);

                editor.commit();

                Toast.makeText(context, "Password changed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MessageActivity.class);
                startActivity(intent);
                //System.exit(0);
            } else {
                Toast.makeText(context, "Invalid password!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Check password!", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{10,}$";

//        ^       # start-of-string
//                (?=.*[0-9])       # a digit must occur at least once
//                (?=.*[a-z])       # a lower case letter must occur at least once
//                (?=.*[A-Z])       # an upper case letter must occur at least once
//                (?=.*[@#$%^&+=])  # a special character must occur at least once you can replace with your special characters
//                (?=\\S+$)          # no whitespace allowed in the entire string
//                .{2,}             # anything, at least two places though
//        $       # end-of-string

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}

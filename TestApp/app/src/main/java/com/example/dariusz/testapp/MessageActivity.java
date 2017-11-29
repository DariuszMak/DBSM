package com.example.dariusz.testapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MessageActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    EditText textField;
    TextView textView;
    String password = "";
    String rightMessage = "";
    AES aes;
    String key = "";
    String salt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Intent I = getIntent();
        password = I.getStringExtra("Password");
        aes = new AES();

        textField = (EditText) findViewById(R.id.editTextRightMessage);
        textView = (TextView) findViewById(R.id.textViewRightMessage);

        sharedPreferences = getSharedPreferences("com.example.dariusz.testapp", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        context = getApplicationContext();

        key = sharedPreferences.getString("password", "default");

        salt = sharedPreferences.getString("salt", "default");

        rightMessage = aes.decrypt(sharedPreferences.getString("message", ""), password, salt);
        textView.setText(rightMessage);
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    public void changePassword(View view) {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        intent.putExtra("Password", password);
        startActivity(intent);
        System.exit(0);
    }

    public void enterNewMessage(View view) {
        String messageTemp = aes.encrypt(textField.getText().toString(), password, salt);

        editor.putString("message", messageTemp);
        editor.commit();
        rightMessage = aes.decrypt(sharedPreferences.getString("message", ""), password, salt);
        textView.setText(rightMessage);

        Toast.makeText(context, "Wiadomość zapisana", Toast.LENGTH_SHORT).show();

    }
}

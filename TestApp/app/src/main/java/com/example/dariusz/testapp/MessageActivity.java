package com.example.dariusz.testapp;

import android.content.Context;
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
    Toast toast;
    String rightMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        textField = (EditText) findViewById(R.id.editTextRightMessage);
        textView = (TextView) findViewById(R.id.textViewRightMessage);

        sharedPreferences = getSharedPreferences("com.example.dariusz.testapp", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        context = getApplicationContext();


        rightMessage = sharedPreferences.getString("message", "");
        textView.setText(rightMessage);
    }

    public void enterNewMessage(View view) {

        String messageTemp = textField.getText().toString();

        editor.putString("message", messageTemp);
        editor.commit();
        rightMessage = sharedPreferences.getString("message", "");
        textView.setText(rightMessage);

    }
}

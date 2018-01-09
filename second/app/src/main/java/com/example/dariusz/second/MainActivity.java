package com.example.dariusz.second;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    TextView contacts;

    HashMap<String, List<String>> allContacts;


    private IntentFilter filter =
            new IntentFilter("android.intent.action.MAIN");
    //Ustawiliśmy sobie filtr na reakcje na nadchodzącego SMS'a

    private BroadcastReceiver broadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {

            if (intent != null) {
                allContacts = (HashMap<String, List<String>>) intent.getSerializableExtra("contacts");

                contacts.setText(TextUtils.concat("Lista kontaktów: ", System.getProperty("line.separator")));

                if (allContacts != null) {
                    Set keys = allContacts.keySet();

                    for (Object key1 : keys) {
                        String key = (String) key1;
                        List<String> value = allContacts.get(key);
                        contacts.append(key + " : ");
                        for (int i = 0; i < value.size(); i++) {
                            contacts.append(value.get(i) + ", ");
                        }
                        contacts.append(System.getProperty("line.separator"));
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(), "nie przesłano kontaktów", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcast, filter);
    }

    /*@Override
    public void onPause() {
        unregisterReceiver(broadcast);
        super.onPause();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contacts = (TextView) findViewById(R.id.contacts);


    }

}

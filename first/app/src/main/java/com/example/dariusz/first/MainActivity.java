package com.example.dariusz.first;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button viewContacts;

    TextView contacts;

    static final int PERMISSIONS = 0;

    HashMap<String, List<String>> allContacts = new HashMap<>();

    private boolean shouldAskPermission(){
        return(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    private void askPermissions() {
        String[] permissions = {
                "android.permission.READ_CONTACTS",
        };
        requestPermissions(permissions, PERMISSIONS);
    }


    private void getPackages() {

        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getPackageManager();

        // get a list of installed apps.
        packages = pm.getInstalledApplications(0);

        for (ApplicationInfo packageInfo : packages) {

            String applicationName = (String) (packageInfo != null ? pm
                    .getApplicationLabel(packageInfo) : "(unknown)");
            Log.d("applicationName=" + applicationName, "package name="
                    + packageInfo.packageName);
        }
    }

    private void displayContacts() {

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor phoneCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (phoneCur != null) {
                        List<String> tempNumbers = new ArrayList<>();
                        while (phoneCur.moveToNext()) {
                            String phoneNumber = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            tempNumbers.add(phoneNumber);
                        }
                        allContacts.put(name, tempNumbers);
                        phoneCur.close();
                    }
                }
            }
            cur.close();
        }

        contacts.setText(TextUtils.concat("Lista kontaktów: ", System.getProperty("line.separator")));

        Set keys = allContacts.keySet();

        for (Object key1 : keys) {
            String key = (String) key1;
            List<String> value = allContacts.get(key);
            contacts.append(key + " : ");
            for (int i=0; i< value.size(); i++){
                contacts.append(value.get(i) + ", ");
            }
            contacts.append(System.getProperty("line.separator"));
        }

        getPackages();

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.example.dariusz.second");
        if (launchIntent != null) {
            launchIntent.putExtra("contacts", allContacts);
            startActivity(launchIntent);
        } else {
            Toast.makeText(getApplicationContext(), "nie uruchomiono", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewContacts = (Button)findViewById(R.id.viewContacts);
        contacts = (TextView)findViewById(R.id.contacts);

        if (shouldAskPermission()){
            askPermissions();
        }

        viewContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayContacts();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case PERMISSIONS:
                boolean readContactsAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (readContactsAccepted){
                    viewContacts.setEnabled(true);
                }
                break;
        }
    }
}
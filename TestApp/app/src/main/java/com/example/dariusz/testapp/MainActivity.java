package com.example.dariusz.testapp;

import android.Manifest;
import android.app.ActionBar;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.security.cert.CertificateException;


public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    EditText textField;
    String message = "";
    String password = "";
    MD5 md5;
    FingerprintManager fingerprintManager;
    KeyguardManager keyguardManager;
    KeyStore keyStore;
    KeyGenerator keyGenerator;
    static final String KEY_NAME = "example_key";
    Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;
    boolean passwordExists;

    FingerprintHandler helper = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passwordExists = false;

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
            passwordExists = false;
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
            System.exit(0);

        } else {
//            Log.d("PasswordMessage", "Password OK");
            message = "Hasło istnieje";
            passwordExists = true;
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

//        Log.d("Hash", md5.createHash("Hasło"));

        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (!keyguardManager.isKeyguardSecure()) {

            Toast.makeText(this,
                    "Lock screen security not enabled in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,
                    "Fingerprint authentication permission not enabled",
                    Toast.LENGTH_LONG).show();

            return;
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {

            // This happens when no fingerprints are registered.
            Toast.makeText(this,
                    "Register at least one fingerprint in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        } else {
            generateKey();

            if (cipherInit()) {
                cryptoObject = new FingerprintManager.CryptoObject(cipher);
                helper = new FingerprintHandler(this);
                helper.startAuth(fingerprintManager, cryptoObject);
            }

        }

    }

    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            throw new RuntimeException(
                    "Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyGenerator.init(new
                        KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
            }
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | IOException e) {
            throw new RuntimeException(e);
        } catch (java.security.cert.CertificateException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        } catch (java.security.cert.CertificateException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPassword(View view) {
        String tempPassword = textField.getText().toString();
        if ((password.equals(md5.createHash(tempPassword)) || helper.isOk) && passwordExists) {
            Toast.makeText(context, "Hasło poprawne!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MessageActivity.class);
            intent.putExtra("Password", password);
            startActivity(intent);
            System.exit(0);
        } else {
            Toast.makeText(context, "Hasło niepoprawne!\nSpróbuj ponownie", Toast.LENGTH_SHORT).show();
        }

    }


}
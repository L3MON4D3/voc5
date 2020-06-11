package com.L3MON4D3.voc5;

import okhttp3.OkHttpClient;
import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.Response;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.content.SharedPreferences;
import android.content.Context;

import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.view.View;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private Voc5Client vClient;
    private OkHttpClient okClient = new OkHttpClient();

    private EditText emailET;
    private EditText pwdET;
    private CheckBox rememberBox;

    private Callback loginCallback = new Callback() {
        public void onFailure(Call call, IOException e) {
            Log.e("voc5:", "not successful:"+e.getMessage());
        }

        public void onResponse(Call call, Response res) throws IOException {
            Log.e("voc5:", res.body().string());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        emailET = findViewById(R.id.usr_et);
        pwdET = findViewById(R.id.pwd_et);
        rememberBox = findViewById(R.id.rmbr_cbx);

        loadPreferences();

        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logIn();
            }
        });
    }

    /**
     * Attempts to log in User using Credentials from EditTexts usr_et, pwd_et.
     */
    public void logIn() {
        vClient = new Voc5Client(
            emailET.getText().toString(),
            pwdET.getText().toString() );

        okClient.newCall(vClient.loginRqst()).enqueue(loginCallback);
        saveLogin();
    }

    /**
     * Load Preferences from Disk.
     */
    public void loadPreferences() {
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        boolean checked = prefs.getBoolean("rememberBox", false);
        rememberBox.setChecked(checked);
        if (checked) {
            //if values are not found, use empty String.
            emailET.setText(prefs.getString("email", ""));
            pwdET.setText(prefs.getString("password", ""));
        }
    }

    /**
     * Save Login-data to Disk if "Remember Me"-Button is checked.
     */
    public void saveLogin() {
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        boolean checked = rememberBox.isChecked();
        if (checked) {
            ed.putString("email", emailET.getText().toString());
            ed.putString("password", pwdET.getText().toString());
        } else {
            ed.remove("email");
            ed.remove("password");
        }
        ed.apply();
    }

    /**
     * Write Status of "Remember Me"-Button in Preferences.
     */
    public void saveRememberStatus() {
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        boolean checked = rememberBox.isChecked();
        ed.putBoolean("rememberBox", checked);
        ed.apply();
    }

    /**
     * Save whether "Remember Me"-Button is checked.
     */
    public void onStop() {
        saveRememberStatus();
        super.onStop();
    }
}

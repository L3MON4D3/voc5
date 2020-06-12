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
import android.widget.Toast;
import android.view.View;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private Voc5Client vClient;
    private OkHttpClient okClient = new OkHttpClient();

    private EditText emailET;
    private EditText pwdET;
    private EditText serverET;
    private CheckBox rememberBox;

    private Toast registerSuccessToast;
    private Toast registerFailToast;

    private Callback loginCallback = new Callback() {
        public void onFailure(Call call, IOException e) {
            Log.e("voc5:", "not successful:"+e.getMessage());
        }

        public void onResponse(Call call, Response res) throws IOException {
            Log.e("voc5:", res.body().string());
        }
    };

    private Callback registerCallback = new Callback() {
        public void onFailure(Call call, IOException e) {
            Log.e("voc5:", "not successful:"+e.getMessage());
        }

        public void onResponse(Call call, Response res) throws IOException {
            if (!res.isSuccessful())
                registerFailToast.show();
            else
                registerSuccessToast.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        emailET = findViewById(R.id.usr_et);
        pwdET = findViewById(R.id.pwd_et);
        serverET = findViewById(R.id.server_et);
        rememberBox = findViewById(R.id.rmbr_cbx);

        registerSuccessToast = Toast.makeText(this, "Register Successful", Toast.LENGTH_SHORT);
        registerFailToast = Toast.makeText(this, "Couldn't register!", Toast.LENGTH_LONG);

        loadPreferences();

        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logIn(false);
            }
        });

        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logIn(true);
            }
        });
    }

    /**
     * As LogIn and Register do essentially the same thing, this method does both
     * and functionality can be switched using bool register.
     * @param register if true, attempts to register user, if false logs in user.
     */
    public void logIn(boolean register) {
        vClient = new Voc5Client(
            serverET.getText().toString(),
            emailET.getText().toString(),
            pwdET.getText().toString() );

        if (!register) {
            okClient.newCall(vClient.loginRqst()).enqueue(loginCallback);
        } else {
            okClient.newCall(vClient.registerRqst()).enqueue(registerCallback);
        }
        saveLogin();
        openMainMenu();
    }

    /**
     * Open Main Menu once user is logged in. CURRENTLY DOES NOTHING!!
     */
    public void openMainMenu() {
         
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
            serverET.setText(prefs.getString("server", "https://api.voc5.org"));
        }
    }

    /**
     * Save Login-data and Server to Disk if "Remember Me"-Button is checked.
     */
    public void saveLogin() {
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        boolean checked = rememberBox.isChecked();
        if (checked) {
            ed.putString("email", emailET.getText().toString());
            ed.putString("password", pwdET.getText().toString());
            ed.putString("server", serverET.getText().toString());
        } else {
            ed.remove("email");
            ed.remove("password");
            ed.remove("server");
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
    protected void onStop() {
        saveRememberStatus();
        super.onStop();
    }
}

package com.L3MON4D3.voc5.UI;

import okhttp3.OkHttpClient;
import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.Response;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.content.SharedPreferences;
import android.content.Context;


import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;


import com.L3MON4D3.voc5.Client.Voc5Client;
import com.L3MON4D3.voc5.R;

import java.io.IOException;

public class LoginActivity extends LoadingInfoActivity {
    private Voc5Client vClient;
    private OkHttpClient okClient;

    private EditText emailET;
    private EditText pwdET;
    private EditText serverET;
    private CheckBox rememberBox;

    private Toast registerSuccessToast;
    private Toast registerFailToast;

    private Callback loginCallback = new Callback() {
        public void onFailure(Call call, IOException e) {
            runOnUiThread(() -> stopLoading());
            Log.e("voc5:", e.getMessage());
        }

        public void onResponse(Call call, Response res) throws IOException {
            runOnUiThread(() -> stopLoading());
            if (res.body().string().equals("Logged in"))
                openMainMenu();
        }
    };

    private Callback registerCallback = new Callback() {
        public void onFailure(Call call, IOException e) {
            runOnUiThread(() -> stopLoading());
            Log.e("voc5:", "not successful:"+e.getMessage());
        }

        public void onResponse(Call call, Response res) throws IOException {
            runOnUiThread(() -> stopLoading());
            if (!res.isSuccessful())
                registerFailToast.show();
            else
                registerSuccessToast.show();
            openMainMenu();
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

        setLoadingInfoParentLayout(findViewById(R.id.loginOuter_lt));

        pwdET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if((keyCode == keyEvent.KEYCODE_ENTER) && keyEvent.getAction() == keyEvent.ACTION_DOWN){
                    logIn(false);
                    return true;
                }

                return false;
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

        okClient = vClient.getOkClient();

        String loadInfoText = "";
        if (!register) {
            okClient.newCall(vClient.loginRqst()).enqueue(loginCallback);
            loadInfoText = "Logging In";
        } else {
            okClient.newCall(vClient.registerRqst()).enqueue(registerCallback);
            loadInfoText = "Registering";
        }
        startLoading();
        saveLogin();
    }

    /**
     * Open Main Menu once user is logged in. CURRENTLY DOES NOTHING!!
     */
    public void openMainMenu() {
        Intent menuIntent = new Intent(getApplicationContext(), GalleryActivity.class);
        menuIntent.putExtra("client", vClient);
        startActivity(menuIntent);
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

    @Override
    protected void changeLayout() {
        findViewById(R.id.login_btn).setEnabled(false);
        findViewById(R.id.register_btn).setEnabled(false);
    }

    @Override
    protected void undoChangeLayout() {
        findViewById(R.id.login_btn).setEnabled(true);
        findViewById(R.id.register_btn).setEnabled(true);
    }
}

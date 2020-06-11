package com.L3MON4D3.voc5;

import okhttp3.OkHttpClient;
import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.Response;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;
import android.view.View;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private Voc5Client vClient;
    private OkHttpClient okClient = new OkHttpClient();

    private EditText emailET;
    private EditText pwdET;

    private View.OnClickListener loginListener = new View.OnClickListener() {
        public void onClick(View v) {
            vClient = new Voc5Client(
                emailET.getText().toString(), pwdET.getText().toString());
            okClient.newCall(vClient.loginRqst()).enqueue(loginCallback);
        }
    };

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

        findViewById(R.id.login_btn).setOnClickListener(loginListener);
    }
}

package UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.lemonade.voc5.R;

public class MainMenuActivity extends AppCompatActivity {
    TextView intro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        if(getIntent().hasExtra("com.L3MON4D3.voc5.USER")){
            intro.findViewById(R.id.introText);
            intro.setText(getIntent().getExtras().getString("com.L3MON4D3.voc5.USER"));
        }
    }
}
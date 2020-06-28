package com.L3MON4D3.voc5.UI;

import android.os.Bundle;
import android.util.DisplayMetrics;
import com.L3MON4D3.voc5.R;
public class Pop extends VocActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lern_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int heigth = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8), (int)(heigth*0.6));
    }
}

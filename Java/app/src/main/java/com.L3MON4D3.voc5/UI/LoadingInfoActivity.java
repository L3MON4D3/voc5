package com.L3MON4D3.voc5.UI;

import com.L3MON4D3.voc5.R;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;
import android.view.ViewGroup;

public class LoadingInfoActivity extends AppCompatActivity {
    private ConstraintLayout loadingInfoParentLayout;
    private CardView loadingInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Sets loadingInfoParentLayout to layout cl, creates new loadingInfo.
     * @param cl ConstrainLayout.
     * @param text Text that should be displayed once Loading starts.
     */
    protected void setLoadingInfoParentLayout(ConstraintLayout cl) {
        loadingInfoParentLayout = cl;
        loadingInfo = (CardView) getLayoutInflater().inflate(R.layout.progress_info, cl, false);
    }

    /**
     * Sets text that is shown when loadingView is displayed.
     * @param text String.
     */
    protected void setLoadingInfoText(String text) {
        ((TextView) ((ViewGroup) loadingInfo.getChildAt(0)).getChildAt(0)).setText(text);
    }

    /**
     * Display Loading animation. MAKE SURE TO CALL setLoadingInfoParentLayout BEFORHAND.
     */
    protected void startLoading() {
        loadingInfoParentLayout.addView(loadingInfo);
    }

    /**
     * Remove Loading animation. MAKE SURE TO CALL setLoadingInfoParentLayout BEFORHAND.
     */
    protected void stopLoading() {
        loadingInfoParentLayout.removeView(loadingInfo);
    }

    /**
     * Called in startLoading, override to make changes to Layout eg disable Buttons.
     */
    protected void changeLayout() { }
}

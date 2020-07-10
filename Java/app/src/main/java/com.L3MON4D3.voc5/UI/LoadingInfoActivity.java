package com.L3MON4D3.voc5.UI;

import com.L3MON4D3.voc5.R;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;
import android.view.ViewGroup;

import android.widget.Toast;

public class LoadingInfoActivity extends AppCompatActivity {
    private ConstraintLayout loadingInfoParentLayout;
    private CardView loadingInfo;
    private boolean isLoading = false;

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
        if (!isLoading) {
            loadingInfoParentLayout.addView(loadingInfo);
            isLoading = true;
            changeLayout();
        }
    }

    /**
     * Remove Loading animation. MAKE SURE TO CALL setLoadingInfoParentLayout BEFORHAND.
     */
    protected void stopLoading() {
        if (isLoading) {
            loadingInfoParentLayout.removeView(loadingInfo);
            isLoading = false;
            undoChangeLayout();
        }
    }

    /**
     * Called in startLoading, override to make changes to Layout eg disable Buttons.
     */
    protected void changeLayout() { }

    /**
     * Called in stopLoading, override to revert changes to Layout eg enabling Buttons.
     */
    protected void undoChangeLayout() { }

    /**
     * Briefly shows a Toast that inform the user about a connection failure.
     */
    protected void showErrorToast() {
        stopLoading();
        Toast.makeText(this, "Couldn't connect to Server", Toast.LENGTH_LONG).show();
    }
}

package ca.cheuksblog.twopicgallery;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

public class MainFragment extends Fragment {
    private ImageView ivc;
    private SeekBar scaleBar;

    private Uri passImg;
    private Uri idImg;
    private boolean isViewingPass = true;

    public MainFragment() {
        super(R.layout.fragment_main);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivc = getView().findViewById(R.id.ivMain);
        scaleBar = getView().findViewById(R.id.scaleBar);

        update();

        ivc.setOnClickListener(v -> {
            isViewingPass = !isViewingPass;
            setIVC();
        });
        scaleBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final float scale = progress / 100.0f + 0.5f;
                ivc.setScaleX(scale);
                ivc.setScaleY(scale);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveScale();
            }
        });
    }

    private void loadScale() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        final int progress = prefs.getInt(isViewingPass ?
                AppPreferenceScreen.PASS_SCALE_KEY :
                AppPreferenceScreen.ID_SCALE_KEY, 49);
        final float scale = progress / 100.0f + 0.5f;
        ivc.setScaleX(scale);
        ivc.setScaleY(scale);
        scaleBar.setProgress(progress);
    }

    private void saveScale() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (isViewingPass) {
            prefs.edit().putInt(AppPreferenceScreen.PASS_SCALE_KEY, scaleBar.getProgress()).apply();
        } else {
            prefs.edit().putInt(AppPreferenceScreen.ID_SCALE_KEY, scaleBar.getProgress()).apply();
        }
    }

    private void setIVC() {
        if (isViewingPass) {
            ivc.setRotation(AppPreferenceScreen.getPassRotation(this));
            ivc.setImageURI(passImg);
        } else {
            ivc.setRotation(AppPreferenceScreen.getIdRotation(this));
            ivc.setImageURI(idImg);
        }
        loadScale();
    }

    public void onSetPassImage() {
        try {
            passImg = AppPreferenceScreen.getPassImage(this);
            setIVC();
        } catch (NullPointerException e) {
            Log.v("onSetPassImage", "Couldn't get pass image");
        }
    }

    public void onSetIdImage() {
        try {
            idImg = AppPreferenceScreen.getIdImage(this);
            setIVC();
        } catch (NullPointerException e) {
            Log.v("onSetIdImage", "Couldn't get id image");
        }
    }

    public void update() {
        onSetPassImage();
        onSetIdImage();
    }
}

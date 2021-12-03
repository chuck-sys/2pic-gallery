package ca.cheuksblog.vaccinepassport;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {
    private ImageView ivc;
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

        update();

        ivc.setOnClickListener(v -> {
            isViewingPass = !isViewingPass;
            setIVC();
        });
    }

    private void setIVC() {
        if (isViewingPass) {
            ivc.setRotation(AppPreferenceScreen.getPassRotation(this));
            ivc.setImageURI(passImg);
        } else {
            ivc.setRotation(AppPreferenceScreen.getIdRotation(this));
            ivc.setImageURI(idImg);
        }
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

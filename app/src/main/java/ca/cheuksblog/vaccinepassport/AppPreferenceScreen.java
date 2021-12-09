package ca.cheuksblog.vaccinepassport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class AppPreferenceScreen extends PreferenceFragmentCompat {
    private Preference passImage;
    private Preference idImage;
    private Preference aboutApp;
    private Preference howToUse;
    private ActivityResultLauncher<String[]> getPassImage;
    private ActivityResultLauncher<String[]> getIdImage;
    MainFragment main;
    public static final int PICK_PASS_IMG = 1;
    public static final int PICK_ID_IMG = 2;
    public static final String PASS_KEY = "settings_pass_img";
    public static final String PASS_ROT_KEY = "settings_pass_rot";
    public static final String ID_KEY = "settings_id_img";
    public static final String ID_ROT_KEY = "settings_id_rot";
    public static final String PASS_SCALE_KEY = "settings_pass_scale";
    public static final String ID_SCALE_KEY = "settings_id_scale";
    public static final String[] LAUNCH_FILTER = new String[] {
            "image/*"
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        passImage = findPreference("settings_pass_img");
        idImage = findPreference("settings_id_img");
        aboutApp = findPreference("settings_about_btn");
        howToUse = findPreference("settings_instructions_btn");

        passImage.setOnPreferenceClickListener(preference -> {
            getPassImage.launch(LAUNCH_FILTER);
            return true;
        });
        idImage.setOnPreferenceClickListener(preference -> {
            getIdImage.launch(LAUNCH_FILTER);
            return true;
        });
        aboutApp.setOnPreferenceClickListener(preference -> {
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.app_about)
                    .setTitle(R.string.settings_about)
                    .create()
                    .show();
            return true;
        });
        howToUse.setOnPreferenceClickListener(preference -> {
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.app_help)
                    .setTitle(R.string.settings_help)
                    .create()
                    .show();
            return true;
        });

        getPassImage = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> handleIntentReturn(PICK_PASS_IMG, uri)
        );
        getIdImage = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> handleIntentReturn(PICK_ID_IMG, uri)
        );
    }

    public void handleIntentReturn(int requestCode, Uri selectedImage) {
        if (selectedImage == null) {
            // When you cancel it
            return;
        }

        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();

        // Revoke previous permissions on file to stop app having too many files with permissions
        if (requestCode == PICK_PASS_IMG) {
            try {
                getActivity().revokeUriPermission(getPassImage(this), Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (NullPointerException ignored) {}
            prefs.edit().putString(PASS_KEY, selectedImage.toString()).apply();
        } else {
            try {
                getActivity().revokeUriPermission(getIdImage(this), Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (NullPointerException ignored) {}
            prefs.edit().putString(ID_KEY, selectedImage.toString()).apply();
        }

        // Give new permissions to file here to prevent it from being removed (if it is the same image as earlier)
        getActivity().grantUriPermission(getActivity().getPackageName(), selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        getActivity().getContentResolver().takePersistableUriPermission(selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        main.update();
    }

    public static Uri getPassImage(final Fragment f) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(f.getContext());
        return Uri.parse(prefs.getString(PASS_KEY, null));
    }

    public static Uri getIdImage(final Fragment f) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(f.getContext());
        return Uri.parse(prefs.getString(ID_KEY, null));
    }

    public static int getPassRotation(final Fragment f) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(f.getContext());
        return prefs.getInt(PASS_ROT_KEY, 0);
    }

    public static int getIdRotation(final Fragment f) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(f.getContext());
        return prefs.getInt(ID_ROT_KEY, 0);
    }
}

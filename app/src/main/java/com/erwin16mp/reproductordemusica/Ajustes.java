package com.erwin16mp.reproductordemusica;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class Ajustes extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TEMA = "Tema";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).registerOnSharedPreferenceChangeListener(this);
        setTema();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(TEMA)){
            this.recreate();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        Preference preference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.ajustes_xml, rootKey);
            preference = findPreference("OCULTAR_AUD");
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.Para, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    private void setTema() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        switch (preferences.getString(TEMA, "Rojo")) {
            case "Rojo":
                setTheme(R.style.Rojo);
                break;
            case "Morado":
                setTheme(R.style.Morado);
                break;
            case "Azul":
                break;
            case "Verde":
                break;
            case "Amarillo":
                break;
            case "Naranja":
                break;
            case "Café":
                break;
        }
    }
}
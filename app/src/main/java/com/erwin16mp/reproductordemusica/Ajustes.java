package com.erwin16mp.reproductordemusica;

import android.content.Intent;
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
        startActivity(new Intent(this, Index.class));
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(this, Index.class));
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
            /*preference.setOnPreferenceClickListener(preference -> {
                Toast.makeText(getActivity().getApplicationContext(), R.string.Para, Toast.LENGTH_SHORT).show();
                return true;
            });*/
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
                setTheme(R.style.Azul);
                break;
            case "Verde":
                setTheme(R.style.Verde);
                break;
            case "Amarillo":
                setTheme(R.style.Amarillo);
                break;
            case "Naranja":
                setTheme(R.style.Naranja);
                break;
            case "Café":
                setTheme(R.style.Cafe);
                break;
        }
    }
}
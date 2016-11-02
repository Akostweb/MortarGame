package com.gmail.akostweb.mortargame;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    public static  final String TIME = "pref_amountOfTime";
    public static final String BLOCKS = "pref_amountOfBlocks";

    EditTextPreference text11, text22;

    public boolean preferenceChanged = true;

    public int text1;
    public int text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text11 = (EditTextPreference) findViewById(R.id.);




        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);



        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(setContentView() , new PlaceholderFragment())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editText1:
                Toast.makeText(getApplicationContext(), "sohranenno1", Toast.LENGTH_SHORT).show();
            case R.id.editeText2:
                Toast.makeText(getApplicationContext(), "sohranenno2", Toast.LENGTH_SHORT).show();
            default:
                return false;
        }
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_settings, container, false);
            return rootView;
        }

    }


}

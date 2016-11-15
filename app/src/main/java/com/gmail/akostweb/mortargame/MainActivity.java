package com.gmail.akostweb.mortargame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;



public class MainActivity extends Activity {

    public static  final String TIME = "pref_amountOfTime";
    public static final String BLOCKS = "pref_amountOfBlocks";

    public boolean preferenceChanged = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferenceChangeListener);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);
        return super.onOptionsItemSelected(item);
    }


    protected void restartApp(){
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);


    }






    @Override
    protected void onStart() {
        super.onStart();

//        if (preferenceChanged){
//            MortarGameFragment mortarGameFragment = (MortarGameFragment) getFragmentManager().
//                    findFragmentById(R.id.mortarView);
//            mortarGameFragment.updateTime(PreferenceManager.getDefaultSharedPreferences(this));
//            preferenceChanged = false;
//            restartApp();
//
//        }
    }


    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    preferenceChanged = true;
                    MortarGameFragment mortarGameFragment = (MortarGameFragment) getFragmentManager().
                    findFragmentById(R.id.mortarView);
                    if(key.equals(TIME)) {
                        mortarGameFragment.updateTime(sharedPreferences);
                        restartApp();


                    }

                }
            };





}

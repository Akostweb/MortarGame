package com.gmail.akostweb.mortargame;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Администратор on 19.10.2016.
 */

public class SettingsFragment extends  PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}

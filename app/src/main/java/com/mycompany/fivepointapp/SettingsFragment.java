package com.mycompany.fivepointapp;
import android.preference.*;
import android.os.*;

public class SettingsFragment extends PreferenceFragment
{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}

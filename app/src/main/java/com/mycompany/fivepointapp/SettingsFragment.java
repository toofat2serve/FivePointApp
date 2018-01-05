package com.mycompany.fivepointapp;
import android.preference.*;
import android.os.*;
import android.content.*;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
	
   @Override
   public void onResume() {
	  super.onResume();
	  getPreferenceScreen().getSharedPreferences()
		 .registerOnSharedPreferenceChangeListener(this);
   }

   @Override
   public void onPause() {
	  super.onPause();
	  getPreferenceScreen().getSharedPreferences()
		 .unregisterOnSharedPreferenceChangeListener(this);
   }
   
   @Override
   public void onSharedPreferenceChanged(SharedPreferences sp, String key)
   {
	  Preference pref = findPreference(key);
	  if (key == "pref_resolution") {
		 String str = "Number of significant digits for data fields.\nMax:5\nCurrently set to hrhjirf ";
		 str += ((ListPreference) pref).getEntry().toString();
		 pref.setSummary(str);
	  }
   }
}

package com.romaka.fivepointapp;
import android.preference.*;
import android.os.*;
import android.content.*;
import android.util.*;
import java.util.*;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{

   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	   SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
	   Map<String,?> spMap =  sp.getAll();
	   

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
	   updateResSummary(sp, spMap);
		
      }
	 
	  public void updateResSummary(SharedPreferences sp, Map<String,?> spMap) {
		 Preference pref = findPreference("pref_resolution");
		 String str = "Number of significant digits for data fields.\nMax:5\tCurrently: ";
		 String strRes = (String) spMap.get("pref_resolution");
		 str += strRes;
		 pref.setSummary(str);
	  }
	  
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key)
	{
	   //Log.i("ME","Preference Changed\n" + key);
	   Map<String,?> spMap =  sp.getAll();
	   updateResSummary(sp,spMap);
	   
    }
	
   @Override
   public void onResume() {
	  //Log.i("ME","Resuming Settings");
	  getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	  super.onResume();
   }

   @Override
   public void onPause() {
	  //Log.i("ME","Pausing Settings");
	  getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	  super.onPause();
   }
   
   
}

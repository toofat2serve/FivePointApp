package com.mycompany.fivepointapp;
import android.app.*;
import android.os.*;
import android.content.*;

public class SettingsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
			.replace(android.R.id.content, new SettingsFragment())
			.commit();
			
	   SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
		  SharedPreferences.OnSharedPreferenceChangeListener() {
		  @Override
		  public void onSharedPreferenceChanged(SharedPreferences sp,
												String key) {
			 
												   
												   // your stuff here
			 if (key == "pref_paste") {
				String strJson = sp.getString("pref_paste","");
				SharedPreferences.Editor spe = sp.edit();
				spe.putString("dd",strJson);
				spe.commit();
				spe.remove("pref_paste");
				spe.commit();
			 }
			 
		  }
	   };
	}
	

    protected boolean isValidFragment(String fragmentName)
    {
        return SettingsFragment.class.getName().equals(fragmentName);
    }
}

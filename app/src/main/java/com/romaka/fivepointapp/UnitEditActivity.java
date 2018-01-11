package com.romaka.fivepointapp;
import android.app.*;
import android.view.*;
import android.os.*;
import android.widget.*;
import android.content.*;
import android.preference.*;
import java.util.*;
import android.util.*;

public class UnitEditActivity extends Activity
{
ListView lv_unit_edit;
String[] units_values;
Set<String> uvset;
   @Override
   public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState)
   {
	  Log.i("ME","Starting unit edit activity");
	  super.onCreate(savedInstanceState, persistentState);
	  setContentView(R.layout.unit_dia_frag);
	  
	  SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(this);
	  Map<String,?> dspMap = dsp.getAll();
	  Set<String> uvset = (Set<String>) dspMap.get("unit_values");
	  units_values = uvset.toArray(new String[uvset.size()]);
	  lv_unit_edit.setAdapter(new UnitAdapter(units_values));
	  
   }
   
   void updateUnits(int index) {
	  
   }
   
   
   
   public class UnitAdapter extends BaseAdapter {
	  String[] arr;
	  UnitAdapter(String[] strs) {
		 arr = strs;
	  }

	  @Override
	  public View getView(int pos, View cV, ViewGroup container)
	  {
		 
		 LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		 if (cV == null) {
			cV = _inflater.inflate(R.layout.unit_edit, null);
		 }

		 TextView t_unit = (TextView) cV.findViewById(R.id.unit_label);
		 Button btn_del = (Button) cV.findViewById(R.id.btn_delete);
		 t_unit.setText(units_values[pos]);

		 return cV;
	  }
	  
	  
	  @Override
	  public int getCount()
	  {
		 // TODO: Implement this method
		 return arr.length;
	  }

	  @Override
	  public Object getItem(int p1)
	  {
		 // TODO: Implement this method
		 return arr[p1];
	  }

	  @Override
	  public long getItemId(int p1)
	  {
		 // TODO: Implement this method
		 return p1;
	  }

	  
   }
}

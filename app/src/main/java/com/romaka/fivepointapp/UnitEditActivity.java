package com.romaka.fivepointapp;
import android.app.*;
import android.view.*;
import android.os.*;
import android.widget.*;
import android.content.*;
import android.preference.*;
import java.util.*;
import android.util.*;
import android.view.inputmethod.*;

public class UnitEditActivity extends Activity 
{
   ListView lv_unit_edit;
   Button btn_add_unit;
   EditText e_add_unit;
   public ArrayList<String> units_values;
   Set<String> uvset;
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
	  //Log.i("ME", "Starting unit edit activity");
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.unit_dia_frag);
	  lv_unit_edit = (ListView) findViewById(R.id.lv_unit_edit);
	  btn_add_unit = (Button) findViewById(R.id.btn_add_unit);
	  e_add_unit = (EditText) findViewById(R.id.e_add_unit);
	  Intent intentExtras = getIntent();
	  String[] uv = intentExtras.getStringArrayExtra("units");
	  units_values = new ArrayList<String>(uv.length);
	  Collections.addAll(units_values, uv);
	  refreshAdapter(units_values);
	  
	  e_add_unit.setOnEditorActionListener(new EditText.OnEditorActionListener(){

			@Override
			public boolean onEditorAction(TextView view, int actionID, KeyEvent keyEvent)
			{
			   if (actionID == EditorInfo.IME_ACTION_DONE) {
				  addUnit(view);
			   }
			   return false;
			}
		 });
   }

   void verifyChange() {
	  SharedPreferences shpr = PreferenceManager.getDefaultSharedPreferences(this);
	  Map<String,?> dspMap = shpr.getAll();
	  Set<String> uvset = (Set<String>) dspMap.get("units");
	  ArrayList<String> unvals = new ArrayList<String>(uvset.size());
	  Log.i("ME","Verifying: " +  unvals.toString());
   }
   
   void setSharedPrefUnits(ArrayList<String> uv)
   {
	  SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
	  SharedPreferences.Editor spe = sp.edit();
	  Set<String> uvset = new HashSet<String>(Arrays.asList(uv));
	  spe.putStringSet("units", uvset);
	  spe.commit();
	  verifyChange();
   }

   void refreshAdapter(ArrayList<String> strs)
   {
	  ListAdapter adapter = new UnitAdapter(strs);
	  lv_unit_edit.setAdapter(adapter);
   }


   public void removeUnit(int index)
   {
	  units_values.remove(index);
	  setSharedPrefUnits(units_values);
	  refreshAdapter(units_values);
   }

   public void addUnit(View view)
   {

	  String str = e_add_unit.getText().toString();
	  units_values.add(str);
	  setSharedPrefUnits(units_values);
	  refreshAdapter(units_values);
	  e_add_unit.setText("");
   }

   public class UnitAdapter extends BaseAdapter {
	  ArrayList<String> arr;
	  UnitAdapter(ArrayList<String> strs)
	  {
		 arr = strs;
	  }

	  @Override
	  public View getView(final int pos, View cV, ViewGroup container)
	  {
		 LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		 if (cV == null) {
			cV = _inflater.inflate(R.layout.unit_edit, null);
		 }

		 TextView t_unit = (TextView) cV.findViewById(R.id.unit_label);
		 ImageButton btn_del = (ImageButton) cV.findViewById(R.id.btn_delete);
		 t_unit.setText(arr.get(pos));

		 btn_del.setOnClickListener(new View.OnClickListener() {

			   @Override
			   public void onClick(View view)
			   {
				  removeUnit(pos);
				  notifyDataSetChanged();
			   }
			});
		 return cV;
	  }

	  @Override
	  public int getCount()
	  {
		 // TODO: Implement this method
		 return arr.size();
	  }

	  @Override
	  public Object getItem(int p1)
	  {
		 // TODO: Implement this method
		 return arr.get(p1);
	  }

	  @Override
	  public long getItemId(int p1)
	  {
		 // TODO: Implement this method
		 return p1;
	  }
   }
}

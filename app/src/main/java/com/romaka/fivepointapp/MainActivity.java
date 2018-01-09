package com.romaka.fivepointapp;
//DONE: FIX progressbar/save behavior (cause: changing to multi sets)
//DONE: CNG CalRecord CalData to ArrayList
//DONE: ADD as found/as left functionality
//      L__ morphed to make multiple sets with custom titles
//DONE: CNG move calibration to new activity, rather than peekaboo
//DONE: FIX save default

//TODO: ADD file browser / import
//TODO: ADD other formats (CSV, HTML, PDF, ...)

//TODO: ADD database storage
//TODO: ADD db retrieval
//TODO: ADD text field auto-complete from db history

//TODO: ADD ABOUT Acvivity

//TODO: ADD comment/notes feature

//TODO: ADD content to help avtivity

//TODO: ADD units to table header

//TODO: FIX main layout: reduce reliance on layered layouts
//TODO: FIX changing resolution should take immediate effect
//TODO: FIX assess public objects for privitization


import android.app.*;
import android.content.*;
import android.graphics.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import java.io.*;
import java.util.*;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.getExternalStoragePublicDirectory;
import com.google.gson.*;

public class MainActivity extends Activity {
   public EditText e_id;
   public EditText e_serial;
   public EditText e_make;
   public EditText e_model;
   public EditText e_dlrv;
   public EditText e_durv;
   public EditText e_clrv;
   public EditText e_curv;
   public EditText e_steps;
   public EditText e_notes;
   public RadioGroup rg_linsq;
   public RadioButton rb_lin;
   public RadioButton rb_squ;
   public Button btn_save;
   public Button btn_reset;
   public Button btn_clr;
   public Spinner spin_dunit;
   public Spinner spin_cunit;
   public LinearLayout ll;
   public ArrayList<String> units_values;
   public ArrayList<EditText> mandos;
   public CalRecord cr;
   public static final String DFLT_DEVICE = "dd";
   public Boolean CLEAR_TEXT_ON_TOUCH;
   public Integer DATA_RESOLUTION;
   public Boolean DONE_FLAG;
   public Boolean RETURN_FROM_SETTINGS;
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
	  //Log.i("ME", "Initializing...");
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.main);        
	  e_id = (EditText) findViewById(R.id.e_id);
	  e_serial = (EditText) findViewById(R.id.e_serial);
	  e_make = (EditText) findViewById(R.id.e_make);
	  e_model = (EditText) findViewById(R.id.e_model);
	  e_dlrv = (EditText) findViewById(R.id.e_dlrv);
	  e_durv = (EditText) findViewById(R.id.e_durv);
	  e_clrv = (EditText) findViewById(R.id.e_clrv);
	  e_curv = (EditText) findViewById(R.id.e_curv);
	  e_steps = (EditText) findViewById(R.id.e_steps);
	  //e_notes = (EditText) findViewById(R.id.e_notes);
	  rg_linsq = (RadioGroup) findViewById(R.id.rg_linsq);
	  btn_save = (Button) findViewById(R.id.btn_save);
	  btn_reset = (Button) findViewById(R.id.btn_reset);
	  btn_clr = (Button) findViewById(R.id.btn_clr);

	  spin_dunit = (Spinner) findViewById(R.id.spin_dunit);
	  spin_cunit = (Spinner) findViewById(R.id.spin_cunit);
	  rb_lin = (RadioButton) findViewById(R.id.rb_lin);
	  rb_squ = (RadioButton) findViewById(R.id.rb_squ);
	  ll = (LinearLayout) findViewById(R.id.lh_row_1);
	  mandos = new ArrayList<EditText>();
	  mandos.add(e_dlrv);
	  mandos.add(e_durv);
	  mandos.add(e_clrv);
	  mandos.add(e_curv);
	  mandos.add(e_steps);
	  units_values = new ArrayList<String>();
	  units_values.add("PSI");
	  units_values.add("\"H2O");
	  units_values.add("°F");
	  units_values.add("mA");
	  units_values.add("RPM");
	  units_values.add("Volts");
	  units_values.add("Amps");
	  units_values.add("Ohms");
	  units_values.add("GPM");
	  DONE_FLAG = false;
	  cr = new CalRecord();
	  spin_dunit.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, units_values));
	  spin_cunit.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, units_values));
	  spin_dunit.setSelection(0);
	  spin_cunit.setSelection(3);
	  ////Log.i("ME", "...Initialized.");
	  checkSharedPreferences();     
	  btn_reset.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view)
			{
			   CollectForm();
			   updateDefaultDevice(cr);
			   return true;
			}
		 });
   }

   @Override
   protected void onStart()
   {
	  checkSharedPreferences();
	  super.onStart();
   }

   @Override
   protected void onResume()
   {
	  checkSharedPreferences();
	  super.onResume();
   }

   public void updateDefaultDevice(CalRecord cr)
   {
	  SharedPreferences sp = getPreferences(0);
	  SharedPreferences.Editor spe = sp.edit();
	  spe.putString(DFLT_DEVICE, toGson(cr.Device));
	  spe.commit();
	  MyUtilities.myToast("New Default Device Saved" + cr.toString(), this);
   }


   public void checkSharedPreferences()
   {
	  SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(this);
	  Map<String,?> dspMap = dsp.getAll();
	  if (dspMap.containsKey("pref_preload")) {
		 Boolean loadDefaultDevice = (Boolean) dspMap.get("pref_preload");
		 if (loadDefaultDevice) {
			resetForm();
		 }
	  }
	  CLEAR_TEXT_ON_TOUCH = (Boolean) dspMap.get("pref_clear_read");
	  DATA_RESOLUTION = (String) dspMap.get("pref_resolution") == null ? 3 : Integer.parseInt((String) dspMap.get("pref_resolution"));  
   }

   public String str(Double d)
   {
	  return String.format("%." + DATA_RESOLUTION.toString() + "f", d);
   }

   public String str(int i)
   {
	  return String.valueOf(i);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.mainmenu, menu);
	  return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
	  switch (item.getItemId()) {
		 case R.id.settings:
			clickSettings();
			return true;
		 case R.id.share:
			clickShare();
			return true;
		 case R.id.help:
			clickHelp();
			return true;
		 case R.id.about:
			clickAbout();
			return true;
		 default:
			return super.onOptionsItemSelected(item);
	  }
   }

   public void clickSettings()
   {
	  Intent intent = new Intent();
	  intent.setClassName(this, "com.romaka.fivepointapp.SettingsActivity");
	  startActivity(intent);
	  checkSharedPreferences();
   }

   public void composeEmail()
   {
	  SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(this);
	  Map<String,?> dspMap = dsp.getAll();
	  String defaultEmail = (String) dspMap.get("pref_email");
	  String strSubject = "Calibration Results for " + cr.Device.ID + " on " + cr.date.toString();
	  String strBody = cr.toString() + "\n";
	  strBody += "JSON String\n";
	  strBody += cr.makeJson(); 
	  Intent intent = new Intent(Intent.ACTION_SENDTO);
	  intent.setData(Uri.parse("mailto:")); // only email apps should handle this
	  intent.putExtra(Intent.EXTRA_EMAIL, new String[] {defaultEmail});
	  intent.putExtra(Intent.EXTRA_SUBJECT, strSubject);
	  intent.putExtra(Intent.EXTRA_TEXT, strBody);
	  if (intent.resolveActivity(getPackageManager()) != null) {
		 startActivity(intent);
	  }
   }

   public void clickShare()
   {
	  composeEmail();
   }

   public void clickHelp()
   {
      Intent intent = new Intent();
      intent.setClassName(this, "com.romaka.fivepointapp.HelpActivity");
      startActivity(intent);
   }

   public void clickAbout()
   {

   }

   public void resetForm()
   {
	  ////Log.i("ME", "Starting reset...");
	  ClearForm((ViewGroup) findViewById(R.id.lv_rows));
	  CalRecord backupDevice = new CalRecord();
	  SharedPreferences sp = getPreferences(0);
	  String strJSON = sp.getString(DFLT_DEVICE, backupDevice.makeJson());
	  ////Log.i("ME", strJSON);
	  CalRecord cr =new CalRecord(strJSON);
	  FillForm(cr.Device);
	  ////Log.i("ME", cr.Device.toString() + "\n...Reset Complete.");
   }

   public void clickReset(View view)
   {
	  resetForm();
   }

   public void clickClear(View view)
   {
	  //Log.i("ME", "Clearing...");
	  ClearForm((ViewGroup) findViewById(R.id.lv_rows));
	  //Log.i("ME", "...Cleared.");
   }


   public void clickCal(View view)
   {
	  if (FormFilledRight(mandos)) {
		 CollectForm();
		 Intent intent = new Intent();
		 intent.setClassName(this, "com.romaka.fivepointapp.CalActivity");
		 intent.putExtra("device", toGson(cr.Device));
		 intent.putExtra("resolution", DATA_RESOLUTION);
		 intent.putExtra("clear", CLEAR_TEXT_ON_TOUCH);
		 startActivity(intent);
	  }
   }

   public String toGson(Object o)
   {
	  Gson gson = new Gson();
	  return gson.toJson(o);
   }

   public void ClearEditText(View view) 
   {  
	  if (view instanceof EditText) {
		 EditText et = (EditText) view;
		 et.setText("");
		 et.setBackgroundColor(Color.RED);
		 et.requestFocus();
		 et.selectAll();
	  }
   }


   public void FillForm(Instrument inst)
   {
	  e_id.setText(inst.ID);
	  e_serial.setText(inst.Serial);
	  e_make.setText(inst.Make);
	  e_model.setText(inst.Model);
	  e_dlrv.setText(inst.DLRV.toString());
	  e_durv.setText(inst.DURV.toString());
	  e_clrv.setText(inst.CLRV.toString());
	  e_curv.setText(inst.CURV.toString());
	  e_steps.setText(inst.Steps.toString());
	  rb_lin.setChecked(true);
	  spin_dunit.setSelection(0);
	  spin_cunit.setSelection(3);
   }

   public Boolean FormFilledRight(ArrayList<EditText> mfs)
   {
	  ////Log.i("ME", "Starting Form Validation...");
	  Boolean gonogo = true;
	  Iterator it = mfs.iterator();
	  while (it.hasNext()) {
		 EditText e = (EditText) it.next();
		 if (e.getText().length() == 0) {
			e.setBackgroundColor(Color.RED);
		 }
		 else {
			e.setBackgroundColor(android.R.color.transparent);
		 }
		 gonogo = (e.getText().length() == 0) ? false : gonogo;
	  }
	  ////Log.i("ME", "...Form Validation Complete.");
	  return gonogo;
   }

   public void CollectForm()
   {
	  ////Log.i("ME", "Starting Collection...");
	  String id = e_id.getText().toString();
	  String se = e_serial.getText().toString();
	  String ma = e_make.getText().toString();
	  String mo = e_model.getText().toString();
	  Double dl = Double.parseDouble(e_dlrv.getText().toString());
	  Double du = Double.parseDouble(e_durv.getText().toString());
	  String dun = spin_dunit.getSelectedItem().toString();
	  String cun = spin_cunit.getSelectedItem().toString();
	  Double cl = Double.parseDouble(e_clrv.getText().toString());
	  Double cu = Double.parseDouble(e_curv.getText().toString());
	  Integer st = Integer.parseInt(e_steps.getText().toString());
	  Boolean il = rb_lin.isChecked();
	  //String no = e_notes.getText().toString();
	  cr = new CalRecord(new Instrument(id, se, ma, mo, dl, du, dun, cl, cu, cun, st, il));
	  // cr.Notes = no;
   }

// Got this procedure from
// https://stackoverflow.com/questions/5740708/android-clearing-all-edittext-fields-with-clear-button
   public void ClearForm(ViewGroup group)
   {

	  for (int i = 0, count = group.getChildCount(); i < count; ++i) {
		 View view = group.getChildAt(i);
		 if (view instanceof EditText) {
			((EditText) view).setText("");
			view.setBackgroundColor(android.R.color.transparent);
		 }
		 if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0)) {
			ClearForm((ViewGroup) view);
		 }
	  }
   }
   
}
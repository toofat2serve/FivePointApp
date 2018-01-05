package com.mycompany.fivepointapp;
//DONE: ADD HELP Activity

//TODO: ADD changing resolution changes summary to include new value STARTED

//TODO: ADD JSON string paste import SCRAP

//TODO: ADD file browser / import
//TODO: ADD ABOUT Acvivity
//TODO: ADD comment/notes feature
//TODO: ADD other formats (CSV, HTML, PDF, ...)
//TODO: ADD content to help avtivity

//TODO: FIX changing resolution should take immediate effect
//TODO: FIX read field to focus on next one down with IME action



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
   public Button btn_edit;
   public Button btn_reset;
   public Button btn_clr;
   public Spinner spin_dunit;
   public Spinner spin_cunit;
   public ViewSwitcher vs_save_edit;
   public ViewSwitcher vs_save_data;
   public ProgressBar pb_calibration;
   public Adapter lv_ds_adapter;
   public ListView lv_dataset;
   public LinearLayout ll;
   public LinearLayout ll_pb;
   public LinearLayout ll_sd;
   public ArrayList<String> units_values;
   public ArrayList<EditText> mandos;
   public Boolean focusLocked;
   public Boolean doneFlag;
   public CalRecord cr;

   public static final String DFLT_DEVICE = "dd";
   public Boolean CLEAR_TEXT_ON_TOUCH;
   public Integer DATA_RESOLUTION;
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
	  Log.i("ME", "Initializing...");
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
	  vs_save_edit = (ViewSwitcher) findViewById(R.id.vs_save_edit);
	  pb_calibration = (ProgressBar) findViewById(R.id.pb_calibration);
	  lv_dataset = (ListView) findViewById(R.id.listview_dataset);
	  spin_dunit = (Spinner) findViewById(R.id.spin_dunit);
	  spin_cunit = (Spinner) findViewById(R.id.spin_cunit);
	  rb_lin = (RadioButton) findViewById(R.id.rb_lin);
	  rb_squ = (RadioButton) findViewById(R.id.rb_squ);
	  ll = (LinearLayout) findViewById(R.id.lh_row_1);
	  ll_pb = (LinearLayout) findViewById(R.id.lv_progress);
	  ll_sd = (LinearLayout) findViewById(R.id.lh_save_data);
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
	  focusLocked = false;
	  doneFlag = false;
	  cr = new CalRecord();
	  spin_dunit.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, units_values));
	  spin_cunit.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, units_values));
	  spin_dunit.setSelection(0);
	  spin_cunit.setSelection(3);
	  Log.i("ME", "...Initialized.");
	  checkSharedPreferences();     

	  btn_reset.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view)
			{
			   cr = CollectForm();
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

   public void updateDefaultDevice(CalRecord cr)
   {
	  SharedPreferences sp = getPreferences(0);
	  SharedPreferences.Editor spe = sp.edit();
	  spe.putString(DFLT_DEVICE, cr.makeJson());
	  spe.commit();
	  MyUtilities.myToast("New Default Device Saved" + cr.toString(), this);

   }

   public void checkSharedPreferences()
   {
	  SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(this);
	  Map<String,?> dspMap = dsp.getAll();
	  SharedPreferences.Editor dspe = dsp.edit();
	  if (dspMap.containsKey("pref_preload")) {
		 Boolean loadDefaultDevice = (Boolean) dspMap.get("pref_preload");
		 if (loadDefaultDevice) {
			resetForm();
		 }
	  }
	  CLEAR_TEXT_ON_TOUCH = (Boolean) dspMap.get("pref_clear_read");
	  DATA_RESOLUTION = (String) dspMap.get("pref_resolution") == null ? 3 : Integer.parseInt((String) dspMap.get("pref_resolution"));


	  SharedPreferences sp = getPreferences(0);
	  Map<String,?> spmap = sp.getAll();
	  Log.i("ME", "Shared Preferences" + spmap.toString());

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
	  intent.setClassName(this, "com.mycompany.fivepointapp.SettingsActivity");
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
      intent.setClassName(this, "com.mycompany.fivepointapp.HelpActivity");
      startActivity(intent);
   }

   public void clickAbout()
   {

   }



   public void resetForm()
   {
	  Log.i("ME", "Starting reset...");
	  ClearForm((ViewGroup) findViewById(R.id.lv_rows));
	  CalRecord backupDevice = new CalRecord();
	  SharedPreferences sp = getPreferences(0);
	  String strJSON = sp.getString(DFLT_DEVICE, backupDevice.makeJson());
	  Log.i("ME", strJSON);
	  CalRecord cr =new CalRecord(strJSON);
	  FillForm(cr.Device);
	  Log.i("ME", cr.Device.toString() + "\n...Reset Complete.");

   }

   public void clickReset(View view)
   {
	  resetForm();
   }

   public void clickClear(View view)
   {
	  Log.i("ME", "Clearing...");
	  ClearForm((ViewGroup) findViewById(R.id.lv_rows));
	  Log.i("ME", "...Cleared.");
   }

   public void clickCal(View view)
   {
	  Log.i("ME", "Starting Save...");
	  if (FormFilledRight(mandos)) {
		 ArrayList al;
		 cr = CollectForm();
		 al = cr.CalData;
		 lv_dataset.setAdapter(new DataSetViewAdapter(cr.CalData));
		 FocusControl(true);
		 vs_save_edit.showNext();
		 peekaboo(true);
	  }
	  Log.i("ME", "... Save Complete.");
   }

   public void clickEdit(View view)
   {
	  Log.i("ME", "Starting Edit...");
	  if (FormFilledRight(mandos)) {
		 FocusControl(false);
		 vs_save_edit.showNext();
	  }
	  if (!cr.CalData.isEmpty()) {
		 cr.CalData.clear();
		 lv_dataset.setAdapter(new DataSetViewAdapter(cr.CalData));
		 peekaboo(false);
	  }
	  Log.i("ME", "...Edit Complete.");
   }

   public void clickSave(View view)
   {
	  try {
		 cr.Notes = e_notes.getText().toString();
		 String state = Environment.getExternalStorageState();
		 if (Environment.MEDIA_MOUNTED.equals(state)) {
			Context context = getApplicationContext();
			File file = new File(context.getExternalFilesDir(null), cr.makeFileName());
			FileOutputStream fileOutput = new FileOutputStream(file);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutput);
			outputStreamWriter.write(cr.makeJson());
			outputStreamWriter.flush();
			fileOutput.getFD().sync();
			outputStreamWriter.close();			
			MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null, null);		
		 }
		 else {
			Log.i("ME", "Media Not Mounted");
		 }
	  }
	  catch (Exception e) {
		 Log.i("ME", "Why? ", e);
	  }
   }
   public void peekaboo(Boolean collapse)
   {	
	  ArrayList<View> views = new ArrayList<View>();
	  views.add((View) findViewById(R.id.lh_row_1));
	  views.add((View) findViewById(R.id.lh_row_2));
	  views.add((View) findViewById(R.id.lh_row_3));
	  views.add((View) findViewById(R.id.lh_row_4));
	  for (Integer i = 0; i < 4; i++) {
		 if (collapse) {		
			views.get(i).setVisibility(View.GONE);
			//findViewById(R.id.e_notes).setVisibility(View.VISIBLE);
		 }
		 else {
			views.get(i).setVisibility(View.VISIBLE);
			//findViewById(R.id.e_notes).setVisibility(View.GONE);
		 }
	  }
   };

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

   public void FocusControl(Boolean lock)
   {
	  HashSet<EditText> formfields = new HashSet<EditText>();
	  formfields.add(e_id);
	  formfields.add(e_serial);
	  formfields.add(e_make);
	  formfields.add(e_model);
	  formfields.add(e_dlrv);
	  formfields.add(e_durv);
	  formfields.add(e_clrv);
	  formfields.add(e_curv);
	  formfields.add(e_steps);
	  Iterator it1 = formfields.iterator();
	  while (it1.hasNext()) {
		 EditText e = (EditText) it1.next();
		 e.setFocusable(!lock);
		 e.setFocusableInTouchMode(!lock);
	  }
	  HashSet<View> views = new HashSet<View>();
	  views.add(rb_lin);
	  views.add(rb_squ);
	  views.add(spin_dunit);
	  views.add(spin_cunit);
	  Iterator it2 = views.iterator();
	  while (it2.hasNext()) {
		 View v = (View) it2.next();
		 v.setClickable(!lock);
		 v.setFocusable(!lock);
		 v.setFocusableInTouchMode(!lock);
		 v.setEnabled(!lock);
	  }
	  focusLocked = lock;
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
	  Log.i("ME", "Starting Form Validation...");
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
	  Log.i("ME", "...Form Validation Complete.");
	  return gonogo;
   }

   public CalRecord CollectForm()
   {
	  Log.i("ME", "Starting Collection...");
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
	  CalRecord cr = new CalRecord(new Instrument(id, se, ma, mo, dl, du, dun, cl, cu, cun, st, il));
	  // cr.Notes = no;
	  Log.i("ME", cr.Device.toString());
	  Log.i("ME", "... Collection Complete.");
	  return cr.createTestData(DATA_RESOLUTION);
   }

// Got this procedure from
// https://stackoverflow.com/questions/5740708/android-clearing-all-edittext-fields-with-clear-button
   public void ClearForm(ViewGroup group)
   {
	  if (!focusLocked) {
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

   public void enterRead(View view, ArrayList<HashMap<String, String>> data, int position, Boolean ime)
   {
	  if (!(data.isEmpty())) {
		 EditText et = (EditText) view;
		 String s = et.getText().toString();
		 HashMap hm = new HashMap();
		 hm = data.get(position);
		 if (!(s.isEmpty())) {
			Double readValue = Double.parseDouble(s);
			String expStr = (String) hm.get("Expected");
			Double expValue = Double.parseDouble(expStr);
			Double devValue = (Math.abs(expValue - readValue) / cr.Device.CRange) * 100;
			hm.put("Dev", String.format("%."    + DATA_RESOLUTION.toString() +  "f", devValue));
		 }
		 hm.put("Read", s);
		 data.set(position, hm);
		 cr.CalData = data;
		 // lv_dataset.invalidate();
		 pb_calibration.setProgress(cr.getProgress());
		 if ((cr.getProgress() == 100) && (!doneFlag) && (ime)) {
			ll_pb.setVisibility(View.GONE);
			ll_sd.setVisibility(View.VISIBLE);
			view.clearFocus();
			doneFlag = true;
		 }
		 if (cr.getProgress() < 100) {
			ll_pb.setVisibility(View.VISIBLE);
			ll_sd.setVisibility(View.GONE);
			doneFlag = false;
		 }

	  }
	 // Log.i("ME", "Data Update: \n" + cr.toString(true));
   }
//Adapter
   public class DataSetViewAdapter extends BaseAdapter {
	  ArrayList<HashMap<String, String>> data;
	  public DataSetViewAdapter(ArrayList<HashMap<String, String>> arr)
	  {
		 data = arr;
	  }
	  @Override public int getCount()
	  {return data.size();}
	  @Override public HashMap<String, String> getItem(int _i)
	  {return data.get(_i);}
	  @Override public long getItemId(int _i)
	  { return _i;}

	  @Override
	  public View getView(final int position, View convertView, ViewGroup container)
	  {
		 LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		 if (convertView == null) {
			convertView = _inflater.inflate(R.layout.datalist, null);
		 }
		 TextView c1 = (TextView) convertView.findViewById(R.id.t_c1_input);
		 TextView c2 = (TextView) convertView.findViewById(R.id.t_c2_exp);
		 EditText c3 = (EditText) convertView.findViewById(R.id.e_c3_read);
		 TextView c4 = (TextView) convertView.findViewById(R.id.t_c4_dev);
		 c1.setText(data.get(position).get("Input"));
		 c2.setText(data.get(position).get("Expected"));
		 c3.setText(data.get(position).get("Read"));
		 c4.setText(data.get(position).get("Dev"));

		 c3.setOnClickListener(new View.OnClickListener() {
			   @Override
			   public void onClick(View view)
			   {
				  EditText et = (EditText) view;
//				  myToast((et.getText().toString()));
//				  et.setText("");			  
//				  notifyDataSetChanged();
				  et.requestFocus();
				  et.selectAll();
			   }			
			});

		 c3.setOnFocusChangeListener(new EditText.OnFocusChangeListener() 
			{
			   @Override
			   public void onFocusChange(View view, boolean hasFocus)
			   {
				  if ((view instanceof EditText) && (hasFocus)) {
					 EditText et = (EditText) view;
					 if (CLEAR_TEXT_ON_TOUCH) {
					    data.get(position).put("Read", "");
						notifyDataSetChanged();
					 }

					 notifyDataSetChanged();
				  }
				  if ((view instanceof EditText) && (!hasFocus)) {

					 enterRead(view, data, position, false);
					 notifyDataSetChanged();
				  }
			   }
			});

		 c3.setOnEditorActionListener(new EditText.OnEditorActionListener() 
			{
			   @Override
			   public boolean onEditorAction(TextView view, int actionID, KeyEvent event)
			   {
				  if (actionID == EditorInfo.IME_ACTION_DONE) {
					 if (view instanceof EditText) {
						enterRead(view, data, position, true);
						view.clearFocus();
						/*Integer nextPosition =  position < data.size() - 1 ? position + 1 : 0;
						Log.i("ME","This position: " + position + "  Next Position: " + nextPosition + "Size: " + data.size());
						View rws = lv_dataset.getAdapter().getView(nextPosition, lv_dataset.getChildAt(nextPosition), lv_dataset);
						ViewGroup rows = (ViewGroup) rws;
						Log.i("ME", "nextRow " + rows.toString());
						ViewGroup nextRow = (ViewGroup) rows.getChildAt(nextPosition);
						Integer viewsInRow = nextRow.getChildCount();
						Log.i("ME","viewsInRow = " + viewsInRow);
						for (Integer i = 0; i < viewsInRow; i++) {
						   View child = nextRow.getChildAt(i);
						   Log.i("ME", "child " + child.toString());
						   if (child instanceof EditText) {
							  EditText et = (EditText) child;
						      if (et.getText().length() == 0) {
							     child.requestFocus();
							  }
						   }
						}*/
						//Log.i("ME", lv_dataset.getFocusables(View.FOCUS_DOWN).toString());
					 }
				  }

				  return false;
			   }
			});
		 return convertView;
	  }
   }
}

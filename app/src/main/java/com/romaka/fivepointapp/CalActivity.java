package com.romaka.fivepointapp;
import android.app.*;
import android.os.*;
import android.view.*;
import android.content.*;
import java.util.*;
import com.google.gson.*;
import android.util.*;
import android.widget.*;
import android.view.inputmethod.*;
import java.io.*;
import android.media.*;

public class CalActivity extends Activity {
   public EditText e_label;
   public Button btn_prev, btn_next, btn_edit, btn_save;
   public ProgressBar pb_calibration;
   public int IDX = 0;
   public int SIZ = 1;
   public int DATA_RESOLUTION;
   public Boolean CLEAR_TEXT_ON_TOUCH, DONE_FLAG;
   public TextView t_pb_cal;
   public ListAdapter adapter;
   public ListView lv_dataset;
   public ArrayList<CalRecord.DataRow> AL;
   public String LABEL;
   public Boolean PAGED;
   CalRecord cr;
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.cal);
	  e_label = (EditText) findViewById(R.id.e_label);
	  btn_edit = (Button) findViewById(R.id.btn_edit);
	  btn_prev = (Button) findViewById(R.id.btn_prev);
	  btn_next = (Button) findViewById(R.id.btn_next);
	  btn_save = (Button) findViewById(R.id.btn_save);
	  pb_calibration = (ProgressBar) findViewById(R.id.pb_calibration);
	  lv_dataset = (ListView) findViewById(R.id.lv_dataset);
	  t_pb_cal = (TextView) findViewById(R.id.t_pb_cal);
	  Intent intentExtras = getIntent();
	  String jsonString = intentExtras.getStringExtra("device");
	  DATA_RESOLUTION = intentExtras.getIntExtra("resolution", 3);
	  CLEAR_TEXT_ON_TOUCH = intentExtras.getBooleanExtra("CLEAR", true);
	  DONE_FLAG = false;
	  Instrument device = extractCalData(jsonString);
	  cr = new CalRecord(device);
	  cr.createTestData(0);
	  LABEL = cr.CalData.get(0).Name;
	  e_label.setText(LABEL);
	  p_n_btns(null);
	  PAGED = false;
	  }
	  
   public class CalDataSetViewAdapter extends BaseAdapter {
      ArrayList<CalRecord.DataRow> data;  
	  CalDataSetViewAdapter(ArrayList<CalRecord.DataRow> al) {data = al;} 
	  @Override public int getCount(){return data.size();}
	  @Override public Object getItem(int position){return data.get(position);}
      @Override public long getItemId(int position){return position;}
      @Override public View getView(final int position, View convertView, final ViewGroup container)
	  {
		 LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 if (convertView == null) {
			convertView = _inflater.inflate(R.layout.datalist, null);
		 }
		 TextView c1 = (TextView) convertView.findViewById(R.id.t_c1_input);
		 TextView c2 = (TextView) convertView.findViewById(R.id.t_c2_exp);
		 EditText c3 = (EditText) convertView.findViewById(R.id.e_c3_read);
		 TextView c4 = (TextView) convertView.findViewById(R.id.t_c4_dev);
		 CalRecord.DataRow dr = cr.CalData.get(IDX).Data.get(position);
		 c1.setText(str(dr.Input));
		 c2.setText(str(dr.Expected));
		 c3.setText(dr.isNull(dr.Read) ? "" : str(dr.Read));
		 int nextPosition = position == data.size() - 1 ? 0 : position + 1;
		 if (data.get(nextPosition).Read != null) {
			c3.setImeOptions(EditorInfo.IME_ACTION_DONE);
		 }
		 else {
			c3.setImeOptions(EditorInfo.IME_ACTION_NEXT);
	     }
		 c4.setText(str(dr.Dev));
		 c3.setTag(position);
		 c3.setOnClickListener(new View.OnClickListener() {
			   @Override
			   public void onClick(View view)
			   {
				  data_event(view, container, position, false, false, false, false, false);
			   }  
			});

		 c3.setOnFocusChangeListener(new EditText.OnFocusChangeListener() 
			{
			   @Override
			   public void onFocusChange(View view, boolean hasFocus)
			   {
				  data_event(view, container, position, true, hasFocus, false, false, false);
			   }
			});

		 c3.setOnEditorActionListener(new EditText.OnEditorActionListener() 
			{
			   @Override
			   public boolean onEditorAction(TextView view, int actionID, KeyEvent event)
			   {
				  Boolean stopProp = false;
				  if (actionID == EditorInfo.IME_ACTION_NEXT) {
					 data_event(view, container, position, false, false, true, true, false);
				  }
				  if (actionID == EditorInfo.IME_ACTION_DONE) {
					 if (view instanceof EditText) {
						data_event(view, container, position, false, false, true, false, true);
						stopProp = true;
					 }
				  }
				  return stopProp;
			   }
			});
		 return convertView;
	  }
	  
	  private void data_event(View view, ViewGroup vg, int position, Boolean focusChange, Boolean hasFocus, Boolean imeEvent, Boolean imeNext, Boolean imeDone)
	  {
		 if (!(data.isEmpty()) && !(PAGED)) {	
			if (view instanceof EditText) {
			   EditText et = (EditText) view;
			   if ((!imeEvent) && (focusChange) && (hasFocus)) { // on click
			      if (CLEAR_TEXT_ON_TOUCH) {
					 cr.CalData.get(IDX).Data.get(position).Read = null;
				  }
			   }
			   else if ((imeEvent) || ((focusChange) && (!hasFocus))) {
				  String s = et.getText().toString();
				  CalRecord.DataRow dr = cr.CalData.get(IDX).Data.get(position);
				  if (!(s.isEmpty())) {
					 Double readValue = Double.parseDouble(s);
					 cr.CalData.get(IDX).Data.get(position).Read = readValue;
					 String expStr = str(dr.Expected);
					 Double expValue = Double.parseDouble(expStr);
					 Double devValue = (Math.abs(expValue - readValue) / cr.Device.CRange) * 100;
					 cr.CalData.get(IDX).Data.get(position).Dev = devValue;
				  }
				  if ((cr.countReadNulls(IDX) == 0) || (imeDone)) {
					 btn_save.requestFocus();
				  }
			   }   
			}
		 }
		 notifyDataSetChanged();
		 pb_calibration.setProgress(cr.getProgress(IDX));
		 if ((cr.getProgress(IDX) == 100) && (!DONE_FLAG) && (imeEvent)) {
			t_pb_cal.setVisibility(View.GONE);
			pb_calibration.setVisibility(View.GONE);
			btn_save.setVisibility(View.VISIBLE);
			DONE_FLAG = true;
		 }
		 if (cr.getProgress(IDX) < 100) {
			t_pb_cal.setVisibility(View.VISIBLE);
			pb_calibration.setVisibility(View.VISIBLE);
			btn_save.setVisibility(View.GONE);
			DONE_FLAG = false;
		 }
	  }
   }
	  
   public void clickSave(View view)
   {
	  try {
		 //cr.Notes = e_notes.getText().toString();
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
			////Log.i("ME", "Media Not Mounted");
		 }
	  }
	  catch (Exception e) {
		 ////Log.i("ME", "Why? ", e);
	  }
   }
	  
   
	  
   public String str(Double d)
   {
	  return String.format("%." +  String.valueOf(DATA_RESOLUTION) + "f", d);
   }

   public String str(int i)
   {
	  return String.valueOf(i);
   }
	  
	  private Instrument extractCalData(String json)
   {
	  Gson gson = new Gson();
	  return gson.fromJson(json, Instrument.class);
   }

   public void clickEdit(View view)
   {
	  //TODO: popup warning that cal daya will be cleared ok/cancel
	  cr.CalData.clear();
	  finish();
   }

   private void refreshAdapter() {
	  
	  
	  AL = cr.CalData.get(IDX).Data;
	  LABEL = cr.CalData.get(IDX).Name;
	  e_label.setText(LABEL);
      adapter = new CalDataSetViewAdapter(AL);
	 
	  lv_dataset.setAdapter(adapter);
	  PAGED = false;
	  }
   
   public void p_n_btns(View view)
   {
	  cr.CalData.get(IDX).Name = e_label.getText().toString();
	  if (!(view == null)) {
		 if (view.getId() == btn_next.getId()) {
            if (IDX == SIZ - 1) {
			   IDX ++;	   
			   cr.newDataSet("NAME" + String.valueOf(IDX), IDX);
			   cr.createTestData(IDX);
			   
			}
			else {
			   IDX ++;
			}
		 }
		 else
		 if (view.getId() == btn_prev.getId()) {
			IDX--;
		 }
		 PAGED = true;
	  }
	  SIZ = cr.CalData.size();
	  if (IDX == 0) {
		 btn_prev.setEnabled(false);
	  }
	  else {
		 btn_prev.setEnabled(true);
	  }
	  if (IDX == SIZ - 1) {
		 btn_next.setText("\u002B");
	  }
	  else {
		 btn_next.setText("→");
	  }
	  refreshAdapter();
   }
}

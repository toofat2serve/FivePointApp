package com.romaka.fivepointapp;
import android.app.*;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.*;
import android.content.*;
import java.util.*;
import com.google.gson.*;
import android.util.*;
import android.widget.*;
import android.view.inputmethod.*;
import java.io.*;
import android.media.*;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.*;

public class CalActivity extends FragmentActivity 
implements DataLossDialogFragment.DataLossDialogListener
{
    private EditText e_label;
    private Button btn_prev;
    private Button btn_next;
    private Button btn_save;
    private ProgressBar pb_calibration;
    private int IDX = 0;
    private int SIZ = 1;
    private int DATA_RESOLUTION;
    private Boolean CLEAR_TEXT_ON_TOUCH, DONE_FLAG;
    private TextView t_pb_cal;
    private ListView lv_dataset;
    private String LABEL;
    private Boolean PAGED;
    private Instrument device;
    private CalRecord cr;
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.cal);
       e_label = findViewById(R.id.e_label);
       Button btn_edit = findViewById(R.id.btn_edit);
       btn_prev = findViewById(R.id.btn_prev);
       btn_next = findViewById(R.id.btn_next);
       btn_save = findViewById(R.id.btn_save);
       TextView t_inpt = findViewById(R.id.t_tbl_c1_head_inpt);
       TextView t_expt = findViewById(R.id.t_tbl_c2_head_expt);
       TextView t_read = findViewById(R.id.t_tbl_c3_head_read);
       TextView t_devn = findViewById(R.id.t_tbl_c4_head_devn);
       pb_calibration = findViewById(R.id.pb_calibration);
       lv_dataset = findViewById(R.id.lv_dataset);
       t_pb_cal = findViewById(R.id.t_pb_cal);
       Intent intentExtras = getIntent();
	  String jsonString = intentExtras.getStringExtra("device");
	  DATA_RESOLUTION = intentExtras.getIntExtra("resolution", 3);
	  CLEAR_TEXT_ON_TOUCH = intentExtras.getBooleanExtra("CLEAR", true);
	  DONE_FLAG = false;
	  device = extractCalData(jsonString);

	  cr = new CalRecord(device.EquipID);
	  cr.createTestData(0, device);
	  LABEL = cr.CalData.get(0).Name;
	  e_label.setText(LABEL);
	  p_n_btns(null);
	  PAGED = false;

	  t_inpt.append("(" + device.DUnits + ")");
	  t_expt.append("(" + device.CUnits + ")");
	  t_read.append("(" + device.CUnits + ")");  
   }
  
   @Override
   public void onBackPressed()
   { 
	  showDataLossDialog();
   }
   
   public void clickEdit(View view)
   {
	  showDataLossDialog();
   } 
   
   public class CalDataSetViewAdapter extends BaseAdapter {
       final ArrayList<DataRow> data;

       CalDataSetViewAdapter(ArrayList<DataRow> al)
	  {data = al;} 
	  @Override public int getCount()
	  {return data.size();}
	  @Override public Object getItem(int position)
	  {return data.get(position);}
      @Override public long getItemId(int position)
	  {return position;}
      @Override public View getView(final int position, View convertView, final ViewGroup container)
	  {
		 LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 if (convertView == null) {
			convertView = _inflater.inflate(R.layout.datalist, null);
		 }
          TextView c1 = convertView.findViewById(R.id.t_c1_input);
          TextView c2 = convertView.findViewById(R.id.t_c2_exp);
          EditText c3 = convertView.findViewById(R.id.e_c3_read);
          TextView c4 = convertView.findViewById(R.id.t_c4_dev);
          DataRow dr = cr.CalData.get(IDX).Data.get(position);
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
				  DataRow dr = cr.CalData.get(IDX).Data.get(position);
				  if (!(s.isEmpty())) {
					 Double readValue = Double.parseDouble(s);
					 cr.CalData.get(IDX).Data.get(position).Read = readValue;
					 String expStr = str(dr.Expected);
					 Double expValue = Double.parseDouble(expStr);
					 Double devValue = (Math.abs(expValue - readValue) / device.CRange) * 100;
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


	private void composeEmail() {
		SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(this);
		Map<String, ?> dspMap = dsp.getAll();
		String defaultEmail = (String) dspMap.get("pref_email");
		String strSubject = "Calibration Results for " + device.EquipID; //+ " on " + cr.date.toString();
		String strBody = "<body>";
		strBody += device.hTable() + "\n";
		strBody += cr.hTable() + "</body>";
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		intent.setType("text/html");
		intent.setData(Uri.parse("mailto:")); // only email apps should handle this
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{defaultEmail});
		intent.putExtra(Intent.EXTRA_SUBJECT, strSubject);
		intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(strBody));
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(Intent.createChooser(intent, "Email:"));
		}
	}
   public void clickSave(View view) {
	   composeEmail();
   }
	  /*  try {
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
	   } */
	  //  Gson gson = new Gson();
	  //  String json = gson.toJson(cr, CalRecord.class);
	  //  Log.i("ME",json);

	//}

    private String str(Double d)
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

   

   private void refreshAdapter()
   {


       ArrayList<DataRow> AL = cr.CalData.get(IDX).Data;
       LABEL = cr.CalData.get(IDX).Name;
	  e_label.setText(LABEL);
       ListAdapter adapter = new CalDataSetViewAdapter(AL);

	  lv_dataset.setAdapter(adapter);
	  PAGED = false;
   }

    private void p_n_btns(View view)
   {
	  cr.CalData.get(IDX).Name = e_label.getText().toString();
	  if (!(view == null)) {
		 if (view.getId() == btn_next.getId()) {
            if (IDX == SIZ - 1) {
			   IDX ++;	   
			   cr.newDataSet("NAME" + String.valueOf(IDX), IDX);
			   cr.createTestData(IDX, device);

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

    private void showDataLossDialog() {
        // Create an instance of the dialog fragment and show it
	  DialogFragment dialog = new DataLossDialogFragment();
	  dialog.show(getSupportFragmentManager(), "DataLossDialogFragment");
   }

   @Override
   public void onDialogPositiveClick(DialogFragment dialog) {
	  cr.CalData.clear();
	  finish();
   }
   @Override
   public void onDialogNegativeClick(DialogFragment dialog) {
	  
	 
   }
   
}

package com.romaka.fivepointapp;
//DONE: ADD device browser / import
//DONE: ADD database storage
//DONE: FIX main layout: reduce reliance on layered layouts

//AIDE: =================================
//TODO: ADD comment/notes feature
//TODO: ADD content to help activity
//TODO: ADD dev column flip to absolute dev (abs(read/expected*100))
//TODO: FIX contact email should have an auto filled subject line

//ANST:=================================
//TODO: ADD text field auto-complete from db history
//TODO: FIX save data function. work on at home.
//TODO: FIX assess public objects for privitization

//UNKN: =================================
//TODO: ADD other formats (CSV, HTML, PDF, ...)

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainActivity extends FragmentActivity
        implements WarningDialogFragment.OnFragmentInteractionListener {
    private static final String DFLT_DEVICE = "dd";
    private static FivePointDB db;
    private EditText e_id;
    private EditText e_serial;
    private EditText e_make;
    private EditText e_model;
    private EditText e_dlrv;
    private EditText e_durv;
    private EditText e_clrv;
    private EditText e_curv;
    private EditText e_steps;
    public EditText e_notes;
    private RadioGroup rg_linsq;
    private RadioButton rb_lin;
    private RadioButton rb_squ;
    private Button btn_cal;
    private Button btn_reset;
    private Button btn_clr;
    private Spinner spin_dunit;
    private Spinner spin_cunit;
    private LinearLayout ll;
    private ArrayList<String> units_values;
    private ArrayList<EditText> mandos;
    private Instrument device;
    private SharedPreferences dsp;
    private Map<String, ?> dspMap;
    private Boolean CLEAR_TEXT_ON_TOUCH;
    private Integer DATA_RESOLUTION;
    private Boolean DONE_FLAG;
    private Boolean FIRST_RUN;
    private ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.i("ME", "Initializing...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        e_id = findViewById(R.id.e_id);
        e_serial = findViewById(R.id.e_serial);
        e_make = findViewById(R.id.e_make);
        e_model = findViewById(R.id.e_model);
        e_dlrv = findViewById(R.id.e_dlrv);
        e_durv = findViewById(R.id.e_durv);
        e_clrv = findViewById(R.id.e_clrv);
        e_curv = findViewById(R.id.e_curv);
        e_steps = findViewById(R.id.e_steps);
        //e_notes = (EditText) findViewById(R.id.e_notes);

        btn_cal = findViewById(R.id.btn_cal);
        btn_reset = findViewById(R.id.btn_reset);
        btn_clr = findViewById(R.id.btn_clr);
        spin_dunit = findViewById(R.id.spin_dunit);
        spin_cunit = findViewById(R.id.spin_cunit);
        rb_lin = findViewById(R.id.rb_lin);
        rb_squ = findViewById(R.id.rb_squ);
        cl = findViewById(R.id.mainconstraint);

        mandos = new ArrayList<>();
        mandos.add(e_dlrv);
        mandos.add(e_durv);
        mandos.add(e_clrv);
        mandos.add(e_curv);
        mandos.add(e_steps);
        DONE_FLAG = false;
        FIRST_RUN = true;

        db = Room.databaseBuilder(getApplicationContext(),
                FivePointDB.class, "FivePointDB")
                .allowMainThreadQueries()
                .build();

        device = new Instrument();
        refreshSP();
        loadDefaultDevice();
        checkSharedPreferences();

        ////Log.i("ME", "...Initialized.");
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(CalRecord.class, new CalRecordAdapter().nullSafe());
        Gson gson = builder.create();

        cl.setFocusableInTouchMode(true);
        cl.requestFocus();


        spin_dunit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                spinLongClick(view);
                return true;
            }
        });

        e_id.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (db.fpdao().getEquipIDs().isEmpty()) {
                    showDialog(getString(R.string.no_devices));
                } else {
                    idLongClick(view);
                }
                return true;
            }
        });

        btn_cal.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CollectForm();
                db.fpdao().insertDevice(device);

                showDialog(getString(R.string.saved_pt1) + device.EquipID + getString(R.string.saved_pt2));
                return true;
            }
        });

        btn_reset.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CollectForm();
                updateDefaultDevice(device);
                return true;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
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
            case R.id.contact:
                clickContact();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialog(String str) {
        WarningDialogFragment dialog = new WarningDialogFragment();
        WarningDialogFragment.newInstance("OK", str).show(this.getSupportFragmentManager(), "WarningDialog");
    }

    private void spinLongClick(View view) {
        Intent intent = new Intent();
        intent.setClassName(getApplicationContext(), "com.romaka.fivepointapp.UnitEditActivity");
        intent.putExtra("units", units_values.toArray(new String[units_values.size()]));
        // Log.i("ME",units_values.toString());
        startActivity(intent);
    }

    private void idLongClick(View view) {
        Intent intent = new Intent();
        intent.setClassName(getApplicationContext(), "com.romaka.fivepointapp.DeviceGridActivity");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == resultCode) && (requestCode == 1)) {
            String eid = data.getStringExtra("equipid");
            device = db.fpdao().getDevice(eid);
            FillForm(device);
        }
    }

    @Override
    protected void onPause() {
        checkSharedPreferences();
        super.onPause();
    }

    @Override
    protected void onStart() {
        checkSharedPreferences();
        super.onStart();
    }

    @Override
    protected void onResume() {
        checkSharedPreferences();
        super.onResume();
    }

    private void refreshSP() {
        dsp = PreferenceManager.getDefaultSharedPreferences(this);
        dspMap = dsp.getAll();
    }


    private void updateDefaultDevice(Instrument inst) {
        SharedPreferences sp = getPreferences(0);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString(DFLT_DEVICE, toGson(device));
        spe.commit();
        showDialog(getString(R.string.new_default) + inst.toString());
    }

    private void firstTimeOnly(SharedPreferences sp) {
        String[] strArr = getResources().getStringArray(R.array.unit_values);
        units_values = new ArrayList<String>(strArr.length);
        Collections.addAll(units_values, strArr);
        SharedPreferences.Editor spe = sp.edit();
        Set<String> uvset = new HashSet<String>(units_values);
        spe.putStringSet("units", uvset);
        spe.putBoolean("first_time", false);
        spe.commit();
        FIRST_RUN = false;
    }

    private void loadDefaultDevice() {
        if (dspMap.containsKey("pref_preload")) {
            Boolean loadDefaultDevice = (Boolean) dspMap.get("pref_preload");
            if (loadDefaultDevice) {
                resetForm();
            }
        }
    }

    private void checkSharedPreferences() {
        refreshSP();
        if (!dspMap.containsKey("first_time")) {
            firstTimeOnly(dsp);
        } else {
            Set<String> uvset = (Set<String>) dspMap.get("units");
            units_values = new ArrayList<>(uvset.size());
            Collections.addAll(units_values, uvset.toArray(new String[uvset.size()]));
        }
        refreshSP();
        CLEAR_TEXT_ON_TOUCH = (Boolean) dspMap.get("pref_clear_read");
        DATA_RESOLUTION = dspMap.get("pref_resolution") == null ? 3 : Integer.parseInt((String) dspMap.get("pref_resolution"));
        spin_dunit.setAdapter(new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, units_values));
        spin_cunit.setAdapter(new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, units_values));
        setSpinners();

    }

    public String str(Double d) {
        return String.format("%." + DATA_RESOLUTION.toString() + "f", d);
    }

    public String str(int i) {
        return String.valueOf(i);
    }


    private void clickSettings() {
        Intent intent = new Intent();
        intent.setClassName(this, "com.romaka.fivepointapp.SettingsActivity");
        startActivity(intent);
        checkSharedPreferences();
    }

    private void composeEmail() {
        SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, ?> dspMap = dsp.getAll();
        String defaultEmail = (String) dspMap.get("pref_email");
        String strSubject = "Calibration Results for " + device.EquipID; //+ " on " + cr.date.toString();
        String strBody = device.toString() + "\n";
        strBody += "JSON String\n";

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{defaultEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, strSubject);
        intent.putExtra(Intent.EXTRA_TEXT, strBody);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void clickShare() {
        composeEmail();
    }

    private void clickHelp() {
        Intent intent = new Intent();
        intent.setClassName(this, "com.romaka.fivepointapp.HelpActivity");
        startActivity(intent);
    }

    private void clickContact() {
        Intent intent = new Intent();
        intent.setClassName(this, "com.romaka.fivepointapp.Contact" +
                "");
        startActivity(intent);
    }

    private void resetForm() {
        ////Log.i("ME", "Starting reset...");
        ClearForm((ViewGroup) findViewById(R.id.mainconstraint));
        Instrument backupDevice = new Instrument();
        Gson gson = new Gson();
        SharedPreferences sp = getPreferences(0);
        String strJSON = sp.getString(DFLT_DEVICE, gson.toJson(backupDevice));
        ////Log.i("ME", strJSON);
        Instrument inst = gson.fromJson(strJSON, Instrument.class);
        FillForm(inst);
        ////Log.i("ME", cr.Device.toString() + "\n...Reset Complete.");
    }

    public void clickReset(View view) {
        resetForm();
    }

    public void clickClear(View view) {
        //Log.i("ME", "Clearing...");
        ClearForm((ViewGroup) findViewById(R.id.mainconstraint));
        //Log.i("ME", "...Cleared.");
    }


    public void clickCal(View view) {
        if (FormFilledRight(mandos)) {
            CollectForm();
            Intent intent = new Intent();
            intent.setClassName(this, "com.romaka.fivepointapp.CalActivity");
            intent.putExtra("device", toGson(device));
            intent.putExtra("resolution", DATA_RESOLUTION);
            intent.putExtra("clear", CLEAR_TEXT_ON_TOUCH);
            startActivity(intent);
        }
    }

    private String toGson(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    public void ClearEditText(View view) {
        if (view instanceof EditText) {
            EditText et = (EditText) view;
            et.setText("");
            et.setBackgroundColor(Color.RED);
            et.requestFocus();
            et.selectAll();
        }
    }

    private void setSpinners() {
        int dindex = units_values.indexOf(device.DUnits);
        int cindex = units_values.indexOf(device.CUnits);
        spin_dunit.setSelection(dindex);
        spin_cunit.setSelection(cindex);
    }

    private void FillForm(Instrument inst) {
        try {
            e_id.setText(inst.EquipID);
            e_serial.setText(inst.Serial);
            e_make.setText(inst.Make);
            e_model.setText(inst.Model);
            e_dlrv.setText(inst.DLRV.toString());
            e_durv.setText(inst.DURV.toString());
            e_clrv.setText(inst.CLRV.toString());
            e_curv.setText(inst.CURV.toString());
            e_steps.setText(inst.Steps.toString());
            rb_lin.setChecked(true);
            setSpinners();
        } catch (Exception e) {
            e.printStackTrace();
            ClearForm((ViewGroup) findViewById(R.id.mainconstraint));
        }
    }

    private Boolean FormFilledRight(ArrayList<EditText> mfs) {
        ////Log.i("ME", "Starting Form Validation...");
        Boolean gonogo = true;
        Iterator it = mfs.iterator();
        while (it.hasNext()) {
            EditText e = (EditText) it.next();
            if (e.getText().length() == 0) {
                e.setBackgroundColor(Color.RED);
            } else {
                e.setBackground(getDrawable(R.drawable.bg6));
            }
            gonogo = (e.getText().length() == 0) ? false : gonogo;
        }
        ////Log.i("ME", "...Form Validation Complete.");
        return gonogo;
    }

    private void CollectForm() {
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
        device = new Instrument(id, se, ma, mo, dl, du, dun, cl, cu, cun, st, il);
        // cr.Notes = no;
    }

    // Got this procedure from
// https://stackoverflow.com/questions/5740708/android-clearing-all-edittext-fields-with-clear-button
    private void ClearForm(ViewGroup group) {

        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).setText("");
                view.setBackground(getDrawable(R.drawable.bg6));
            }
            if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0)) {
                ClearForm((ViewGroup) view);
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

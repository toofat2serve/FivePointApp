package com.mycompany.fivepointapp;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.util.*;

import android.content.*;
import android.view.inputmethod.*;

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
    public RadioGroup rg_linsq;
    public Button btn_save;
    public Button btn_edit;
    public Button btn_reset;
    public Button btn_clr;
    public ViewSwitcher vs_save_edit;
    public Boolean focusLocked;
    public CalRecord cr;
    public ArrayList<EditText> mandos;
    public ProgressBar pb_calibration;
    public EditText e_test;
    public Adapter lv_ds_adapter;
    public ListView lv_dataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        focusLocked = false;
        cr = new CalRecord();
        init();

    }

    public void init() {
        Log.i("ME", "Initializing...");
        e_id = (EditText) findViewById(R.id.e_id);
        e_serial = (EditText) findViewById(R.id.e_serial);
        e_make = (EditText) findViewById(R.id.e_make);
        e_model = (EditText) findViewById(R.id.e_model);
        e_dlrv = (EditText) findViewById(R.id.e_dlrv);
        e_durv = (EditText) findViewById(R.id.e_durv);
        e_clrv = (EditText) findViewById(R.id.e_clrv);
        e_curv = (EditText) findViewById(R.id.e_curv);
        e_steps = (EditText) findViewById(R.id.e_steps);
        rg_linsq = (RadioGroup) findViewById(R.id.rg_linsq);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_clr = (Button) findViewById(R.id.btn_clr);
        vs_save_edit = (ViewSwitcher) findViewById(R.id.vs_save_edit);
        mandos = new ArrayList<EditText>();
        e_test = (EditText) findViewById(R.id.e_test);
        pb_calibration = (ProgressBar) findViewById(R.id.pb_calibration);
        lv_dataset = (ListView) findViewById(R.id.listview_dataset);
        mandos.add(e_dlrv);
        mandos.add(e_durv);
        mandos.add(e_clrv);
        mandos.add(e_curv);
        mandos.add(e_steps);

        e_test.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View p1, boolean p2) {
                if (!p2) {
                    pb_calibration.setProgress(Integer.parseInt(e_test.getText().toString()));
                }
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _v) {
                Log.i("ME", "Starting reset...");
                ClearForm((ViewGroup) findViewById(R.id.lv_rows));
                CalRecord cr = new CalRecord();
                FillForm(cr.Device);
                Log.i("ME", cr.Device.toString() + "\n...Reset Complete.");
            }
        });

        btn_clr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _v) {
                Log.i("ME", "Clearing...");
                ClearForm((ViewGroup) findViewById(R.id.lv_rows));
                Log.i("ME", "...Cleared.");
            }
        });

        Log.i("ME", "...Initialized.");
    }

    public void ClearEditText(View view) {
       // et.selectAll();
        if (view instanceof EditText) {
            EditText et = (EditText) view;
            et.requestFocus();
            et.selectAll();
            if (!(et.getText().toString() == "")) {
                et.selectAll();
            }
        }
    }

    public void ClickEdit(View _v) {
        Log.i("ME", "Starting Edit...");
        if (FormFilledRight(mandos)) {
            FocusControl(false);
            vs_save_edit.showNext();
        }
        if (!cr.Data.isEmpty()) {
            cr.Data.clear();
            lv_dataset.setAdapter(new DataSetViewAdapter(cr.Data));

        }
        Log.i("ME", "...Edit Complete.");
    }

    public void ClickSave(View _v) {
        Log.i("ME", "Starting Save...");
        if (FormFilledRight(mandos)) {
            ArrayList al;
            cr = CollectForm();
            al = cr.Data;
            lv_dataset.setAdapter(new DataSetViewAdapter(cr.Data));
            FocusControl(true);
            vs_save_edit.showNext();

        }
        Log.i("ME", "... Save Complete.");
    }

    public ArrayList<Double> calculateSteps(Double lrv, Double rng, Integer stps) {
        Double increment = (rng / (stps - 1));
        Double stepValue = lrv;
        Integer count;
        ArrayList<Double> al;

        al = new ArrayList<Double>();
        for (count = 0; (count < stps); count++) {
            al.add(count, stepValue);
            stepValue += increment;
        }
        return al;
    }

    public CalRecord createTestData(CalRecord cr) {
        Log.i("ME", "Creating Test Data...");
        Integer count;
        Instrument i = cr.Device;
        ArrayList<Double> inputs = calculateSteps(i.DLRV, i.DRange, i.Steps);
        ArrayList<Double> expecteds = calculateSteps(i.CLRV, i.CRange, i.Steps);
        for (count = 0; (count < i.Steps); count++) {
            cr.storeData(count, inputs.get(count), expecteds.get(count), 0.0, 0.0);

        }
        Log.i("ME", "... Test Data Creation Complete.");
        Log.i("ME", cr.toString());
        return cr;
    }

    public void FocusControl(Boolean lock) {
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
        Iterator it = formfields.iterator();
        while (it.hasNext()) {
            EditText e = (EditText) it.next();
            e.setFocusable(!lock);
            e.setFocusableInTouchMode(!lock);
        }
        focusLocked = lock;
    }

    public void FillForm(Instrument inst) {
        e_id.setText(inst.ID);
        e_serial.setText(inst.Serial);
        e_make.setText(inst.Make);
        e_model.setText(inst.Model);
        e_dlrv.setText(inst.DLRV.toString());
        e_durv.setText(inst.DURV.toString());
        e_clrv.setText(inst.CLRV.toString());
        e_curv.setText(inst.CURV.toString());
        e_steps.setText(inst.Steps.toString());
    }

    public Boolean FormFilledRight(ArrayList<EditText> mfs) {
        Log.i("ME", "Starting Form Validation...");

        Boolean gonogo = true;
        Iterator it = mfs.iterator();
        while (it.hasNext()) {
            EditText e = (EditText) it.next();
            if (e.getText().length() == 0) {
                e.setBackgroundColor(Color.RED);
            } else {
                e.setBackgroundColor(Color.parseColor("#303030"));
            }
            gonogo = (e.getText().length() == 0) ? false : gonogo;
        }
        Log.i("ME", "...Form Validation Complete.");
        return gonogo;
    }

    public CalRecord CollectForm() {
        Log.i("ME", "Starting Collection...");
        String id = e_id.getText().toString();
        String se = e_serial.getText().toString();
        String ma = e_make.getText().toString();
        String mo = e_model.getText().toString();
        Double dl = Double.parseDouble(e_dlrv.getText().toString());
        Double du = Double.parseDouble(e_durv.getText().toString());
        Double cl = Double.parseDouble(e_clrv.getText().toString());
        Double cu = Double.parseDouble(e_curv.getText().toString());
        Integer st = Integer.parseInt(e_steps.getText().toString());
        Boolean il = rg_linsq.getCheckedRadioButtonId() == (Integer) 2131099684;
        CalRecord cr = new CalRecord(new Instrument(id, se, ma, mo, dl, du, "", cl, cu, "", st, il));
        Log.i("ME", cr.Device.toString());
        Log.i("ME", "... Collection Complete.");
        return createTestData(cr);
    }

    // Got this procedure from
// https://stackoverflow.com/questions/5740708/android-clearing-all-edittext-fields-with-clear-button
    public void ClearForm(ViewGroup group) {
        if (!focusLocked) {
            for (int i = 0, count = group.getChildCount(); i < count; ++i) {
                View view = group.getChildAt(i);
                if (view instanceof EditText) {
                    ((EditText) view).setText("");
//((EditText)view).setFocusable(false);
                    view.setBackgroundColor(Color.parseColor("#303030"));
                }
                if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0))
                    ClearForm((ViewGroup) view);
            }
        }
    }




    public class DataSetViewAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> data;

        public DataSetViewAdapter(ArrayList<HashMap<String, String>> arr) {
            data = arr;
        }



        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public HashMap<String, String> getItem(int _i) {
            return data.get(_i);
        }

        @Override
        public long getItemId(int _i) {
            return _i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
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




            c3.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {


                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Do something before Text Change
                }
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Do something while Text Change
                }
            });

            c3.setOnEditorActionListener(new EditText.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView view, int actionID, KeyEvent event) {
                    if (actionID == EditorInfo.IME_ACTION_DONE) {
                        if (view instanceof  EditText) {
                            EditText et = (EditText) view;
                            String s = et.getText().toString();
                            HashMap hm = new HashMap();
                            hm = data.get(position);
                            Log.i("ME", "hm = data.get(position) : \n" + hm.entrySet());
                            hm.put("Read",s);


                            data.set(position,hm);
                        }
                        cr.Data = data;
                        Log.i("ME", "Data Update: \n" + cr.toString());
                        view.clearFocus();
                    }
                    return false;
                }


            });



            return convertView;
        }
    }




}


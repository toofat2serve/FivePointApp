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

public class UnitEditActivity extends Activity {
    private ListView lv_unit_edit;
    private Button btn_add_unit;
    private EditText e_add_unit;
    private ArrayList<String> units_values;
    Set<String> uvset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.i("ME", "Starting unit edit activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unit_dia_frag);
        lv_unit_edit = findViewById(R.id.lv_unit_edit);
        btn_add_unit = findViewById(R.id.btn_add_unit);
        e_add_unit = findViewById(R.id.e_add_unit);
        Intent intentExtras = getIntent();
        String[] uv = intentExtras.getStringArrayExtra("units");
        units_values = new ArrayList<String>(uv.length);
        Collections.addAll(units_values, uv);
        refreshAdapter(units_values);

        e_add_unit.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_DONE) {
                    addUnit(view);
                }
                return false;
            }
        });
    }


    private void setSharedPrefUnits(ArrayList<String> uv) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor spe = sp.edit();
        HashSet<String> uvSet;
        uvSet = new HashSet<String>();
        for (String s : uv) {
            uvSet.add(s);
        }

        spe.putStringSet("units", uvSet);
        spe.commit();

    }

    private void refreshAdapter(ArrayList<String> strs) {
        ListAdapter adapter = new UnitAdapter(strs);
        lv_unit_edit.setAdapter(adapter);
    }


    private void removeUnit(int index) {
        units_values.remove(index);
        setSharedPrefUnits(units_values);
        refreshAdapter(units_values);
    }

    void addUnit(View view) {

        String str = e_add_unit.getText().toString();
        units_values.add(str);
        setSharedPrefUnits(units_values);
        refreshAdapter(units_values);
        e_add_unit.setText("");
    }

    public class UnitAdapter extends BaseAdapter {
        final ArrayList<String> arr;

        UnitAdapter(ArrayList<String> strs) {
            arr = strs;
        }

        @Override
        public View getView(final int pos, View cV, ViewGroup container) {
            LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (cV == null) {
                cV = _inflater.inflate(R.layout.unit_edit, null);
            }

            TextView t_unit = cV.findViewById(R.id.unit_label);
            ImageButton btn_del = cV.findViewById(R.id.btn_delete);
            t_unit.setText(arr.get(pos));

            btn_del.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    removeUnit(pos);
                    notifyDataSetChanged();
                }
            });
            return cV;
        }

        @Override
        public int getCount() {
            return arr.size();
        }

        @Override
        public Object getItem(int p1) {
            return arr.get(p1);
        }

        @Override
        public long getItemId(int p1) {
            return p1;
        }
    }
}

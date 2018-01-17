package com.romaka.fivepointapp;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviceGridActivity extends AppCompatActivity {
    private GridView gv_devices;
    private FivePointDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_grid);


        db = Room.databaseBuilder(getApplicationContext(),
                FivePointDB.class, "FivePointDB")
                .allowMainThreadQueries()
                .build();

        final ArrayList<String> idArray = (ArrayList<String>) db.fpdao().getEquipIDs();

        gv_devices = findViewById(R.id.gv_devices);
        DeviceGridAdapter dga = new DeviceGridAdapter(this, idArray);
        gv_devices.setAdapter(dga);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    class DeviceGridAdapter extends BaseAdapter {
        final Context mContext;
        final ArrayList<String> data;

        public DeviceGridAdapter(Context c, ArrayList<String> al) {
            mContext = c;
            data = (ArrayList<String>) db.fpdao().getEquipIDs();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView view;
            ViewGroup.MarginLayoutParams textViewParams = new ViewGroup.MarginLayoutParams(
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT);


            String recordString = data.get(position) + "\n" + db.fpdao().getDevFields(data.get(position)).toString();
            if (convertView == null) {
                view = new TextView(mContext);
                view.setLayoutParams(textViewParams);
            } else {
                view = (TextView) convertView;
            }
            view.setBackground(getDrawable(R.drawable.bg6));
            view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            view.setText(recordString);


            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("equipid", data.get(position));
                    setResult(1, intent);
                    finish();
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View view) {
                    db.fpdao().deleteDevice(db.fpdao().getDevice(data.get(position)));
                    data.remove(position);
                    notifyDataSetChanged();
                    return true;
                }
            });

            return view;
        }

    }

}

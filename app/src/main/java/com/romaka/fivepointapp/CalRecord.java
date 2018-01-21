package com.romaka.fivepointapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "calrecords",
        foreignKeys = @ForeignKey(entity = Instrument.class,
                parentColumns = "EquipID",
                childColumns = "EquipID",
                onDelete = CASCADE))
public class CalRecord {
    @PrimaryKey
    private int crID;
    private String EquipID;
    private String CalDate;
    private String Notes;

    @Ignore
    final ArrayList<CalDataSet> CalData;

    public CalRecord(String equipid,
                     ArrayList<CalDataSet> calData,
                     String notes) {
        EquipID = equipid;
        CalData = calData;
        String CalDate = new Date().toString();
        Notes = notes;
    }

    public CalRecord(String equipid) {
        EquipID = equipid;
        CalData = new ArrayList<>();
        CalData.add(0, new CalDataSet());
        String CalDate = new Date().toString();
        Notes = "";
    }

    public CalRecord() {
        EquipID = "DEFAULT_ID";
        CalData = new ArrayList<CalDataSet>();
        String CalDate = new Date().toString();
        Notes = "";
    }

    public CalRecord fromJSON(String json) {
        CalRecord cr = new CalRecord();
        try {
            Gson gson = new Gson();
            cr = gson.fromJson(json, CalRecord.class);
        } catch (Exception e) {
            cr = new CalRecord();
        }
        return cr;
    }

    public void newDataSet(String name, int index) {
        CalData.add(index, new CalDataSet(name));
    }

    private ArrayList<DataRow> getSet(int index) {
        return CalData.get(index).Data;
    }

    //TODO: check this function to see if it works
    public String hTable() {
        String s = "<table>";
        Iterator it = CalData.iterator();
        while (it.hasNext()) {
            CalDataSet ds = (CalDataSet) it.next();
            s += "<tr><th colspan=\"4\">" + ds.Name + "</th></tr>";
            for (int i = 0; i < ds.Data.size(); i++) {
                DataRow dr = ds.Data.get(i);
                s += "<tr>";
                for (int j = 0; j < dr.toArray().size(); j++) {
                    s += "<td>" + String.valueOf(dr.toArray().get(j)) + "</td>";
                }
                s += "</tr>";
            }
        }
        s += "</table>";
        return s;
    }


    public int countReadNulls(int index) {
        int nullCount = 0;
        CalDataSet CalSet = CalData.get(index);
        Iterator it = CalSet.Data.iterator();
        while (it.hasNext()) {
            DataRow itdr = (DataRow) it.next();
            if (itdr.isNull(itdr.Read)) {
                nullCount++;
            }
        }
        return nullCount;
    }

    public String crToJson() {


        return "";
    }

   /*  public String cdsToJson(){
    Gson g = new Gson();
	String json = g.toJson(this.CalData);
	return json;
	}

	public String cdsdataToJson() {
	Gson g = new Gson();
	String json = "";
	for (int i = 0; i < CalData.size(); i++) {
	json += g.toJson(CalData.get(i).Data);
	}
	return json;

	}*/

    public String dataRowsToJson() {
        Gson g = new Gson();
        String json = "";
        CalDataSet cds;
        for (int i = 0; i < CalData.size(); i++) {
            cds = CalData.get(i);
            ArrayList<DataRow> dr = cds.Data;
            Iterator it = dr.iterator();
            while (it.hasNext()) {
                json += g.toJson(it.next()) + "\n";
            }
        }

        return json;
    }

    private ArrayList<Double> calculateSteps(Double lrv,
                                             Double rng,
                                             Integer stps,
                                             Boolean linear) {
        ArrayList<Double> al = new ArrayList<Double>(stps);
        Double dblStps = stps.doubleValue();
        for (Integer i = 0; i < stps; i++) {
            Double mlt = i / (dblStps - 1.0);
            if (!linear) {
                mlt = Math.pow(mlt, 2.0);
            }
            Double stepValue = lrv + (rng * mlt);
            al.add(stepValue);
        }
        return al;
    }

    public void createTestData(int index, Instrument inst) {
        // Log.i("ME", "Creating Test Data...");

        if (CalData.isEmpty()) {
            CalData.add(new CalDataSet());
        }
        ArrayList<Double> inputs = calculateSteps(inst.DLRV,
                inst.DRange,
                inst.Steps,
                inst.IsLinear);
        ArrayList<Double> expecteds = calculateSteps(inst.CLRV,
                inst.CRange,
                inst.Steps,
                true);
        for (int i = 0; i < inputs.size(); i++) {
            DataRow dr = new DataRow();
            Gson gson = new Gson();

            //Log.i("DataRow", "New DataRow() : " + gson.toJson(dr));
            dr.Step = i;
            dr.Input = inputs.get(i);
            dr.Expected = expecteds.get(i);
            dr.Read = null;
            //myLog(dr.toString());

            CalData.get(index).Data.add(i, dr);
        }
        //  Log.i("ME", "... Test Data Creation Complete.");
        //  Log.i("ME", this.toString());
    }

    public Integer getProgress(int index) {
        Integer intComplete = 0;
        ArrayList CalSet = getSet(index);
        Iterator it = CalSet.iterator();
        while (it.hasNext()) {
            DataRow row = (DataRow) it.next();
            Double dbl = row.Read;
            if (dbl != null) {
                intComplete++;
            }
        }
        Integer ds = CalSet.size();
        Double dbl = (Double.parseDouble(intComplete.toString()) / (Double.parseDouble(ds.toString()))) * 100;
        String str = String.valueOf((Math.round(dbl)));
        return Integer.parseInt(str);
    }



   /* @Override
	public String toString()
	{
	String str;
	str = "CALIBRATION RECORD - " + date.toString() + "\n";
	str += Device.toString() + "\n";
	for (int i = 0; i < CalData.size(); i++) {
	CalDataSet CalSet = CalData.get(i);
	Iterator it =    CalSet.Data.iterator();
	while (it.hasNext()) {
	DataRow n = (DataRow) it.next();
	str += n.toString() + "\n";
	}
	}
	return str;
	}*/


}

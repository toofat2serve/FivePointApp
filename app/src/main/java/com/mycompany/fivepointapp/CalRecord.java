package com.mycompany.fivepointapp;
import java.util.*;
import android.util.*;
import java.io.*;
import com.google.gson.*;

public class CalRecord
{
	Instrument Device;
	ArrayList CalData;
	Date date;
	
	public CalRecord()
	{
		Device = new Instrument();
		CalData = new ArrayList(Device.Steps);
		date = new Date();
	}
	
	public CalRecord(Instrument device)
	{
		Device = device;
		CalData = new ArrayList(Device.Steps);
		date = new Date();
	}

	public CalRecord(Instrument device, ArrayList data)
	{
		Device = device;
		CalData = data;
		date = new Date();
	}

	public void setDevice(Instrument device)
	{
		Device = device;
	}

	public Instrument getDevice()
	{
		return Device;
	}
	
	public void storeData(Integer position, Double i, Double e, Double r, Double d){
		HashMap<String,String> hm = new HashMap<String,String>();
		hm.put("Step",
			   (position == null)? " ": String.valueOf(position + 1));
		hm.put("Input",
			   (position == null)? " ": String.format("%.3f",i));
		hm.put("Expected",
			   (position == null)? " ": String.format("%.3f",e));
		hm.put("Read", "");
		hm.put("Dev",
			   (position == null)? " ": String.format("%.3f",d));
		CalData.add(position, hm);
	
	}
	private ArrayList<Double> calculateSteps(Double lrv, Double rng, Integer stps, Boolean linear) {
		ArrayList<Double> al = new ArrayList<Double>(stps);
		Double dblStps = stps.doubleValue();
		for (Integer i = 0; i < stps; i++)
		{
			Double mlt = i / (dblStps-1.0);
			if (!linear)
			{
				mlt = Math.pow(mlt, 2.0);
			}
			Double stepValue = lrv + (rng * mlt);
			al.add(stepValue);
		}	
		return al;
	}

    public CalRecord createTestData() {
        Log.i("ME", "Creating Test Data...");
        Integer count;
        Instrument i = this.Device;
        ArrayList<Double> inputs = calculateSteps(i.DLRV, i.DRange, i.Steps, i.IsLinear);
        ArrayList<Double> expecteds = calculateSteps(i.CLRV, i.CRange, i.Steps, true);
        for (count = 0; (count < i.Steps); count++) {
            this.storeData(count, inputs.get(count), expecteds.get(count), 0.0, 0.0);
        }
        Log.i("ME", "... Test Data Creation Complete.");
        Log.i("ME", this.toString());
        return this;
    }
	
	public Integer getProgress() {
		Integer intComplete = 0;
		Iterator it = CalData.iterator();
		while (it.hasNext()) {
			HashMap row = (HashMap) it.next();
			String s = (String) row.get("Read");
			if (!(s.isEmpty())) {
				intComplete++;
			}
		}
		Integer ds = CalData.size();
		Double dbl = (Double.parseDouble(intComplete.toString()) / (Double.parseDouble(ds.toString()))) * 100;
		String str = String.valueOf((Math.round(dbl)));
		return Integer.parseInt(str);
	}
	
	public String makeFileName() {
		String str = this.Device.ID + "-";
		str += String.valueOf(date.getYear());
		str += String.valueOf(date.getMonth());
		str += String.valueOf(date.getDate());
		str += String.valueOf(date.getHours());
		str += String.valueOf(date.getMinutes());
		str += String.valueOf(date.getSeconds());
		str += ".txt";
		return str;
	}
	
	public String makeJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	
	@Override
	public String toString()
	{
		String str;
		str = "CALIBRATION RECORD - " + date.toString() + "\n";
		str += Device.toString() + "\n";
		Iterator it = this.CalData.iterator();
		while (it.hasNext()) {
			HashMap m = (HashMap) it.next();
			str += m.toString() + "\n";
		}
		return str;
	}
	
	public String toString(Boolean dataOnly)
	{
		String str;
		if (!dataOnly) {
			str = this.toString();
			str += Device.toString() + "\n";
		}
		else
		{
			str = "DATA RECORD\n";
			Iterator it = this.CalData.iterator();
			while (it.hasNext()) 
			{
				HashMap m = (HashMap) it.next();
				str += m.toString() + "\n";
			}
		}
		return str;
	}
	
}

package com.mycompany.fivepointapp;
import java.util.*;
import android.util.*;
import java.io.*;
import com.google.gson.*;
import android.content.*;
import android.preference.*;
import android.view.*;

public class CalRecord {
   Instrument Device;
   ArrayList<DataRow> CalData;
   Date date;
   String Notes;


   public CalRecord(String json)
   {
      CalRecord cr = new CalRecord();
	  try {
		 Gson gson = new Gson();
		 cr.Device = gson.fromJson(json, Instrument.class); 
	  }
	  catch (Exception e) {
		 cr = new CalRecord();
		 //Log.i("ME", "Error in JSON CalRecord", e);
	  }
	  Device = cr.Device;
	  CalData = cr.CalData;
	  date = cr.date;
	  Notes = cr.Notes;
   }

   public CalRecord()
   {
	  Device = new Instrument();
	  CalData = new ArrayList<DataRow>(Device.Steps);
	  date = new Date();
	  Notes = "";
   }

   public CalRecord(Instrument device)
   {
	  Device = device;
	  CalData = new ArrayList<DataRow>(Device.Steps);
	  date = new Date();
	  Notes = "";
   }

   public CalRecord(Instrument device, ArrayList<DataRow> data)
   {
	  Device = device;
	  CalData = data;
	  date = new Date();
	  Notes = "";
   }

   public void setDevice(Instrument device)
   {
	  Device = device;
   }

   public Instrument getDevice()
   {
	  return Device;
   }

   private String sdFormat(Integer sd)
   {
	  String str = "%." + sd.toString() + "f";  
	  return str;
   }

   public int countReadNulls() {
	  int nullCount = 0;
	  Iterator it = CalData.iterator();
	  while (it.hasNext()) {
		 DataRow itdr = (DataRow) it.next();
		 if (itdr.isNull(itdr.Read)) {
			nullCount ++;
		 }
	  }
	  return nullCount;
   }




   private ArrayList<Double> calculateSteps(Double lrv, Double rng, Integer stps, Boolean linear)
   {
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

   public CalRecord createTestData()
   {
      Log.i("ME", "Creating Test Data...");
	  Instrument inst = this.Device;
	  Log.i("ME", this.CalData.toString());
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
		 
		 dr.Step = i;
		 dr.Input = inputs.get(i);
		 dr.Expected = expecteds.get(i);
		 
		 //dr.Read = this.CalData.get(i).Read.SIZE > 0 ? this.CalData.get(i).Read : 0.0;
		 
		// dr.Dev	= this.CalData.get(i).Dev;
		 
			this.CalData.add(i, dr);
		 
		 // this.CalData.set(i,dr);
	  }
	  Log.i("ME", "... Test Data Creation Complete.");
	  Log.i("ME", this.toString());
	  return this;
   }



   public Integer getProgress()
   {
	  Integer intComplete = 0;
	  Iterator it = CalData.iterator();
	  while (it.hasNext()) {
		 DataRow row = (DataRow) it.next();
		 Double dbl = row.Read;
		 if (dbl != null) {
			intComplete++;
		 }
	  }
	  Integer ds = CalData.size();
	  Double dbl = (Double.parseDouble(intComplete.toString()) / (Double.parseDouble(ds.toString()))) * 100;
	  String str = String.valueOf((Math.round(dbl)));
	  return Integer.parseInt(str);
   }

   public String makeFileName()
   {
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

   public String makeJson()
   {
	  Gson gson = new Gson();
	  return gson.toJson(this.Device);
   }


   @Override
   public String toString()
   {
	  String str;
	  str = "CALIBRATION RECORD - " + date.toString() + "\n";
	  str += Device.toString() + "\n";
	  Iterator it = this.CalData.iterator();
	  while (it.hasNext()) {
		 DataRow n = (DataRow) it.next();
		 str += n.toString() + "\n";
	  }
	  return str;
   }


   public class DataRow {
	  int Step;
	  Double Input;
	  Double Expected;
	  Double Read;
	  Double Dev;
	  

	  public DataRow(int step, Double input, Double expected, Double read, Double dev)
	  {
		 Step = step;
		 Input = input;
		 Expected = expected;
		 Read = read;
		 Dev = dev;
	  }

	  public DataRow()
	  {
		 Step = 0;
		 Input = 0.0;
		 Expected = 0.0;
		 Read = null;
		 Dev = 0.0;
	  }

	  public Boolean isNull(Double d) {
		 return d == null;
	  }
	  
	  public Boolean isNull(Integer i) {
		 return i == null;
	  }
	  
	  @Override
	  public String toString()
	  {
		 String str = "DataRow:\tStep:" + String.valueOf(Step)
			+ "\tInput:" + String.valueOf(Input)
			+ "\tExpect:" + String.valueOf(Expected)
			+ "\tRead:" + String.valueOf(Read)
			+ "\tDev:" + String.valueOf(Dev) + "\n";
		 return str;
	  }


   }

}

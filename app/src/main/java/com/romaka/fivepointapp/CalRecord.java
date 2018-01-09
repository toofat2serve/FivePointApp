package com.romaka.fivepointapp;
import java.util.*;
import android.util.*;
import java.io.*;
import com.google.gson.*;
import android.content.*;
import android.preference.*;
import android.view.*;
import android.os.*;

public class CalRecord {

   Instrument Device;
   ArrayList<CalDataSet> CalData;
   Date date;
   String Notes;
 
   public CalRecord(Instrument device, 
                    ArrayList<CalDataSet> calData, 
					String notes)
   {
	  Device = device;
	  CalData = calData;
	  date = new Date();
	  Notes = notes;
   }

   public CalRecord(Instrument instrument)
   {
	  Device = instrument;
	  CalData = new ArrayList<CalDataSet>();
	  CalData.add(0, new CalDataSet());
	  date = new Date();
	  Notes = "";
   }
   
   public CalRecord()
   {
	  Device = new Instrument();
	  CalData = new ArrayList<CalDataSet>();
	  date = new Date();
	  Notes = "";
   }

   public CalRecord(String json)
   {
      CalRecord cr = new CalRecord();
	  try {
		 Gson gson = new Gson();
		 cr.Device = gson.fromJson(json, Instrument.class); 
	  }
	  catch (Exception e) {
		 cr = new CalRecord();
	  }
	  Device = cr.Device;
	  CalData = cr.CalData;
	  date = cr.date;
	  Notes = cr.Notes;
   }
   
   public void newDataSet(String name, int index) {
	  CalData.add(index, new CalDataSet(name));
   }
   
   public ArrayList<DataRow> getSet(int index) {
	  return CalData.get(index).Data;
   }

   public void setDevice(Instrument device)
   {
	  Device = device;
   }

   public Instrument getDevice()
   {
	  return Device;
   }

   public void myLog(String str) {
	  Log.i("CalRecord", str);
   }
   public int countReadNulls(int index)
   {
	  int nullCount = 0;
	  CalDataSet CalSet =  CalData.get(index);
	  Iterator it = CalSet.Data.iterator();
	  while (it.hasNext()) {
		 DataRow itdr = (DataRow) it.next();
		 if (itdr.isNull(itdr.Read)) {
			nullCount ++;
		 }
	  }
	  return nullCount;
   }

   private ArrayList<Double> calculateSteps(Double lrv, 
                                            Double rng, 
											Integer stps, 
											Boolean linear)
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

   public void createTestData(int index)
   {
     // Log.i("ME", "Creating Test Data...");
	  if (CalData.isEmpty()) {
		 CalData.add(new CalDataSet());
	  }
	  Instrument inst = this.Device;
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
		 
		 CalData.get(index).Data.add(i,dr);
		 }
	//  Log.i("ME", "... Test Data Creation Complete.");
	//  Log.i("ME", this.toString());
   }

   public Integer getProgress(int index)
   {
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
	  for (int i = 0; i < CalData.size(); i++) {
		 CalDataSet CalSet = CalData.get(i);
		 Iterator it =    CalSet.Data.iterator();
		  while (it.hasNext()) {
			DataRow n = (DataRow) it.next();
			str += n.toString() + "\n";
		 }
	  }
	  return str;
   }

   public class CalDataSet {
	  String Name;
	  ArrayList<DataRow> Data;

	  public CalDataSet(String name, ArrayList<DataRow> data)
	  {
		 Name = name;
		 Data = data;
	  }

	  public CalDataSet(String name)
	  {
		 Name = name;
		 Data = new ArrayList<DataRow>();
	  } 
	  
	  public CalDataSet()
	  {
		 Name = "As Found";
		 //DataRow dr = new DataRow();
		 Data = new ArrayList<DataRow>(Device.Steps);
		// Data.add(dr);
	  }

	  @Override
	  public String toString()
	  {
		 String str = "CalDataSet\nName: " + Name + "\n";
		 Iterator it = Data.iterator();
		 while (it.hasNext()) {
			str += it.next().toString();
		 }
		 return str;
	  } 
	  
   }

   public class DataRow {
	  int Step;
	  Double Input;
	  Double Expected;
	  Double Read;
	  Double Dev;

	  public DataRow(int step, 
	                 Double input, 
					 Double expected, 
					 Double read, 
					 Double dev)
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
		 Read = 0.0;
		 Dev = 0.0; 
	  }

	  public Boolean isNull(Double d)
	  {
		 return d == null;
	  }

	  public Boolean isNull(Integer i)
	  {
		 return i == null;
	  }

	 @Override
	  public String toString()
	  {
		 String str = "Step:" + String.valueOf(Step)
			+ "\tInput:" + String.valueOf(Input)
			+ "\tExpect:" + String.valueOf(Expected)
			+ "\tRead:" + String.valueOf(Read)
			+ "\tDev:" + String.valueOf(Dev) + "\n";
		 return str;
	  }


   }

}

package com.romaka.fivepointapp;
import java.util.*;
import java.io.*;
import com.google.gson.*;

import android.preference.*;
import android.view.*;
import android.os.*;
import com.google.gson.stream.*;
import android.arch.persistence.room.*;

@Entity
public class CalRecord {
   @PrimaryKey(autoGenerate = true)
   int crID;
   @ForeignKey
   String EquipID;
   ArrayList<CalDataSet> CalData;
   Date date;
   String Notes;
   
   public CalRecord(String equipid, 
                    ArrayList<CalDataSet> calData, 
					String notes)
   {
	  EquipID = equipid;
	  CalData = calData;
	  date = new Date();
	  Notes = notes;
   }

   public CalRecord(String equipid)
   {
	  EquipID = equipid;
	  CalData = new ArrayList<CalDataSet>();
	  CalData.add(0, new CalDataSet());
	  date = new Date();
	  Notes = "";
   }
   
   public CalRecord()
   {
	  EquipID = "DEFAULT_ID";
	  CalData = new ArrayList<CalDataSet>();
	  date = new Date();
	  Notes = "";
   }

  /* public CalRecord(String json)
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
   } */
   
   public void newDataSet(String name, int index) {
	  CalData.add(index, new CalDataSet(name));
   }
   
   public ArrayList<DataRow> getSet(int index) {
	  return CalData.get(index).Data;
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
   
   public String dataRowsToJson()
   {
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

   public void createTestData(int index, Instrument inst)
   {
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
	  String str = EquipID + "-";
	  str += String.valueOf(date.getYear());
	  str += String.valueOf(date.getMonth());
	  str += String.valueOf(date.getDate());
	  str += String.valueOf(date.getHours());
	  str += String.valueOf(date.getMinutes());
	  str += String.valueOf(date.getSeconds());
	  str += ".txt";
	  return str;
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

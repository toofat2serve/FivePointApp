package com.romaka.fivepointapp;
import com.google.gson.*;
import com.google.gson.stream.*;
import java.io.*;
import java.util.*;
import android.util.Log;

public class CalRecordAdapter extends TypeAdapter<CalRecord> {

   @Override
   public CalRecord read(JsonReader p1) throws IOException
   {

	   return null;
   }


   @Override
   public void write(JsonWriter jw, CalRecord cr) throws IOException
   {

	   jw.beginObject();
	   jw.name("date").value(cr.date.toString());
		/* Instrument dvc = cr.Device;
	     jw.beginObject();
	        jw.name("ID").value(dvc.ID);
	        jw.name("Serial").value(dvc.Serial);
	        jw.name("Make").value(dvc.Make);
	        jw.name("Model").value(dvc.Model);
	        jw.name("DLRV").value(dvc.DLRV);
	        jw.name("DURV").value(dvc.DURV);
	        jw.name("DRange").value(dvc.DRange);
	        jw.name("DUnits").value(dvc.DUnits);
	        jw.name("CLRV").value(dvc.CLRV);
	        jw.name("CURV").value(dvc.CURV);
	        jw.name("CRange").value(dvc.CRange);
	        jw.name("CUnits").value(dvc.CUnits);
	        jw.name("Steps").value(dvc.Steps);
	        jw.name("IsLinear").value(dvc.IsLinear);
	      jw.endObject(); */
	      ArrayList<CalDataSet> cds = cr.CalData;
		  jw.beginArray();
	        for (int i = 0; i < cds.size(); i++) {
		    jw.beginObject();
               jw.name("Name").value(cr.CalData.get(i).Name); 
		       ArrayList<DataRow> dta = cr.CalData.get(i).Data;
			   jw.beginArray();
		          for (int j = 0; j < dta.size(); j++) {
			         DataRow dr = dta.get(j);
					 jw.beginObject();
					 jw.name("Step").value(dr.Step);
					 jw.name("Input").value(dr.Input);
					 jw.name("Expected").value(dr.Expected);
					 jw.name("Read").value(dr.Read);
					 jw.name("Dev").value(dr.Dev);
					jw.endObject();}
		       jw.endArray();
		    jw.endObject();}
	     jw.endArray();
	     jw.endObject();
		 Log.i("ME", jw.toString());
		 }
}

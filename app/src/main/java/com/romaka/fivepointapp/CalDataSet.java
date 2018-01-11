package com.romaka.fivepointapp;

import android.arch.persistence.room.*;
import java.util.*;

@Entity
public class CalDataSet {
   @PrimaryKey(autoGenerate = true)
   int cdsID;
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

   public CalDataSet(int steps)
   {
	  Name = "As Found";
	  Data = new ArrayList<DataRow>(steps);
   }

   public CalDataSet()
   {
	  Name = "As Found";
	  Data = new ArrayList<DataRow>();
   }

   /*  @Override
	public String toString()
	{
	String str = "CalDataSet\nName: " + Name + "\n";
	Iterator it = Data.iterator();
	while (it.hasNext()) {
	str += it.next().toString();
	}
	return str;
	} */

}

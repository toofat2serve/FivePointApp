package com.romaka.fivepointapp;
import java.util.*;


class DataRow {
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
   
   public ArrayList<?> toArray() {
	  ArrayList al = new ArrayList();
	  al.add(0,Input);
	  al.add(1,Expected);
	  al.add(2,Read);
	  al.add(3,Dev);
	  return al;
   }




   /* @Override
	public String toString()
	{
	String str = "Step:" + String.valueOf(Step)
	+ "\tInput:" + String.valueOf(Input)
	+ "\tExpect:" + String.valueOf(Expected)
	+ "\tRead:" + String.valueOf(Read)
	+ "\tDev:" + String.valueOf(Dev) + "\n";
	return str;
	}*/


}

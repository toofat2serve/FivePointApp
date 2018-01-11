package com.romaka.fivepointapp;
import com.google.gson.*;

public class Instrument {

  
   String EquipID;
   String Serial;
   String Make;
   String Model;
   Double DLRV;
   Double DURV;
   Double DRange;
   String DUnits;
   Double CLRV;
   Double CURV;
   Double CRange;
   String CUnits;
   Integer Steps;
   Boolean IsLinear;


   private double calculateRange(Double lrv, Double urv)
   {
	  return Math.abs(urv - lrv);
   }

   public Instrument()
   {
	  EquipID = "123-567-901-3456";
	  Serial = "1234567890";
	  Make = "ABCDEFGHIJ";
	  Model = "1234567890";
	  DLRV = 0.0;
	  DURV = 100.0;
	  DUnits = "PSI";
	  CLRV = 4.0;
	  CURV = 20.0;
	  CUnits = "mA";
	  Steps = 5;
	  IsLinear = true;
	  DRange = calculateRange(DLRV, DURV);
	  CRange = calculateRange(CLRV, CURV);
   }

   public Instrument(String iD, String serial, String make, String model, Double dLRV, Double dURV, String dUnits, Double cLRV, Double cURV, String cUnits, Integer steps, Boolean islinear)
   {
	  EquipID = iD;
	  Serial = serial;
	  Make = make;
	  Model = model;
	  DLRV = dLRV;
	  DURV = dURV;
	  DUnits = dUnits;
	  CLRV = cLRV;
	  CURV = cURV;
	  CUnits = cUnits;
	  Steps = steps;
	  IsLinear = islinear;
	  DRange = calculateRange(DLRV, DURV);
	  CRange = calculateRange(CLRV, CURV);
   }
   
   @Override
   public String toString()
   {
	  // TODO: Implement this method
	  String str;
	  String div;

	  str = "ID: " + EquipID + " / Serial: " + Serial;
	  str += "\nMake: " + Make + " / Model: " + Model;
	  div = "\n";
	  for (int i=0; i < 50; i++) {
		 div += "-";
	  }
	  str += div + "\nDevice     : URV: " + DURV + " / LRV: " + DLRV + " / UNITS: " + DUnits; 
	  str += "\nCalibrator : URV: " + CURV + " / LRV: " + CLRV + " / UNITS: " + CUnits; 
	  str += div + "\nSteps : " + Steps + " / " + ((IsLinear) ? "Linear" : "Square") + div;
	  return str;
   }






}

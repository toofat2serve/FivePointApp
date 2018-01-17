package com.romaka.fivepointapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.*;

@Entity(tableName = "devices", primaryKeys = {"EquipID", "Serial"})
public class Instrument {
    @NonNull
    String EquipID;
    @NonNull
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

    private double calculateRange(Double lrv, Double urv) {
        return Math.abs(urv - lrv);
    }

    public Instrument() {
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

    public Instrument(String iD, String serial, String make, String model, Double dLRV, Double dURV, String dUnits, Double cLRV, Double cURV, String cUnits, Integer steps, Boolean islinear) {
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
	
   private String td(Object o) {
	  return "<td>"+String.valueOf(o)+"</td>";
   }
   
   public Instrument fromJSON(String json) {
	  Gson gson = new Gson();
	  Instrument i = gson.fromJson(json, Instrument.class);
	  return i;
   }
   
   public String hTable() {   
	  String s = "<html><head></head><body><table>\n\t";
	  s += "<tr>\n\t\t<th>EquipmentID</th>\n\t\t<th>Serial</th>\n\t\t<th>Make</th>\n\t\t<th>Model</th>\n\t</tr>";
	  s += "\n\t<tr>" + td(EquipID) + td(Serial) + td(Make) + td(Model) + "\n\t</tr>";
	  s += "\n</table>\n<table>";
	  s += "\n\t<tr>\n\t\t<th colspan=\"3\">Device</th>\n\t\t<th colspan=\"3\">Calibrator</th>\n\t</tr>";
	  s += "\n\t<tr>\n\t\t<th>LRV</th>\n\t\t<th>URV</th>\n\t\t<th>Units</th>\n\t\t<th>LRV</th>\n\t\t<th>URV</th>\n\t\t<th>Units</th>\n\t</tr>";
	  s += "\n\t<tr>" + td(DLRV) + td(DURV) + td(DUnits)+ td(CLRV) + td(CURV) + td(CUnits) +"\n\t</tr>";
	  s += "\n\t<tr>\n\t\t<th colspan=\"3\">Steps</th>\n\t\t<th colspan=\"3\">Liniarity</th>\n\t</tr>";
	  s += "\n\t<tr>\n\t\t<td colspan=\"3\">" + Steps + "</td>\n\t\t<td colspan=\"3\">" + (IsLinear?"Linear":"Square Root") + "</td>\n\t</tr>";
	  return s += "\n</table></body></html>";
   }

	private String tag(String t, Object o) {
	   return "<" + t + ">" + String.valueOf(o) + "</" + t + ">";
	}
	
	public String toXML() {
	   String s = "";
	   
	   return s;
	}
	
    @Override
    public String toString() {
        String str;
        String div;

        str = "ID: " + EquipID + " / Serial: " + Serial;
        str += "\nMake: " + Make + " / Model: " + Model;
        div = "\n";
        for (int i = 0; i < 50; i++) {
            div += "-";
        }
        str += div + "\nDevice     : URV: " + DURV + " / LRV: " + DLRV + " / UNITS: " + DUnits;
        str += "\nCalibrator : URV: " + CURV + " / LRV: " + CLRV + " / UNITS: " + CUnits;
        str += div + "\nSteps : " + Steps + " / " + ((IsLinear) ? "Linear" : "Square") + div;
        return str;
    }

    public String getEquipID() {
        return EquipID;
    }

    public void setEquipID(String equipID) {
        EquipID = equipID;
    }

    public String getSerial() {
        return Serial;
    }

    public void setSerial(String serial) {
        Serial = serial;
    }

    public String getMake() {
        return Make;
    }

    public void setMake(String make) {
        Make = make;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public Double getDLRV() {
        return DLRV;
    }

    public void setDLRV(Double DLRV) {
        this.DLRV = DLRV;
    }

    public Double getDURV() {
        return DURV;
    }

    public void setDURV(Double DURV) {
        this.DURV = DURV;
    }

    public Double getDRange() {
        return DRange;
    }

    public void setDRange(Double DRange) {
        this.DRange = DRange;
    }

    public String getDUnits() {
        return DUnits;
    }

    public void setDUnits(String DUnits) {
        this.DUnits = DUnits;
    }

    public Double getCLRV() {
        return CLRV;
    }

    public void setCLRV(Double CLRV) {
        this.CLRV = CLRV;
    }

    public Double getCURV() {
        return CURV;
    }

    public void setCURV(Double CURV) {
        this.CURV = CURV;
    }

    public Double getCRange() {
        return CRange;
    }

    public void setCRange(Double CRange) {
        this.CRange = CRange;
    }

    public String getCUnits() {
        return CUnits;
    }

    public void setCUnits(String CUnits) {
        this.CUnits = CUnits;
    }

    public Integer getSteps() {
        return Steps;
    }

    public void setSteps(Integer steps) {
        Steps = steps;
    }

    public Boolean getLinear() {
        return IsLinear;
    }

    public void setLinear(Boolean linear) {
        IsLinear = linear;
    }


}

package com.mycompany.fivepointapp;

public class Instrument
{
	String ID;
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
		ID = "123-567-901-3456";
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
		ID = iD;
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

	public void setID(String iD)
	{
		ID = iD;
	}

	public String getID()
	{
		return ID;
	}

	public void setSerial(String serial)
	{
		Serial = serial;
	}

	public String getSerial()
	{
		return Serial;
	}

	public void setMake(String make)
	{
		Make = make;
	}

	public String getMake()
	{
		return Make;
	}

	public void setModel(String model)
	{
		Model = model;
	}

	public String getModel()
	{
		return Model;
	}

	public void setDLRV(Double dLRV)
	{
		DLRV = dLRV;
	}

	public Double getDLRV()
	{
		return DLRV;
	}

	public void setDURV(Double dURV)
	{
		DURV = dURV;
	}

	public Double getDURV()
	{
		return DURV;
	}

	public void setDUnits(String dUnits)
	{
		DUnits = dUnits;
	}

	public String getDUnits()
	{
		return DUnits;
	}

	public void setCLRV(Double cLRV)
	{
		CLRV = cLRV;
	}

	public Double getCLRV()
	{
		return CLRV;
	}

	public void setCURV(Double cURV)
	{
		CURV = cURV;
	}

	public Double getCURV()
	{
		return CURV;
	}

	public void setCUnits(String cUnits)
	{
		CUnits = cUnits;
	}

	public String getCUnits()
	{
		return CUnits;
	}

	public void setSteps(Integer steps)
	{
		Steps = steps;
	}

	public Integer getSteps()
	{
		return Steps;
	}
	
	public void setIsLinear(Boolean isLinear)
	{
		IsLinear = isLinear;
	}

	public Boolean getIsLinear()
	{
		return IsLinear;
	}
	
	@Override
	public String toString()
	{
		// TODO: Implement this method
		String str;
		String div;
		
		str = "ID: " + ID + " / Serial: " + Serial + " / Make: " + Make + " / Model: " + Model;
		div = "\n";
		for (int i=0; i < str.length(); i++) {
			div +="-";
		}
		str += div + "\nDevice     : URV: " + DURV + " / LRV: " + DLRV + " / UNITS: " + DUnits; 
		str += "\nCalibrator : URV: " + CURV + " / LRV: " + CLRV + " / UNITS: " + CUnits; 
		str += div + "\nSteps : " + Steps + " / " + ((IsLinear) ? "Linear" : "Square") + div;
		return str;
	}

	




}

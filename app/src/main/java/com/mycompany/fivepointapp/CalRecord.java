package com.mycompany.fivepointapp;
import java.util.*;

public class CalRecord
{
	Instrument Device;
	ArrayList Data;

	public CalRecord()
	{
		Device = new Instrument();
		Data = new ArrayList(Device.Steps);
	}
	
	public CalRecord(Instrument device)
	{
		Device = device;
		Data = new ArrayList(Device.Steps);
	}

	public CalRecord(Instrument device, ArrayList data)
	{
		Device = device;
		Data = data;
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
			   (position == null)? " ": String.valueOf(i));
		hm.put("Expected",
			   (position == null)? " ": String.valueOf(e));
		hm.put("Read",
			   (position == null)? " ": String.valueOf(r));
		hm.put("Dev",
			   (position == null)? " ": String.valueOf(d));
		Data.add(position, hm);
	
	}
	
	@Override
	public String toString()
	{
		String str;
		str = "CALIBRATION RECORD\n";
		str += Device.toString() + "\n";
		Iterator it = this.Data.iterator();
		while (it.hasNext()) {
			HashMap m = (HashMap) it.next();
			str += m.toString() + "\n";
		}
		
		
		return str;
	}
	
}

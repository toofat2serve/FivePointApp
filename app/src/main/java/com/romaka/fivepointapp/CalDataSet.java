package com.romaka.fivepointapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "caldatasets",
        foreignKeys = @ForeignKey(
                entity = CalRecord.class,
                parentColumns = "crID",
                childColumns = "crID"))
class CalDataSet {
    @PrimaryKey
    int cdsID;
    int rdID;
    String Name;

    @Ignore
    final ArrayList<DataRow> Data;

    public CalDataSet(String name, ArrayList<DataRow> data) {
        Name = name;
        Data = data;
    }

    public CalDataSet(String name) {
        Name = name;
        Data = new ArrayList<DataRow>();
    }

    public CalDataSet(int steps) {
        Name = "As Found";
        Data = new ArrayList<DataRow>(steps);
    }

    public CalDataSet() {
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

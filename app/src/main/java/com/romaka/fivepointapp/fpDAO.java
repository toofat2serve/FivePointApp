package com.romaka.fivepointapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface fpDAO {
    //Instrument queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDevice(Instrument... devices);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCalRecord(CalRecord... calrecords);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCalDataSet(CalDataSet... caldatasets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDataRow(DataRow... datarows);

    @Query("SELECT EquipID FROM devices")
    List<String> getEquipIDs();

    @Query("SELECT * FROM datarows ")

    @Query("SELECT * FROM devices")
    List<Instrument> getDevices();

    @Query("SELECT * FROM devices WHERE EquipID = :eID")
    Instrument getDevice(String eID);

    @Query("SELECT Make, Model, Serial, DLRV, DURV, DUnits FROM devices WHERE EquipID = :eID")
    DevFields getDevFields(String eID);

    @Query("SELECT * FROM calrecords INNER JOIN caldatasets ON calrecords.crID = caldatasets.crID")
    CalDataSet getCalDataSet();

    @Delete
    void deleteDevice(Instrument... instruments);


}
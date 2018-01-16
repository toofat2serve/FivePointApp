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

    @Query("SELECT EquipID FROM devices")
    List<String> getEquipIDs();

    @Query("SELECT * FROM devices")
    List<Instrument> getDevices();

    @Query("SELECT * FROM devices WHERE EquipID = :eID")
    Instrument getDevice(String eID);

    @Query("SELECT Make, Model, Serial, DLRV, DURV, DUnits FROM devices WHERE EquipID = :eID")
    DevFields getDevFields(String eID);

    @Delete
    void deleteDevice(Instrument... instruments);

}
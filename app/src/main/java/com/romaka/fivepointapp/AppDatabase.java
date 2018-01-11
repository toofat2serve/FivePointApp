package com.romaka.fivepointapp;
import android.arch.persistence.room.*;


@Database(entities = {Instrument.class, CalRecord.class, CalDataSet.class, DataRow.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
   public abstract Instrument_dao instDao();
   public abstract CalRecord_dao crDao();
   public abstract CalDataSet_dao cdsDao();
   public abstract DataRow_dao drDao();
}


/*
Database Visualization

DB
|
+------------+
|            |
Device       CalRecord
   |             |
   (pk)EquipID   (pk)Unique
   Serial        (fk)EquipID
   Make          Date
   Model         Notes
   DLRV          Data
   DURV            |
   DRange          (pk)Unique
   DUnits          Name
   CLRV            Rows
   CURV              |
   CRange            (fk)CalRecord.(pk)
   CUnits            Step
   Steps             Input
   IsLinear          Expected
                     Read   
                     Deviation
					 
					 */

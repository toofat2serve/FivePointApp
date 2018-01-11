package com.romaka.fivepointapp;
import android.arch.persistence.room.*;
import java.util.*;

@Dao
public interface Instrument_dao {
   @Query("SELECT * FROM instrument")
   List<Instrument> getAll();

   @Query("SELECT * FROM instrument WHERE EquipID IN (:equipIds)")
   List<Instrument> loadAllByIds(int[] userIds);

   @Query("SELECT * FROM instrument WHERE EquipID LIKE :equipID LIMIT 1")
   Instrument findByEquipID(String equipID);

   @Insert
   void insertAll(Instrument... instruments);

   @Delete
   void delete(Instrument instrument);
}

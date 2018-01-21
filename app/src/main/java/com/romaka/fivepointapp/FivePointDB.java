package com.romaka.fivepointapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Instrument.class, CalRecord.class}, version = 1)
public abstract class FivePointDB extends RoomDatabase {
    public abstract fpDAO fpdao();
}

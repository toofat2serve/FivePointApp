package com.romaka.fivepointapp;

import android.arch.persistence.room.ColumnInfo;

/**
 * Created by george on 1/13/2018.
 */

public class DevFields {
    @ColumnInfo(name = "Make")
    private String Make;

    @ColumnInfo(name = "Model")
    private String Model;

    @ColumnInfo(name = "Serial")
    private String Serial;

    @ColumnInfo(name = "DLRV")
    private Double LRV;

    @ColumnInfo(name = "DURV")
    private Double URV;

    @ColumnInfo(name = "DUnits")
    private String Units;

    public void setMake(String make) {
        Make = make;
    }

    public void setModel(String model) {
        Model = model;
    }

    public void setSerial(String serial) {
        Serial = serial;
    }

    public void setLRV(Double LRV) {
        this.LRV = LRV;
    }

    public void setURV(Double URV) {
        this.URV = URV;
    }

    public void setUnits(String units) {
        Units = units;
    }

    public String getMake() {
        return Make;
    }

    public String getModel() {
        return Model;
    }

    public String getSerial() {
        return Serial;
    }

    public Double getLRV() {
        return LRV;
    }

    public Double getURV() {
        return URV;
    }

    public String getUnits() {
        return Units;
    }

    @Override
    public String toString() {
        return Make + "\n" + Model + "\n" + Serial + "\n" + LRV.toString() + " - " + URV.toString() + " " + Units;
    }
}

package com.example.ps.geodroidapp.Domain;


import com.example.ps.geodroidapp.Utils.Utils;

public class Discontinuity {

/*
    public enum RoughEnum {VERY_ROUGH, ROUGH, SLIGHTLY_ROUGH, SMOOTH, SLICKENSIDED};
    public enum InfilingEnum {NONE, HARD_FILLING_LESS_THAN_5MM, HARD_FILLING_MORE_THAN_5MM,SOFT_FILLING_LESS_THAN_5MM, SOFT_FILLING_MORE_THAN_5MM}
    public enum WeatheringEnum {UNWEATHERED, SLIGHTLY_WEATHERED, MODERATELY_WEATHERED, HIGLY_WEATHERED, DEDOMPOSED}
*/

    private int id;
    private String idSession;
    private String idUser;
    private int direction;
    private int dip;
    private double latitude;
    private double longitude;
    private int persistence;
    private int aperture;
    private int roughness;
    private int infilling;
    private int weathering;
    private String note;
    private String datetime;
    private int sent;    // possible values 0 and 1


    public Discontinuity(){}
    public Discontinuity(int id, int direction, int dip, double latitude, double longitude, int persistence,
                         int aperture, int roughness, int infilling, int weathering, String note, String datetime, int sent, String idUser, String idSession){
        this.id             = id;
        this.direction      = direction;
        this.dip            = dip;
        this.latitude       = latitude;
        this.longitude      = longitude;
        this.persistence    = persistence;
        this.aperture       = aperture;
        this.roughness      = roughness;
        this.infilling      = infilling;
        this.weathering     = weathering;
        this.note           = note;
        this.datetime       = datetime;
        this.sent           = sent;
        this.idUser         = idUser;
        this.idSession      = idSession;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id;}

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDip() {
        return dip;
    }

    public void setDip(int dip) {
        this.dip = dip;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getPersistence() {
        return persistence;
    }

    public void setPersistence(int persistence) {
        this.persistence = persistence;
    }

    public int getAperture() {
        return aperture;
    }

    public void setAperture(int aperture) {
        this.aperture = aperture;
    }

    public int getRoughness() {
        return roughness;
    }

    public void setRoughness(int roughness) {
        this.roughness = roughness;
    }

    public int getInfilling() {
        return infilling;
    }

    public void setInfilling(int infilling) {
        this.infilling = infilling;
    }

    public int getWeathreing() {
        return weathering;
    }

    public void setWeathreing(int weathreing) {
        this.weathering = weathreing;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {return note;}

    public String getDatetime() {return datetime;}

    public void setDatetime(String datetime) {this.datetime = datetime;}

    public int getSent() {
        return sent;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdSession() {
        return idSession;
    }

    public void setIdSession(String idSession) {
        this.idSession = idSession;
    }

    // Atention: The method toString is used to save file in CSV!
    @Override
    public String toString() {

        return  ""+id +
                "," + idSession +
                "," + idUser +
                "," + direction +
                "," + dip +
                "," + latitude +
                "," + longitude +
                "," + persistence +
                "," + aperture +
                "," + roughness +
                "," + infilling +
                "," + weathering +
                "," + note +
                "," + datetime +
                "\n";
    }
}

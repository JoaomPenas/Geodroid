package com.example.ps.geodroidapp.Domain;

import java.util.ArrayList;

public class DtoDiscontinuity {

    private ArrayList<Discontinuity> discontinuities;

    public DtoDiscontinuity(ArrayList<Discontinuity> discontinuities) {
        this.discontinuities = discontinuities;
    }

    public ArrayList<Discontinuity> getDiscontinuities() {
        return discontinuities;
    }

    public void setDiscontinuities(ArrayList<Discontinuity> discontinuities) {
        this.discontinuities = discontinuities;
    }
}

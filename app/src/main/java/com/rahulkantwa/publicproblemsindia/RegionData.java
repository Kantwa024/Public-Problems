package com.rahulkantwa.publicproblemsindia;

public class RegionData {
    private String state,district,tehsil;

    public RegionData(String state, String district, String tehsil) {
        this.state = state;
        this.district = district;
        this.tehsil = tehsil;
    }

    public RegionData()
    {

    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTehsil() {
        return tehsil;
    }

    public void setTehsil(String tehsil) {
        this.tehsil = tehsil;
    }
}

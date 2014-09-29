package com.android.tuto.ch10.actionbar.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.location.Location;

public class Quake {

    /** The id */
    private long id;
    private Date date;
    private String details;
    private Location location;
    private double magnitude;
    private String link;

    public Quake(Date date, String details, Location location, double magnitude, String link) {
        super();
        this.date = date;
        this.details = details;
        this.location = location;
        this.magnitude = magnitude;
        this.link = link;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * @param details
     *            the details to set
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location
     *            the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @return the magnitude
     */
    public double getMagnitude() {
        return magnitude;
    }

    /**
     * @param magnitude
     *            the magnitude to set
     */
    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    /**
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link
     *            the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(date);
        return dateString + " - M " + magnitude + "\nDetails: " + details;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Quake other = (Quake) obj;
        if (id != other.id)
            return false;
        return true;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(long id) {
        this.id = id;
    }
}

package com.berry.android.piggybank.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Transaction object
 * 
 * @author minhducngo
 *
 */
public class Transaction {
    /** The id */
    private int id;

    /** The date */
    private Date date;

    /** The description */
    private String description;

    /** The money amount */
    private float amout;

    /** The type can be income or expense */
    private String type;
    
    /** The asset id*/
    private int assetId;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the amout
     */
    public float getAmout() {
        return amout;
    }

    /**
     * @param amout
     *            the amout to set
     */
    public void setAmout(float amout) {
        this.amout = amout;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
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
        result = prime * result + id;
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
        Transaction other = (Transaction) obj;
        if (id != other.id)
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getInstance();
        sdf.applyPattern("dd-MM-yyyy");
        String dateString = sdf.format(date);
        return "Transaction [id=" + id + ", date=" + dateString + ", description=" + description + ", amout=" + amout + ", type=" + type + "]";
    }

    /**
     * @return the assetId
     */
    public int getAssetId() {
        return assetId;
    }

    /**
     * @param assetId the assetId to set
     */
    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

}

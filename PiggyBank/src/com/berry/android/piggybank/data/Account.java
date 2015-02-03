package com.berry.android.piggybank.data;

public class Account {
    /** The asset id */
    private int id;

    /** The asset name */
    private String name;

    /** The account's balance */
    private float balance = 0;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

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
     * @return the balance
     */
    public float getBalance() {
        return balance;
    }

    /**
     * @param balance
     *            the balance to set
     */
    public void setBalance(float balance) {
        this.balance = balance;
    }

}

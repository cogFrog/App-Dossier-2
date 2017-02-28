package com.example.neilprajapati.appdosier2;

import com.google.firebase.database.Exclude;

import java.util.Date;

/**
 * Represents a singular money change. For example, a bet you made with your friends
 * which you lost (or won).
 */
public class OneTimeMoneyChange implements Comparable<OneTimeMoneyChange>{
    private double amount; //amount of money change
    private String tag; //category of money change (e.g bet)
    private Date date; //the date which it occured

    /**
     * Default constructor used by firebase
     */
    public OneTimeMoneyChange() {}

    /**
     * The constructor
     * @param amount the amount of money change
     * @param tag the tag
     */
    public OneTimeMoneyChange(double amount, String tag) {
        this.amount = amount;
        this.tag = tag;
        this.date = new Date();
    }

    /**
     * returns the amount of change.
     * @return the amount of change
     */
    public double getAmount() {
        return amount;
    }

    /**
     * returns the tag
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * returns the date which it occured
     * @return the date which it occured.
     */
    public Date getDate() {
        return date;
    }

    //====================================METHODS=======================//


    /**
     * returns a String version of the object
     * @return a String version of the object
     */
    @Override
    public String toString() {
        return tag+": " + amount ;
    }

    /**
     * Compare 2 OneTimeMoneyChange Objects.
     * @param o other object
     * @return the comparison value (+ for this > o, 0 for this == o, - for this < o)
     */
    @Override
    public int compareTo(OneTimeMoneyChange o) {
        return this.date.compareTo(o.date);
    }
}

package com.example.neilprajapati.appdosier2;

import com.google.firebase.database.Exclude;

import java.util.Date;

/**
 * Reperesents period money changes such as taxes and jobs
 */
public class ContinousMoneyChange implements Comparable<ContinousMoneyChange>{
    private double amountChange; //amount of money gained or lost every period. units: dollars
    private double timePeriodOfChange; //the period between each lost. units: seconds
    private String tag; //the category of the money.

    private Date date; //represents data last update



    //=====================CONSTRUCTORS===================//

    /**
     * Default Constructor for firebase to use.
     */
    public ContinousMoneyChange() {
    }

    /**
     * Constructor
     * @param amountChange the amount the balance changes every timePeriodOfChange
     * @param timePeriodOfChange the gap of time between 2 changes. units: seconds :D
     */
    public ContinousMoneyChange(double amountChange, double timePeriodOfChange, String tag) {
        this.amountChange = amountChange;
        this.timePeriodOfChange = timePeriodOfChange;
        this.tag = tag;
        date = new Date();
    }

    //=====================METHODS===================//

    /**
     * returns the time period of change
     * @return the time period of change
     */
    public double getTimePeriodOfChange() {
        return timePeriodOfChange;
    }

    /**
     * returns  the money change that occurs every period.
     * @return the money change
     */
    public double getAmountChange() {
        return amountChange;
    }

    /**
     * returns the tag
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Returns last date when getMoneyChange was called
     * @return last date
     */
    public Date getDate() {
        return date;
    }

    /**
     * For internal use in balance class. Calculates how much money has been earned or lost
     * since last call.
     * @return OneTimeChange representing the paycheck/bill if paycheck/bill comes, otherwise null.
     */
    @Exclude
    public OneTimeMoneyChange getMoneyChange() {
        Date current = new Date();
        double seconds = (current.getTime() - date.getTime())/1000.0; //bc converting mil to base unit
        date.setTime(current.getTime() - (long)(date.getTime()%timePeriodOfChange));
        if(seconds > timePeriodOfChange)
            return new OneTimeMoneyChange(seconds/timePeriodOfChange * amountChange, tag + ".continuous@" + current.getTime());
        return null; //meaning its not ready yet
    }

    /**
     * Compares 2 ContineousMoneyChange Objects
     * @param o other object
     * @return the comparasion value.
     */
    @Override
    public int compareTo(ContinousMoneyChange o) {
        return this.getDate().compareTo(o.getDate());
    }
}

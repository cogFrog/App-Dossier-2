package com.example.neilprajapati.appdosier2;

import com.google.firebase.database.Exclude;

import java.util.Date;

/**
 * Created by neilprajapati on 2/21/17.
 */
public class ContinousMoneyChange implements Comparable<ContinousMoneyChange>{
    private double amountChange; //units: dollars
    private double timePeriodOfChange; //units: seconds
    private String tag;

    private Date date; //represents data last update



    //=====================CONSTRUCTORS===================//

    public ContinousMoneyChange() {
    }

    /**
     *
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

    public double getTimePeriodOfChange() {
        return timePeriodOfChange;
    }

    public double getAmountChange() {
        return amountChange;
    }
    public String getTag() {
        return tag;
    }
    public Date getDate() {
        return date;
    }

    /**
     * For internal use in balance class.
     * @return OneTimeChange represeting the paycheck if paycheck comes, otherwise null.
     */
    @Exclude
    public OneTimeMoneyChange getMoneyChange() {
        Date current = new Date();
        double seconds = (current.getTime() - date.getTime())/1000; //bc converting mil to base unit
        if(seconds > timePeriodOfChange)
            return new OneTimeMoneyChange(seconds/timePeriodOfChange * amountChange, tag + ".continuous@" + current.getTime());
        return null; //meaning its not ready yet
    }

    @Override
    public int compareTo(ContinousMoneyChange o) {
        return this.getDate().compareTo(o.getDate());
    }
}

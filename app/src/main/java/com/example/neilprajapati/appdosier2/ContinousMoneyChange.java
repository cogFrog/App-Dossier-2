package com.example.neilprajapati.appdosier2;

import java.util.Date;

/**
 * Created by neilprajapati on 2/21/17.
 */
public class ContinousMoneyChange {
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
     * @param timePeriodOfChange the gap of time between 2 changes.
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
     *
     * @return OneTimeChange represeting the paycheck if paycheck comes, otherwise null.
     */
    public OneTimeMoneyChange getMoneyChange() {
        Date current = new Date();
        double seconds = (current.getTime() - date.getTime())/1000; //bc converting mil to base unit
        if(seconds > timePeriodOfChange)
            return new OneTimeMoneyChange(amountChange, tag + "@" + current.getTime());
        return null; //meaning its not ready yet
    }
}

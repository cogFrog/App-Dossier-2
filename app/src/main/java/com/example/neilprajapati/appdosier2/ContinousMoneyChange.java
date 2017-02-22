package com.example.neilprajapati.appdosier2;

/**
 * Created by neilprajapati on 2/21/17.
 */
public class ContinousMoneyChange {
    private double amountChange; //units: dollars
    private double timePeriodOfChange; //units: year

    //amount of timePeriodOfChange in ..
    public static final double DAY = 1/365.0;
    public static final double WEEK = 1/52.0;
    public static final double MONTH = 1/12.0;
    public static final double YEAR = 1;


    //=====================CONSTRUCTORS===================//

    public ContinousMoneyChange() {
    }

    public ContinousMoneyChange(double amountChange, double timePeriodOfChange) {
        this.amountChange = amountChange;
        this.timePeriodOfChange = timePeriodOfChange;
    }

    //=====================METHODS===================//

    public double getTimePeriodOfChange() {
        return timePeriodOfChange;
    }

    public double getAmountChange() {
        return amountChange;
    }

    /**
     *
     * @param time time elapsed in years
     * @return money change
     */
    public double getMoneyChange(double time){
        return time * amountChange / timePeriodOfChange;
    }
}

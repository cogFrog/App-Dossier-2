package com.example.neilprajapati.appdosier2;

import com.google.firebase.database.Exclude;

import java.util.Date;

/**
 * Created by neilprajapati on 2/21/17.
 */
public class OneTimeMoneyChange {
    private double amount;
    private String tag;
    private Date date;

    public OneTimeMoneyChange() {}

    public OneTimeMoneyChange(double amount, String tag) {
        this.amount = amount;
        this.tag = tag;
        this.date = new Date();
    }

    public double getAmount() {
        return amount;
    }

    public String getTag() {
        return tag;
    }


    //====================================METHODS=======================//
    @Exclude

    public boolean isLoss(){
        return amount < 0;
    }

    @Exclude
    public boolean isGain(){
        return amount > 0;
    }
}

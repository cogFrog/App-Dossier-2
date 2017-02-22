package com.example.neilprajapati.appdosier2;

/**
 * Created by neilprajapati on 2/21/17.
 */
public class OneTimeMoneyChange {
    private double amount;
    private String tag;

    public OneTimeMoneyChange() {}

    public OneTimeMoneyChange(double amount, String tag) {
        this.amount = amount;
        this.tag = tag;
    }

    public double getAmount() {
        return amount;
    }

    public String getTag() {
        return tag;
    }


    //====================================METHODS=======================//
    public boolean isLoss(){
        return amount < 0;
    }
    public boolean isGain(){
        return amount > 0;
    }
}

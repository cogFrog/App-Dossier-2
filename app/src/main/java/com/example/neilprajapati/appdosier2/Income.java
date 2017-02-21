package com.example.neilprajapati.appdosier2;

import java.util.HashMap;

/**
 * Created by neilprajapati on 2/21/17.
 */
public class Income {
    private HashMap<Long, Double> bonuses;
    private double wage; //per month

    public Income(){}

    public Income(HashMap<Long, Double> bonuses, double wage) {
        this.bonuses = bonuses;
        this.wage = wage;
    }

}

package com.example.neilprajapati.appdosier2;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the money and the history
 */
public class Balance {
    private double amt; //the amount of money currently in the balance
    private List<ContinousMoneyChange> continousMoneyChanges; //the active contineous money changes applied on the balance
    private List<OneTimeMoneyChange> oneTimeMoneyChanges; //simply logs these for future uses

    //used by Firebase
    //==================================CONSTRUCTORS========================//

    /**
     * Default Constructor so Firebase can initialize the obj.
     */
    public Balance() {
    }

    /**
     * Constructs a Balance object
     *
     * @param amt amount of money in the balance
     * @param continousMoneyChanges the list of active contineous money changes.
     * @param oneTimeMoneyChanges the history of oneTime Money Changes.
     */
    public Balance(double amt, List<ContinousMoneyChange> continousMoneyChanges, List<OneTimeMoneyChange> oneTimeMoneyChanges) {
        this.amt = amt;
        this.continousMoneyChanges = continousMoneyChanges;
        this.oneTimeMoneyChanges = oneTimeMoneyChanges;
    }

    /**
     * Creates a Balance object with no history of oneTime Money Changes or contineous money changes
     * @param amt the current balance
     */
    public Balance(double amt) {
        this.amt = amt;
        this.continousMoneyChanges = new ArrayList<>();
        this.oneTimeMoneyChanges = new ArrayList<>();

    }
    //==================================GETTERS========================//

    /**
     * updates amt and oneTimeMoneyChanges by applying the contineousMoneyChanges.
     */
    public void calculateNewBalance(){
        for(ContinousMoneyChange change: continousMoneyChanges)
        {
            OneTimeMoneyChange p = change.getMoneyChange();
            if(p != null) add(p);
        }
    }

    /**
     * updates and returns the current balance.
     * @return amt
     */
    public double getAmt() {
        calculateNewBalance();
        return amt;
    }

    /**
     * gets the contineous money changes
     * @return contineousMoneyChanges list
     */
    public List<ContinousMoneyChange> getContinousMoneyChanges() {
        return continousMoneyChanges;
    }

    /**
     * gets the one time money changes
     * @return the oneTimeMoneyChanges list
     */
    public List<OneTimeMoneyChange> getOneTimeMoneyChanges() {
        return oneTimeMoneyChanges;
    }

    //==================================Methods========================//

    /**
     * Registers a new oneTimeMoneyChange and adds it to the amt.
     * @param change the oneTimeMoneyChange to be registered
     */
    public void add(OneTimeMoneyChange change){
        amt += change.getAmount();
        oneTimeMoneyChanges.add(change);
        Collections.sort(oneTimeMoneyChanges);
    }

    /**
     * adds a contineousMoneyChange to balance
     * @param change the contineousMoneyChange to add.
     */
    public void add(ContinousMoneyChange change){
        continousMoneyChanges.add(change);
        Collections.sort(continousMoneyChanges);
    }


    /**
     * Makes sure that none of the fields are null. Null fields occur when the database
     * has no node for the variables. This can cause null pointer exception.
     */
    public void cleanFields(){
        if(continousMoneyChanges == null) continousMoneyChanges = new ArrayList<>();
        if(oneTimeMoneyChanges == null) oneTimeMoneyChanges = new ArrayList<>();

    }
    //==================================STATS METHODS========================//




}

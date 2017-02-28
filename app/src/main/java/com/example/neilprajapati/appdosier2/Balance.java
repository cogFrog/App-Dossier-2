package com.example.neilprajapati.appdosier2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the money and the history
 */
public class Balance {
    private double amt; //the amount of money currently in the balance
    private List<ContinuousMoneyChange> continuousMoneyChanges; //the active contineous money changes applied on the balance
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
     * @param continuousMoneyChanges the list of active continuous money changes.
     * @param oneTimeMoneyChanges the history of oneTime Money Changes.
     */
    public Balance(double amt, List<ContinuousMoneyChange> continuousMoneyChanges, List<OneTimeMoneyChange> oneTimeMoneyChanges) {
        this.amt = amt;
        this.continuousMoneyChanges = continuousMoneyChanges;
        this.oneTimeMoneyChanges = oneTimeMoneyChanges;
    }

    /**
     * Creates a Balance object with no history of oneTime Money Changes or continuous money changes
     * @param amt the current balance
     */
    public Balance(double amt) {
        this.amt = amt;
        this.continuousMoneyChanges = new ArrayList<>();
        this.oneTimeMoneyChanges = new ArrayList<>();

    }
    //==================================GETTERS========================//

    /**
     * updates amt and oneTimeMoneyChanges by applying the continuousMoneyChanges.
     */
    public void calculateNewBalance(){
        for(ContinuousMoneyChange change: continuousMoneyChanges)
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
     * gets the continuous money changes
     * @return continuousMoneyChanges list
     */
    public List<ContinuousMoneyChange> getContinuousMoneyChanges() {
        return continuousMoneyChanges;
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
     * adds a continuousMoneyChange to balance
     * @param change the continuousMoneyChange to add.
     */
    public void add(ContinuousMoneyChange change){
        continuousMoneyChanges.add(change);
        Collections.sort(continuousMoneyChanges);
    }


    /**
     * Makes sure that none of the fields are null. Null fields occur when the database
     * has no node for the variables. This can cause null pointer exception.
     */
    public void cleanFields(){
        if(continuousMoneyChanges == null) continuousMoneyChanges = new ArrayList<>();
        if(oneTimeMoneyChanges == null) oneTimeMoneyChanges = new ArrayList<>();

    }
    //==================================STATS METHODS========================//




}

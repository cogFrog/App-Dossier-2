package com.example.neilprajapati.appdosier2;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO:
 *  update method.
 *  statistics methods on what ur spending a lot of etc
 *  yeah.
 */
public class Balance {
    private double amt;
    private List<ContinousMoneyChange> continousMoneyChanges;
    private List<OneTimeMoneyChange> oneTimeMoneyChanges; //simply logs these for future uses

    //used by Firebase
    //==================================CONSTRUCTORS========================//
    public Balance() {
    }

    public Balance(double amt, List<ContinousMoneyChange> continousMoneyChanges, List<OneTimeMoneyChange> oneTimeMoneyChanges) {
        this.amt = amt;
        this.continousMoneyChanges = continousMoneyChanges;
        this.oneTimeMoneyChanges = oneTimeMoneyChanges;
    }

    public Balance(double amt) {
        this.amt = amt;
        this.continousMoneyChanges = new ArrayList<>();
        this.oneTimeMoneyChanges = new ArrayList<>();

    }
    //==================================GETTERS========================//

    public void calculateNewBalance(){
        for(ContinousMoneyChange change: continousMoneyChanges)
        {
            OneTimeMoneyChange p = change.getMoneyChange();
            if(p != null) add(p);
        }
    }

    public double getAmt() {
        calculateNewBalance();
        return amt;
    }

    public List<ContinousMoneyChange> getContinousMoneyChanges() {
        return continousMoneyChanges;
    }

    public List<OneTimeMoneyChange> getOneTimeMoneyChanges() {
        return oneTimeMoneyChanges;
    }

    //==================================Methods========================//
    public void add(OneTimeMoneyChange change){
        amt += change.getAmount();
        oneTimeMoneyChanges.add(change);
        Collections.sort(oneTimeMoneyChanges);
    }
    public void add(ContinousMoneyChange change){
        continousMoneyChanges.add(change);
        Collections.sort(continousMoneyChanges);
    }

    public void cleanFields(){
        if(continousMoneyChanges == null) continousMoneyChanges = new ArrayList<>();
        if(oneTimeMoneyChanges == null) oneTimeMoneyChanges = new ArrayList<>();

    }
    //==================================STATS METHODS========================//




}

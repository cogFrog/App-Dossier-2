package com.example.neilprajapati.appdosier2;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO:
 *  update method.
 *  statistics methods on what ur spending alot of etc
 *  yeah.
 */
public class Balance {
    private double amt;
    private List<ContinousMoneyChange> continousMoneyChanges;
    private List<OneTimeMoneyChange> oneTimeMoneyChanges; //simply logs these for future uses

    //used by Firebase
    //==================================CONSTRUCTORS========================//
    public Balance() {}

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

    public double getAmt() {
        return amt;
    }

    public List<ContinousMoneyChange> getContinousMoneyChanges() {
        return continousMoneyChanges;
    }

    public List<OneTimeMoneyChange> getOneTimeMoneyChanges() {
        return oneTimeMoneyChanges;
    }

    //==================================STATS METHODS========================//




}

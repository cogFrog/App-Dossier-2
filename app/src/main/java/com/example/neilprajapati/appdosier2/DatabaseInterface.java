package com.example.neilprajapati.appdosier2;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON layout
 *  root
 *      -users
 *          -uid
 *              balance
 *                  obj:
 *                      -cont:
 *                          0: obj0
 *                          1: obj1
 *                          ...
 *                      -onetime:
 *                          0: obj0
 *                          1: obj1
 *                          ...
 *                          9: obj9
 *                      -amt
 *
 *               tags
*                    obj:
 *                      0: grocery
 *                      1: ...
 *                      ....
 *              history
 *                  0:
 *                      0: obj0
 *                      1: obj1
 *                      ...
 *                      9: obj9
 *                  1:
 *                      0: obj0
 *                      1: obj1
 *                      ...
 *                      9: obj9
 *                  ...
 *
 */
public final class DatabaseInterface {
    private static DatabaseInterface databseInstance;


    //fire base
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;
    private String mUserId;

    //check if firebase is ready
    private boolean tagsReady = false, balanceReady = false, historyReady = false; //historyRead will reset to false when a request is made to load more history

    //balance
    private Balance balance;
    public static final int MAX_ONE_TIME_STORED = 10; //how many one time $$ changes are stored in balance

    //tags
    private List<String> tags;

    private DatabaseInterface(){
        try {
            mFirebaseAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            if(mFirebaseUser == null) return;

            mUserId = mFirebaseUser.getUid();

        } catch(Exception e){
            return;
        }
        initializeBalanceAndTags();
    }

    private void initializeBalanceAndTags(){
        balance = new Balance(0, new ArrayList<ContinousMoneyChange>(), new ArrayList<OneTimeMoneyChange>());
        tags = new ArrayList<>();

        //retrieve from db. If there is nothing in db, it won't be called
        //when something is added, itll overwrite the balance and tag objs created above.
        mDatabase.child("users").child(mUserId).child("init").child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Balance tmp = dataSnapshot.getValue(Balance.class);
                //balance = tmp!=null?tmp:balance;
                mDatabase.child("users").child(mUserId).child("balance").child("obj").addChildEventListener(new BalanceListener());
                balanceReady = true;
            }

            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Canceled");
            }
        });

        //retrieve tags
        mDatabase.child("users").child(mUserId).child("tags").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                tags = dataSnapshot.getValue(t);
                tagsReady = true;
            }

            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //==================================Interfacing with Databse Methods========================//


    public boolean isLogined(){
        return mUserId != null;
    }

    public String getUserName(){
        if(mUserId == null) throw new IllegalStateException("User not signed in");
        return mFirebaseUser.getDisplayName();
    }

    //TODO
    public List<OneTimeMoneyChange> getRecentOneTimeMoneyChanges(int amtOfChanges){

        if(!balanceReady || !isLogined()) throw new IllegalStateException("Balance was not ready or user isn't loggined in");
        int amtCached = balance.getContinousMoneyChanges().size();


        if(amtOfChanges > amtCached){

            //intitialize
            int startIndex, endIndex;
            startIndex = amtCached/ MAX_ONE_TIME_STORED;

            if(amtCached != -1){
                endIndex = (amtOfChanges - amtCached)/ MAX_ONE_TIME_STORED + startIndex;
            } else{
                endIndex = Integer.MAX_VALUE; //just a really big value.
            }


        }else {
            List<OneTimeMoneyChange> l = balance.getOneTimeMoneyChanges();
            return l.subList(l.size() - amtOfChanges - 1, l.size() - 1);
        }
        return null;
    }

    public void appendMoneyChange(ContinousMoneyChange change) {
        if(!balanceReady || !isLogined()) throw new IllegalStateException("Balance was not ready or user isn't loggined in");
        mDatabase.child("users").child(mUserId).child("balance").child("obj").child("continousMoneyChanges").child(balance.getContinousMoneyChanges().size() + "").setValue(change);
        balance.getContinousMoneyChanges().add(change);
        if(!tags.contains(change.getTag())){
            //now we have to add it to the tags list
            //notice that we know the index of
            mDatabase.child("users").child(mUserId).child("tags").child(tags.size() + "").setValue(change.getTag());
            tags.add(change.getTag());
        }
    }

    public void appendMoneyChange(OneTimeMoneyChange change){
        if(!balanceReady || !isLogined()) throw new IllegalStateException("Balance was not ready or user wasn't loggined in");

        if(balance.getOneTimeMoneyChanges().size() > MAX_ONE_TIME_STORED){
            //initialize vars
            int index = balance.getContinousMoneyChanges().size()/ MAX_ONE_TIME_STORED - 1; //bc integer div, its work :D
            int localIndex = balance.getContinousMoneyChanges().size() % MAX_ONE_TIME_STORED - 1; //bc yes
            mDatabase.child("users").child(mUserId).child("history").child(""+index).child(localIndex + "").setValue(change);

        }else {
            //just add it to balance obj
            mDatabase.child("users").child(mUserId).child("balance").child("obj").child("oneTimeMoneyChanges").child(balance.getOneTimeMoneyChanges().size() + "").setValue(change);
        }
        balance.getOneTimeMoneyChanges().add(change);

        if(!tags.contains(change.getTag())){
            //now we have to add it to the tags list
            //notice that we know the index of
            mDatabase.child("users").child(mUserId).child("tags").child(tags.size() + "").setValue(change.getTag());
            tags.add(change.getTag());
        }
    }

    public boolean isTagsReady() {
        return tagsReady;
    }

    public boolean isBalanceReady() {
        return balanceReady;
    }

    public boolean isHistoryReady() {
        return historyReady;
    }

    //==================================FACTORY METHOD========================//

    @NonNull
    public static DatabaseInterface getDatabseInstance(){
        if(databseInstance != null) return databseInstance;
        databseInstance = new DatabaseInterface();
        return databseInstance;
    }


    //==================================INNER CLASSES========================//
    /**
     * Will never be called before the initilization value event occurs.
     */
    private class BalanceListener implements ChildEventListener{
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            System.out.println("Inside BalanceListener" + dataSnapshot);
            System.out.println("Inside BalanceListener" + dataSnapshot.getKey());
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}

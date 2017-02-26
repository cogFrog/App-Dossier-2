package com.example.neilprajapati.appdosier2;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
 *                      -amt
 *
 */
public final class Database {
    private static Database databaseInstance;


    //fire base
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;
    private String mUserId;

    //check if firebase is ready
    private boolean tagsReady = false, balanceReady = false;//, historyReady = false; //historyRead will reset to false when a request is made to load more history

    //balance
    private Balance balance;
    //public static final int MAX_ONE_TIME_STORED = 10; //how many one time $$ changes are stored in balance

    //tags
    private List<String> tags;

    private Database(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(mFirebaseUser == null) return;
        mUserId = mFirebaseUser.getUid();

        initializeBalanceAndTags();
    }

    private void initializeBalanceAndTags(){
        balance = new Balance(0, new ArrayList<ContinousMoneyChange>(), new ArrayList<OneTimeMoneyChange>());
        tags = new ArrayList<>();

        //retrieve from db. If there is nothing in db, it won't be called
        //when something is added, itll overwrite the balance and tag objs created above.
        mDatabase.child("users").child(mUserId).child("balance").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue() == null) return;
                Balance tmp = dataSnapshot.getValue(Balance.class);
                System.out.println("Inside: "+tmp.getOneTimeMoneyChanges() + ", " + tmp.getContinousMoneyChanges());
                balance = tmp;
                balance.cleanFields();
                balanceReady = true;
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
        });

        //retrieve tags
        mDatabase.child("users").child(mUserId).child("tags").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                tags.add( (String) dataSnapshot.getValue());
                tagsReady = true;
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
        });
    }

    //==================================Interfacing with Database Methods========================//


    public boolean isLogined(){
        return mUserId != null;
    }

    public void signIn(final String user, final String password, final SuccessListener listener){
        mFirebaseAuth.signInWithEmailAndPassword(user, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mFirebaseUser = mFirebaseAuth.getCurrentUser();
                    mUserId = mFirebaseUser.getUid();
                    initializeBalanceAndTags();
                    listener.onSuccess();
                } else {

                    listener.onFailure();
                }
            }
        });

    }


    public List<OneTimeMoneyChange> getRecentOneTimeMoneyChanges(int amtOfChanges) throws InterruptedException {
        checkInited();

        List<OneTimeMoneyChange> l = balance.getOneTimeMoneyChanges();
        Collections.sort(l);
        return l.subList(Math.max(0,l.size() - amtOfChanges - 1), l.size() - 1);
    }


    public void appendMoneyChange(ContinousMoneyChange change) {
        checkInited();

        balance.add(change);
        mDatabase.child("users").child(mUserId).child("balance").child("obj").setValue(balance);

        if(!tags.contains(change.getTag())){
            //now we have to add it to the tags list
            //notice that we know the index of
            mDatabase.child("users").child(mUserId).child("tags").child(tags.size() + "").setValue(change.getTag());
            tags.add(change.getTag());
            Collections.sort(tags);
        }
    }

    public void appendMoneyChange(OneTimeMoneyChange change){
        if(!balanceReady || !isLogined()) throw new IllegalStateException("Balance was not ready or user wasn't loggined in");

        balance.add(change);
        mDatabase.child("users").child(mUserId).child("balance").child("obj").setValue(balance);


        if(!tags.contains(change.getTag())){
            //now we have to add it to the tags list
            //notice that we know the index of
            mDatabase.child("users").child(mUserId).child("tags").child(tags.size() + "").setValue(change.getTag());
            tags.add(change.getTag());
            Collections.sort(tags);
        }
    }

    public double getBalance(){
        checkInited();



        return balance.getAmt();
    }

    public List<ContinousMoneyChange> getContinousMoneyChanges(){
        checkInited();
        return balance.getContinousMoneyChanges();
    }

    public List<String> searchTags(String fragment){
        ArrayList<String> list = new ArrayList<>();
        for(String str: tags)
            if(str.contains(fragment))
                list.add(str);
        return list;
    }

    public void deleteChange(Date date){
        checkInited();

        //super cancer ineefficient but screw dis
        for (int i = 0; i < balance.getContinousMoneyChanges().size(); i++) {
            ContinousMoneyChange change = balance.getContinousMoneyChanges().get(i);
            if (change.getDate().equals(date)) {
                balance.getContinousMoneyChanges().remove(i);
                mDatabase.child("users").child(mUserId).child("balance").child("obj").setValue(balance);
                return;
            }
        }
        for (int i = 0; i < balance.getOneTimeMoneyChanges().size(); i++) {
            OneTimeMoneyChange change = balance.getOneTimeMoneyChanges().get(i);
            if (change.getDate().equals(date)) {
                balance.getOneTimeMoneyChanges().remove(i);
                mDatabase.child("users").child(mUserId).child("balance").child("obj").setValue(balance);
                return;
            }
        }
    }

    public void editOneTimeMoneyChange(Date date, double newAmt, String newTag){
        checkInited();
        for (int i = 0; i < balance.getOneTimeMoneyChanges().size(); i++) {
            OneTimeMoneyChange change = balance.getOneTimeMoneyChanges().get(i);
            if (change.getDate().equals(date)) {
                balance.getOneTimeMoneyChanges().set(i, new OneTimeMoneyChange(newAmt, newTag));
                mDatabase.child("users").child(mUserId).child("balance").child("obj").setValue(balance);
            }
        }
    }

    public void editContineousMoneyChange(Date date, double newAmt, double newTimePeriodOfChange, String newTag){
        checkInited();
        for (int i = 0; i < balance.getContinousMoneyChanges().size(); i++) {
            ContinousMoneyChange change = balance.getContinousMoneyChanges().get(i);
            if (change.getDate().equals(date)) {
                balance.getContinousMoneyChanges().set(i, new ContinousMoneyChange(newAmt, newTimePeriodOfChange, newTag));
                mDatabase.child("users").child(mUserId).child("balance").child("obj").setValue(balance);
                return;
            }
        }
    }

    //==================================Checking if ready========================//

    private void checkInited() {
        if(!balanceReady || !isLogined()) throw new IllegalStateException("Balance was not ready or user isn't loggined in");
    }


    public boolean isTagsReady() {
        return tagsReady;
    }

    //    public boolean isHistoryReady() {
    //        return historyReady;
    //    }

    public boolean isBalanceReady() {
        return balanceReady;
    }


    //==================================FACTORY METHOD========================//

    @NonNull
    public static Database getDatabaseInstance(){
        if(databaseInstance != null) return databaseInstance;
        databaseInstance = new Database();
        return databaseInstance;
    }


    //==================================INNER CLASSES========================//
    public interface SuccessListener {
        void onSuccess();
        void onFailure();
    }


    //==================================OLD CODE using history========================//

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
     *
     */
    /*

    @TargetApi(Build.VERSION_CODES.N)
    public List<OneTimeMoneyChange> getRecentOneTimeMoneyChanges(int amtOfChanges) throws InterruptedException {

        if(!balanceReady || !isLogined()) throw new IllegalStateException("Balance was not ready or user isn't loggined in");
        int amtCached = balance.getOneTimeMoneyChanges().size();

        if(amtOfChanges > amtCached){

            //initialize
            final int startIndex, endIndex;
            startIndex = amtCached/ MAX_ONE_TIME_STORED - 1;

            if(amtCached != -1){
                endIndex = (amtOfChanges - amtCached)/ MAX_ONE_TIME_STORED + startIndex;
            } else{
                endIndex = Integer.MAX_VALUE; //just a really big value.
            }

            System.out.println(startIndex + ":"+ endIndex);
            //thread to populate
            Thread t = new Thread(){
                @Override
                public void run(){
                    for (int i = startIndex; i <= endIndex; i++) {
                        mDatabase.child("users").child(mUserId).child("history").child(""+i).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                System.out.println("hehe");
                                balance.getOneTimeMoneyChanges().add( dataSnapshot.getValue(OneTimeMoneyChange.class));

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
                        });

                    }

                }
            };
            t.start();
            t.join();
            System.out.println("thread state "+ t.isAlive());

            Collections.sort(balance.getOneTimeMoneyChanges());

            System.out.println(":"+balance.getOneTimeMoneyChanges().size());

            return balance.getOneTimeMoneyChanges().subList(0, amtOfChanges); //subtract 1 bc 0th index is 1
        }else {
            List<OneTimeMoneyChange> l = balance.getOneTimeMoneyChanges();
            Collections.sort(l);
            l = l.subList(l.size() - amtOfChanges - 1, l.size() - 1);
            return l;
        }
    }

    public void appendMoneyChange(ContinousMoneyChange change) {
        if(!balanceReady || !isLogined()) throw new IllegalStateException("Balance was not ready or user isn't loggined in");

        balance.getContinousMoneyChanges().add(change);
        mDatabase.child("users").child(mUserId).child("balance").child("obj").child("continousMoneyChanges").setValue(balance.getContinousMoneyChanges());


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
            int index = balance.getOneTimeMoneyChanges().size()/ MAX_ONE_TIME_STORED - 1; //bc integer div, its work :D
            int localIndex = balance.getOneTimeMoneyChanges().size() % MAX_ONE_TIME_STORED; //bc yes
            mDatabase.child("users").child(mUserId).child("history").child(""+index).child(localIndex + "").setValue(change);
            balance.getOneTimeMoneyChanges().add(change);
        }else {
            //just add it to balance obj
            balance.getOneTimeMoneyChanges().add(change);
            mDatabase.child("users").child(mUserId).child("balance").child("obj").setValue(balance);

        }


        if(!tags.contains(change.getTag())){
            //now we have to add it to the tags list
            //notice that we know the index of
            mDatabase.child("users").child(mUserId).child("tags").child(tags.size() + "").setValue(change.getTag());
            tags.add(change.getTag());
        }
    }
     */

}

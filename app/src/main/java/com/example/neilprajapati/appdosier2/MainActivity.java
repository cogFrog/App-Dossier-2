package com.example.neilprajapati.appdosier2;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private List<String> tags = new ArrayList<>(); //This is the list that would contain the tags.

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;
    private String mUserId;
    private Balance balance;

    private Queue<OneTimeMoneyChange> pendingOneTimeReqs;
    private Queue<String> pendingTags;

    private ArrayAdapter<String> adapter;
    private AutoCompleteTextView textView;
    private TextView moneyRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        System.out.println("starting app");

        if(mFirebaseUser == null){
            System.out.println("user not loginned in");
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else{
            mUserId = mFirebaseUser.getUid();


            quoteSetup();


            setUpTags();
            setUpBalance();


            adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tags);
            textView = (AutoCompleteTextView) findViewById(R.id.tagValue);
            textView.setAdapter(adapter); // links the set of tags to the autocomplete text view

            moneyRemaining = (TextView) findViewById(R.id.moneyRemaining);


            final EditText moneyChangeAmount = (EditText) findViewById(R.id.moneyChangeValue);
            final TextView unitQuantity = (TextView) findViewById(R.id.unitCount);

            final Button addExpenseButton = (Button) findViewById(R.id.addExpense);
            addExpenseButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    System.out.println("INside tags: "+tags);
                    if(moneyChangeAmount.getText().toString().matches("[-+]?\\d*\\.?\\d+") && !textView.getText().toString().equals(""))
                        appendMoneyChange(
                                new OneTimeMoneyChange(
                                        -Integer.parseInt(unitQuantity.getText().toString())*Double.parseDouble(moneyChangeAmount.getText().toString()),
                                        textView.getText().toString()
                                )
                        );

                }
            });

            final Button addIncomeButton = (Button) findViewById(R.id.addIncome);
            addIncomeButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    System.out.println("INside tags: "+tags);
                    if(moneyChangeAmount.getText().toString().matches("[-+]?\\d*\\.?\\d+") && !textView.getText().toString().equals(""))

                        appendMoneyChange(
                                new OneTimeMoneyChange(
                                        +Integer.parseInt(unitQuantity.getText().toString())*Double.parseDouble(moneyChangeAmount.getText().toString()),
                                        textView.getText().toString()
                                )
                        );

                }
            });

            final Button logoutButton = (Button) findViewById(R.id.logoutButton);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFirebaseAuth.signOut();
                    Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });

        }


    }

    private void setUpBalance() {
        pendingOneTimeReqs = new LinkedList<>();
        mDatabase.child("users").child(mUserId).child("balance").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue() == null) return;
                balance = dataSnapshot.getValue(Balance.class);
                balance.cleanFields();
                while(pendingOneTimeReqs.size() > 0){
                    balance.add(pendingOneTimeReqs.poll());
                }
                moneyRemaining.setText("$"+ balance.getAmt() +" remaining from your average income");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                balance = dataSnapshot.getValue(Balance.class);
                balance.cleanFields();
                moneyRemaining.setText("$"+ balance.getAmt() +" remaining from your average income");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Inside: Canceled");
            }
        });
    }

    private void setUpTags() {
        pendingTags = new LinkedList<>();


        mDatabase.child("users").child(mUserId).child("tags").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(pendingTags.size() > 0) {
                    while (pendingTags.size() > 0) {
                        String tagInQueue = pendingTags.poll();
                        if (!tags.contains(tagInQueue))
                            tags.add(tagInQueue);
                    }
                    Collections.sort(tags);
                }
                tags.add((String) dataSnapshot.getValue());
                adapter.add((String) dataSnapshot.getValue());
                adapter.notifyDataSetChanged();
                textView.setAdapter(adapter);
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

    private void quoteSetup() {
        Random rand = new Random();

        Resources res = getResources();
        String[] quotes = res.getStringArray(R.array.quotes);

        TextView quoteBox = (TextView) findViewById(R.id.quoteBox);
        int randomValue = rand.nextInt(quotes.length);
        quoteBox.setText(quotes[randomValue]);
    }

    public void addUnit(View v) {
        TextView unitQuantity = (TextView) findViewById(R.id.unitCount);
        int currentValue = Integer.parseInt(unitQuantity.getText().toString());

        currentValue++;

        unitQuantity.setText(currentValue + "");
    }

    public void removeUnit(View v) {
        TextView unitQuantity = (TextView) findViewById(R.id.unitCount);
        int currentValue = Integer.parseInt(unitQuantity.getText().toString());

        if (currentValue > 1) {
            currentValue--;
        }

        unitQuantity.setText(currentValue + "");
    }

    public void appendMoneyChange(OneTimeMoneyChange change){
        if(balance == null){
            pendingOneTimeReqs.add(change);
            pendingTags.add(change.getTag());
            return;
        }
        balance.add(change);
        mDatabase.child("users").child(mUserId).child("balance").child("obj").setValue(balance);
        if(!tags.contains(change.getTag())){
            mDatabase.child("users").child(mUserId).child("tags").push().setValue(change.getTag());
            //that is handled by the child listener
//            tags.add(change.getTag());
//            Collections.sort(tags);
        }
    }
}
package com.example.inclass06;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements ContactsFragment.ContactsListener,
        CreateContactFragment.CreateContactListener {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.rootView, new ContactsFragment(), "ContactsFragment")
                .commit();
    }

    // ContactsFragment Interface
    @Override
    public void gotoAddContact() {
        fragmentManager.beginTransaction()
                .replace(R.id.rootView, new CreateContactFragment(), "CreateContactFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoContactDetails(Contact contact) {
        fragmentManager.beginTransaction()
                .replace(R.id.rootView, DetailsFragment.newInstance(contact))
                .addToBackStack(null)
                .commit();
    }

    // CreateContactFragment Interface
    @Override
    public void cancelCreateContact() {
        fragmentManager.popBackStack();
    }

    @Override
    public void doneCreateContact() {
        fragmentManager.popBackStack();
    }
}
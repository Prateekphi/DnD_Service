package com.example.dndservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.dndservice.data.MyDbHandler;
import com.example.dndservice.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class Records extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        MyDbHandler db = new MyDbHandler(Records.this);//00
        /*
        // Creating a contact for the db
        Contact harry = new Contact();
        harry.setPhoneNumber("9090909090");
        harry.setName("Harry");

        // Adding a contact to the db
        db.addContact(harry);
        //Log.d("dbharry","Id for harry successfully added to the db");
        */

        //db.addContact("test","1910","01/01/2001","01/04/2001");

        /*
        harry.setId(1);
        harry.setName("Changed Harry");
        harry.setPhoneNumber("121212");
        int affectedRows = db.updateContact(harry);
        Log.d("dbharry","No of affected rows are: " + affectedRows);
        */

        releaseDataToUser(db);
        db.close();
    }

    public void back(View view) {
        onBackPressed();
    }

    public void home(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void releaseDataToUser(MyDbHandler db){
        List<Contact> allContacts = db.getAllContacts();
        ArrayList<String> contacts = new ArrayList();

        for(Contact contact: allContacts){
            //Log.d("dbharry","Id: " + contact.getId() + "\n" + "Name: " + contact.getName() + "\n" + "Phone Number: " + contact.getPhoneNumber());
            contacts.add(contact.getName() +
                    "\n \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t- " +
                    contact.getDateReport());
        }


        listView = findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contacts);//contacts
        listView.setAdapter(arrayAdapter);
    }
}
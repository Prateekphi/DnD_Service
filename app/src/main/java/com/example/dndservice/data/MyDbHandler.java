package com.example.dndservice.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.dndservice.model.Contact;
import com.example.dndservice.params.Params;

import java.util.ArrayList;
import java.util.List;

public class MyDbHandler extends SQLiteOpenHelper {

    public MyDbHandler(Context context) {
        super(context, Params.DB_NAME, null, Params.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + Params.TABLE_NAME + "("
                + Params.KEY_ID + " INTEGER PRIMARY KEY,"
                + Params.KEY_NAME + " TEXT, "
                + Params.KEY_STATUS + " TEXT, "
                + Params.KEY_DATE_REC + " TEXT, "
                + Params.KEY_DATE_REPORT + " TEXT"
                + ")";
        Log.d("phi", "Query being run is : "+ create);
        db.execSQL(create);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addContact(Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Params.KEY_NAME, contact.getName());
        values.put(Params.KEY_STATUS, contact.getStatus());
        values.put(Params.KEY_DATE_REC, contact.getDateRec());
        values.put(Params.KEY_DATE_REPORT, contact.getDateReport());
        db.insert(Params.TABLE_NAME, null, values);
        Log.d("phi", "Successfully inserted");
        db.close();
    }

    public void addContact(String name, String status, String dateRec, String dateReport){
        SQLiteDatabase db = this.getWritableDatabase();
        // Adding a contact to the db
        ContentValues values = new ContentValues();
        values.put(Params.KEY_NAME, name);
        values.put(Params.KEY_STATUS, status);
        values.put(Params.KEY_DATE_REC, dateRec);
        values.put(Params.KEY_DATE_REPORT, dateReport);

        db.insert(Params.TABLE_NAME, null, values);
        db.close();
    }

    public List<Contact> getAllContacts(){
        List<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Generate the query to read from the database
        String select = "SELECT * FROM " + Params.TABLE_NAME;
        Cursor cursor = db.rawQuery(select, null);

        // Loop through now
        //00 Edited : cursor ko reverse me chalaya
        if(cursor.moveToLast()){
            do{
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setStatus(cursor.getString(2));
                contact.setDateRec(cursor.getString(3));
                contact.setDateReport(cursor.getString(4));
                contactList.add(contact);
            }while(cursor.moveToPrevious());
        }
        return contactList;
    }

    public int updateContact(Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Params.KEY_NAME, contact.getName());
        values.put(Params.KEY_STATUS, contact.getStatus());
        values.put(Params.KEY_DATE_REC, contact.getDateRec());
        values.put(Params.KEY_DATE_REPORT, contact.getDateReport());
        //Lets update now
        return db.update(Params.TABLE_NAME, values, Params.KEY_ID + "=?",
                new String[]{String.valueOf(contact.getId())});
    }

    public void deleteContactById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Params.TABLE_NAME, Params.KEY_ID +"=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteContact(Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Params.TABLE_NAME, Params.KEY_ID +"=?", new String[]{String.valueOf(contact.getId())});
        db.close();
    }

    public boolean CheckIsDataAlreadyInDBorNot(String fieldValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + Params.TABLE_NAME + " where " + Params.KEY_NAME + " = \"" + fieldValue + "\";";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}

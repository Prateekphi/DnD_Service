package com.example.dndservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dndservice.data.MyDbHandler;
import com.google.android.material.color.MaterialColors;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CallActivity extends AppCompatActivity {
    private TextView textView2;
    EditText text_description2;
    SimpleDateFormat formatter;
    String pNumber, strDate , s1 , s2;
    private Spinner spinner2;
    List<String> list ;
    int spinnerPosition;
    public String Message;
    Cursor cursorCallLogs;
    MyDbHandler db;
    String SimState;
    int subscriptionId;
    Dialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALL_LOG},0);
        spinner2 = findViewById(R.id.spinner2);
        list = new ArrayList<> ();
        textView2 = findViewById(R.id.textView2);
        text_description2 = findViewById(R.id.description2);
        pNumber = "";
        db = new MyDbHandler(this);

        alertDialog = new Dialog(this);
        alertDialog.setContentView(R.layout.already_sent_dialog_box);
        alertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.setCancelable(false);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        Button no = alertDialog.findViewById(R.id.button11);
        Button yes = alertDialog.findViewById(R.id.button12);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyMessage();
                alertDialog.dismiss();
            }
        });
    }

    public void back(View view) {
        onBackPressed();
    }

    public void home(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @SuppressLint("ResourceAsColor")
    public void fetchLogs(View view){
        int permissionCheck = ContextCompat.checkSelfPermission(this , Manifest.permission.READ_CALL_LOG);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            String s0 = "Call Logging Started...";
            textView2.setText(s0);

            Uri allCalls = Uri.parse("content://call_log/calls");
            cursorCallLogs = getContentResolver().query(allCalls, null, null, null);
            /*
            Above one requires higher API so did something with lower API
            changed min SDK TO 26 IN BUILD.gradle(:app)
            Cursor cursorCallLogs = managedQuery(allCalls, null, null, null, null);
             */
            cursorCallLogs.moveToFirst();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
            //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(adapter);
            do {
                int num = cursorCallLogs.getColumnIndex(CallLog.Calls.NUMBER);
                int name = cursorCallLogs.getColumnIndex(CallLog.Calls.CACHED_NAME);
                String stringNumber = cursorCallLogs.getString(num);
                String stringName = cursorCallLogs.getString(name);
                String s4 = stringNumber + "-" + stringName;
                list.add(s4);
            } while (cursorCallLogs.moveToNext());
            adapter.notifyDataSetChanged();
            s0 = "Check dropdown list and select!!";
            int color = MaterialColors.getColor(findViewById(R.id.textView2), R.attr.colorTertiary);
            textView2.setTextColor(color);
            textView2.setText(s0);
            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    spinnerPosition = spinner2.getSelectedItemPosition();
                    s2 = setString2(spinnerPosition, allCalls);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            cursorCallLogs.close();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG} , 0);
        }
    }

    public String setString1(@NonNull EditText text_description2) {
        return text_description2.getText().toString().trim();
    }

    @NonNull
    public String setString2(int spinnerPosition, Uri allCalls){
        cursorCallLogs = getContentResolver().query(allCalls, null, null, null);
        cursorCallLogs.moveToFirst();
        cursorCallLogs.move(spinnerPosition);
        int pNum = cursorCallLogs.getColumnIndex(CallLog.Calls.NUMBER);
        pNumber = cursorCallLogs.getString(pNum);

        int intDate = cursorCallLogs.getColumnIndex(android.provider.CallLog.Calls.DATE);
        String callDate = cursorCallLogs.getString(intDate);
        subscriptionId = cursorCallLogs.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);//00
        long seconds=Long.parseLong(callDate);
        formatter = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        strDate = formatter.format(new Date(seconds));
        return pNumber
                +", "
                +strDate;
    }

    public void btn_send(View view){
        s1 = setString1(text_description2);
        Message = s1 + ", " + s2;
        int permissionCheckSendSms = ContextCompat.checkSelfPermission(this , Manifest.permission.SEND_SMS);
        if(permissionCheckSendSms == PackageManager.PERMISSION_GRANTED)
        {
            if(!text_description2.equals("") && text_description2.getText().toString().length() > 0){
                if(!db.CheckIsDataAlreadyInDBorNot(pNumber)){//00
                    MyMessage();
                }else {
                    alertDialog.show();
                    //Toast.makeText(this,"Already reported", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this,"Enter complete details!", Toast.LENGTH_LONG).show();
            }
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS} , 0);
        }

    }

    private String dateTimeNow(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a dd/MM/yy", Locale.getDefault());
        return df.format(c);
    }

    public boolean isSimExists() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int SIM_STATE = telephonyManager.getSimState();


        switch (SIM_STATE) {
            case TelephonyManager.SIM_STATE_READY:
                return true;
            case TelephonyManager.SIM_STATE_ABSENT:
                SimState = "No Sim Found!";
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                SimState = "Network Locked!";
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                SimState = "PIN Required to access SIM!";
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                SimState = "PUK Required to access SIM!";
                // Personal
                // Unblocking Code
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                SimState = "Unknown SIM State!";
                break;
            case TelephonyManager.SIM_STATE_CARD_IO_ERROR:
                SimState = "Sim Card to error!";
                break;
            case TelephonyManager.SIM_STATE_CARD_RESTRICTED:
                SimState = "Sim card Restricted!";
                break;
            case TelephonyManager.SIM_STATE_NOT_READY:
                SimState = "Sim not ready!";
                break;
            case TelephonyManager.SIM_STATE_PERM_DISABLED:
                SimState = "Sim Perm Disabled!";
                break;
        }
        return false;
    }

    private void MyMessage() {
        String phoneNumber = "7014942422";
        int permissionCheck_ReadPhoneState = ContextCompat.checkSelfPermission(this , Manifest.permission.READ_PHONE_STATE);
        if(permissionCheck_ReadPhoneState == PackageManager.PERMISSION_GRANTED){
            if(isSimExists()){
                SubscriptionManager localSubscriptionManager = SubscriptionManager.from(this);
                if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {//00
                    List<SubscriptionInfo> localList = localSubscriptionManager.getActiveSubscriptionInfoList();

                    SubscriptionInfo simInfo1 = localList.get(0);
                    SubscriptionInfo simInfo2 = localList.get(1);

                    if(subscriptionId == simInfo1.getSubscriptionId()){
//                        SmsManager.getSmsManagerForSubscriptionId(simInfo1.getSubscriptionId())
//                                .sendTextMessage(phoneNumber, null, Message, null, null);
                        Toast.makeText(this, Message , Toast.LENGTH_SHORT).show();
                    }else{
//                        SmsManager.getSmsManagerForSubscriptionId(simInfo2.getSubscriptionId())
//                                .sendTextMessage(phoneNumber, null, Message, null, null);
                        Toast.makeText(this, Message , Toast.LENGTH_SHORT).show();
                    }
                }else{
//                    SmsManager.getDefault().sendTextMessage(phoneNumber, null, Message, null, null);
                    Toast.makeText(this, Message , Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, SimState+ " " + "Cannot send SMS", Toast.LENGTH_LONG).show();
            }
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE} , 0);
        }
        String dateTimeNow = dateTimeNow();
        // TODO: What if SMS Sending failed
        db.addContact(pNumber,"AlreadyDone",strDate,dateTimeNow);
        Toast.makeText(this,pNumber, Toast.LENGTH_SHORT).show();
        db.close();
    }
}
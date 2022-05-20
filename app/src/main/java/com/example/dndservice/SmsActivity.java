package com.example.dndservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class SmsActivity extends AppCompatActivity {
    private TextView textView;
    SimpleDateFormat formatter;
    String stringSender, strDate, s1 ;
    private Spinner spinner;
    List<String> list ;
    int spinnerPosition;
    public String Message;
    Cursor cursorSms;
    MyDbHandler db;
    String SimState;
    int subscriptionId;
    Dialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},0);
        spinner = findViewById(R.id.spinner);
        list = new ArrayList<>();

        textView = findViewById(R.id.textView);
        db = new MyDbHandler(this);
        stringSender = "";

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

    public void fetchSms(View view){
        int permissionCheckReadSms = ContextCompat.checkSelfPermission(this , Manifest.permission.READ_SMS);
        if(permissionCheckReadSms == PackageManager.PERMISSION_GRANTED) {
            String s0 = "SMS Fetching Started...";
            textView.setText(s0);
            Uri allSms = Uri.parse("content://sms/inbox");
            cursorSms = getContentResolver().query(allSms,null,null,null);
            cursorSms.moveToFirst();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, list);
            spinner.setAdapter(adapter);
            do {
                int sender = cursorSms.getColumnIndex(Telephony.Sms.ADDRESS);
                int body = cursorSms.getColumnIndex(Telephony.Sms.BODY);
                //int time = cursorSms.getColumnIndex(Telephony.Sms.DATE);
                stringSender = cursorSms.getString(sender);
                String stringBody = cursorSms.getString(body);
                //String time = cursorSms.getString(time);//Needs to be fixed
                String s4 = stringSender
                        + "\n\t" + stringBody
                        //+ "\n\t" + time
                        + "\n";//not in use as of now because content is shrunk
                list.add(s4);
            } while (cursorSms.moveToNext());
            adapter.notifyDataSetChanged();
            s0 = "Check dropdown list and select!!";
            int color = MaterialColors.getColor(findViewById(R.id.textView), R.attr.colorTertiary);
            textView.setTextColor(color);
            textView.setText(s0);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    spinnerPosition = spinner.getSelectedItemPosition();
                    s1 = setString2(spinnerPosition, allSms);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            cursorSms.close();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS} , 0);
        }
    }


    @NonNull
    public String setString2(int spinnerPosition, Uri allSms){
        cursorSms = getContentResolver().query(allSms, null, null, null);
        cursorSms.moveToFirst();
        cursorSms.move(spinnerPosition);
        int ucc = cursorSms.getColumnIndex(Telephony.Sms.BODY);
        String stringUcc = cursorSms.getString(ucc);
        int sender = cursorSms.getColumnIndex(Telephony.Sms.ADDRESS);
        stringSender = cursorSms.getString(sender);

        int intDate = cursorSms.getColumnIndex(Telephony.Sms.DATE);
        String callDate = cursorSms.getString(intDate);

        subscriptionId = cursorSms.getColumnIndex(Telephony.Sms.SUBSCRIPTION_ID);//00

        long seconds=Long.parseLong(callDate);
        formatter = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        strDate = formatter.format(new Date(seconds));
        return stringUcc
                + ", "
                +stringSender
                +", "
                +strDate;
    }

    public void btn_send(View view){
        int permissionCheckSendSms = ContextCompat.checkSelfPermission(this , Manifest.permission.SEND_SMS);
        if(permissionCheckSendSms == PackageManager.PERMISSION_GRANTED)
        {
            Message = s1;
            if(!stringSender.equals("")){
                if(!db.CheckIsDataAlreadyInDBorNot(stringSender)){
                    MyMessage();
                }else {
                    alertDialog.show();
                    //Toast.makeText(this,"Already reported", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this,"Click Fetch SMS Button First !", Toast.LENGTH_LONG).show();
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
        db.addContact(stringSender,"AlreadyDone",strDate,dateTimeNow);
        //Toast.makeText(this,stringSender, Toast.LENGTH_SHORT).show();
        db.close();
    }

}
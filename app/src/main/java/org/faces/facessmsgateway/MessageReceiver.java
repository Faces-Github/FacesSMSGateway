package org.faces.facessmsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class MessageReceiver extends BroadcastReceiver {

    private static MessageListener mListener;
    String sender,from,body,display_message,message;
    @Override
    public void onReceive(Context context, Intent intent) {
        sender=from=body=display_message=message="";
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for(int i=0; i<pdus.length; i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            //            String message = "Sender : " + smsMessage.getDisplayOriginatingAddress()
//                    + "Email From: " + smsMessage.getEmailFrom()
//                    + "Emal Body: " + smsMessage.getEmailBody()
//                    + "Display message body: " + smsMessage.getDisplayMessageBody()
//                    + "Time in millisecond: " + smsMessage.getTimestampMillis()
//                    + "Message: " + smsMessage.getMessageBody();

            int time;


            sender = smsMessage.getDisplayOriginatingAddress();
            from = smsMessage.getEmailFrom();
            body = smsMessage.getEmailBody();
            display_message = smsMessage.getDisplayMessageBody();
            time = (int)smsMessage.getTimestampMillis();
            message = message+""+ smsMessage.getMessageBody();
            mListener.messageReceived(sender,from,body,display_message,time,message);
        }

        Toast.makeText(context,"New message Received",Toast.LENGTH_SHORT).show();
    }

    public static void bindListener(MessageListener listener){
        mListener = listener;
    }
}


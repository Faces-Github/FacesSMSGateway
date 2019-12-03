package org.faces.facessmsgateway;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import java.io.File;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SMS_to_Excel extends AppCompatActivity{
    static public int   READ_EXST=20;
    static public int   WRITE_EXST=21;
    static public int   READ_SMS=22;
    static public int   RECEIVE_SMS=23;
    TextInputEditText ET_Separator,ET_Header,ET_StartDate,ET_EndDate;
    MaterialButton BTNSend;
   String start_date,end_date,separator,headers;
    DatePickerDialog picker,picker2;

    SharedPreferences preference;
    SharedPreferences.Editor editor;

    String Saved_Separator,Saved_Header;
    String final_separator="@@@";

    ArrayList<String []> data = new ArrayList<String []>();
int num_records=0;
    CustomListViewDialog customDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_to__excel);

        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXST);
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXST);
        askForPermission(Manifest.permission.READ_SMS, READ_SMS);
        askForPermission(Manifest.permission.RECEIVE_SMS, RECEIVE_SMS);

        preference = getApplicationContext().getSharedPreferences("faces_gateway", 0); // 0 - for private mode
        editor = preference.edit();

        Saved_Separator = preference.getString("separator","");
        Saved_Header = preference.getString("header","");

            ET_Separator = findViewById(R.id.separator);
            ET_Header = findViewById(R.id.headers);
            ET_StartDate = findViewById(R.id.start_date);
            ET_EndDate = findViewById(R.id.end_date);


          if(!Saved_Separator.equals("")) {
              ET_Separator.setText(Saved_Separator);
          }

          if(!Saved_Header.equals("")){
              ET_Header.setText(Saved_Header);
          }

        BTNSend = findViewById(R.id.generate);
        BTNSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int error_count=0;
                      start_date = ET_StartDate.getText().toString();
                      end_date = ET_EndDate.getText().toString();
                      separator = ET_Separator.getText().toString();
                      headers = ET_Header.getText().toString();

                      if(start_date.equals("")){
                          error_count++;
                          ET_StartDate.setError("Enter correct start date");
                      }

                if(end_date.equals("")){
                    error_count++;
                    ET_EndDate.setError("Enter correct end date");
                }

                if(error_count==0){
                   if(Integer.parseInt(start_date.replace("-",""))>Integer.parseInt(end_date.replace("-",""))) {
                     error_count++;
                     ET_StartDate.setError("Start Date cannot be greater than End Date");
                     ET_EndDate.setError("Start Date cannot be greater than End Date");
                    }
                }


                if(error_count>0){
                    Toast.makeText(getApplicationContext(),"Check and correct highlighted errors",Toast.LENGTH_LONG);
                }

                else{
                    ET_StartDate.setError(null);
                    ET_EndDate.setError(null);
                    editor.putString("separator",separator);
                    editor.putString("header",headers);
                    editor.commit();

                    if(separator.equals("")){
                        separator="@@@";
                    }

                    headers = "Sender Phone"+separator+"Date & Time SMS Received"+separator+headers;
                    headers = headers.replace(separator,final_separator);
                    String header_array[] = headers.split(final_separator);

                    data.clear();

                    data.add(header_array);

                    getSMS(start_date,end_date);

                    File file = ExcelExporter.export(data,final_separator,start_date,end_date);
//                    shareFile(file);
//                    openFile(file);
                    viewActions(v,file);
                }


            }
        });


//        ET_StartDate.setEnabled(false);

        ET_StartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(SMS_to_Excel.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String mn="",dd="";
                                monthOfYear++;
                                if(monthOfYear<10){mn="0"+monthOfYear;} else{mn=""+monthOfYear;}
                                if(dayOfMonth<10){dd="0"+dayOfMonth;} else{dd=""+dayOfMonth;}
                                ET_StartDate.setText(year+"-" +mn+"-"+dd);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        ET_EndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker2 = new DatePickerDialog(SMS_to_Excel.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String mn="",dd="";
                                monthOfYear++;
                                if(monthOfYear<10){mn="0"+monthOfYear;} else{mn=""+monthOfYear;}
                                if(dayOfMonth<10){dd="0"+dayOfMonth;} else{dd=""+dayOfMonth;}
                                ET_EndDate.setText(year+"-" +mn+"-"+dd);
                            }
                        }, year, month, day);
                picker2.show();
            }
        });
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(SMS_to_Excel.this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    SMS_to_Excel.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(SMS_to_Excel.this,
                        new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(SMS_to_Excel.this,
                        new String[]{permission}, requestCode);
            }
        } else {
//            Toast.makeText(this, permission + " is already granted.",
//                    Toast.LENGTH_SHORT).show();
        }
    }


    public void getSMS(String start_,String end_){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date dateStart=null,dateEnd=null,eDate=null;
        try {
            dateStart = formatter.parse(start_ + "T00:00:00");
            eDate = formatter.parse(end_ + "T00:00:00");
        }
        catch (ParseException e){
        Toast.makeText(getApplicationContext(),"Error Processing start and end dates",Toast.LENGTH_LONG).show();
        }

        dateEnd = new Date(eDate.getTime() + TimeUnit.DAYS.toMillis( 1 ));
        // Now create the filter and query the messages.
        String filter = "date between " + dateStart.getTime() + " and " + dateEnd.getTime();
        String sms_data = "";

//        String sms_data = "13860*2019-11-21*56*3*1";
//
//        sms_data=sms_data.replace(separator,final_separator);
//
//        data.add(sms_data.split(final_separator));


        final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(SMS_INBOX, null, filter, null, null);
        List<String> items = new ArrayList<String>();

        while(cursor.moveToNext()) {
            // Convert date to a readable format.
            Calendar calendar = Calendar.getInstance();
            String date =  cursor.getString(cursor.getColumnIndex("date"));
            Long timestamp = Long.parseLong(date);
            calendar.setTimeInMillis(timestamp);
            Date finaldate = calendar.getTime();
            String smsDate = finaldate.toString();

            //String smsDate =  cursor.getString(cursor.getColumnIndex("date"));
            String smsBody = cursor.getString(cursor.getColumnIndex("body"));
            String phoneNumber =cursor.getString(cursor.getColumnIndex("address"));

            sms_data = phoneNumber+separator+smsDate+separator+smsBody;

            sms_data=sms_data.replace(separator,final_separator);

            data.add(sms_data.split(final_separator));
        }
        cursor.close();

        num_records = (data.size()-1);
//        Toast.makeText(getApplicationContext(),"Messages Found "+(data.size()-1),Toast.LENGTH_LONG).show();
    }

    public void viewActions(View view, File f) {
        customDialog = new CustomListViewDialog(SMS_to_Excel.this,f,num_records);
        customDialog.show();
        customDialog.setCanceledOnTouchOutside(false);
    }
}

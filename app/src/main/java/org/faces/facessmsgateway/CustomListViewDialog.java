package org.faces.facessmsgateway;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.net.URLConnection;

public class CustomListViewDialog extends Dialog implements View.OnClickListener {

    public Activity activity;
    public MaterialButton Share, Open,Exit;
    TextView title;
    File file=null;
    MaterialTextView TitleRecords;
int num_records=0;
    public CustomListViewDialog(Activity a, File file, int num_records) {
        super(a);
        this.activity = a;
        setupLayout();
        this.file=file;
        this.num_records = num_records;
    }

    private void setupLayout() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_layout);
        Share = findViewById(R.id.sharefile);
        Open = findViewById(R.id.openfile);
        Exit = findViewById(R.id.exit);
        TitleRecords = findViewById(R.id.no_records);

        if(num_records>1) {
            TitleRecords.setText(num_records + " Records found");
        }
        else if(num_records==1){
            TitleRecords.setText(num_records + " Records found");
        }
        else{
            TitleRecords.setText("No Records to export");
        }
        title = findViewById(R.id.title);
        Exit.setOnClickListener(this);

        if(num_records>0){
            Share.setOnClickListener(this);
            Open.setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sharefile: // share file to other users
                //Do Something
                shareFile(v);
                break;
            case R.id.openfile: // open file for content viewing
                openFile(v);
                break;

            case R.id.exit: // open file for content viewing
                dismiss();
                break;
            default:
                break;
        }
        //dismiss();
    }

    private void shareFile(View v) {

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        intentShareFile.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file://"+file.getAbsolutePath()));

        //if you need
        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Faces SMS data");
        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Dear User,\n Attached kindly find SMS to excel data.\n\n " +
                "The file is stored locally in the path "+file.getAbsolutePath());

        v.getContext().startActivity(Intent.createChooser(intentShareFile, "Share File"));

        //delete shared file
        //boolean deleted = file.delete();

    }

    private void openFile(View v) {

        try {

            Uri uri = Uri.fromFile(file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (file.toString().contains(".doc") || file.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (file.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (file.toString().contains(".ppt") || file.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (file.toString().contains(".xls") || file.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (file.toString().contains(".zip")) {
                // ZIP file
                intent.setDataAndType(uri, "application/zip");
            } else if (file.toString().contains(".rar")){
                // RAR file
                intent.setDataAndType(uri, "application/x-rar-compressed");
            } else if (file.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (file.toString().contains(".wav") || file.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (file.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (file.toString().contains(".jpg") || file.toString().contains(".jpeg") || file.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (file.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (file.toString().contains(".3gp") || file.toString().contains(".mpg") ||
                    file.toString().contains(".mpeg") || file.toString().contains(".mpe") || file.toString().contains(".mp4") || file.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                intent.setDataAndType(uri, "*/*");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(v.getContext(), "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }
}

package org.faces.facessmsgateway;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelExporter {
    public static File export(ArrayList<String []> data, String separator, String start_date,String end_date) {
        String filename = "Faces_HTS_SMS_to_Excel_data_from_"+start_date.replace("-","_")+"_to_"+end_date.replace("-","_");
        File file=null;
        File sd = Environment.getExternalStorageDirectory();
        String excelFile = filename+".xls";

        File directory = new File(sd.getAbsolutePath()+"/FACES/Excel_Extracts/");
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {
            //file path
            file = new File(directory, excelFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale(Locale.GERMAN.getLanguage(), Locale.GERMAN.getCountry()));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);

            //Excel sheetA first sheetA
            WritableSheet sheetA = workbook.createSheet("SMS_to_Excel_"+start_date+" to "+end_date, 0);


            for(int row=0;row<data.size();row++){ // these are the columns to generate
                String [] row_data = data.get(row);
                for(int col=0;col<row_data.length;col++){ // number of columns to create
                String elem_data = row_data[col];
                sheetA.addCell(new Label(col, row, elem_data));
                }
            }
            // close workbook
            workbook.write();
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }
}

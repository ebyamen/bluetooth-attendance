package com.distresx.bluetoothyoklama.others;

import android.net.Uri;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;


public class ReadExcelFile {

    protected static final String TAG = ReadExcelFile.class.getSimpleName();

    static String quesSt, op1, op2, op3, op4;
    static int ca;
    static ArrayList<Question> questionArrayList = new ArrayList<Question>();

   public static ArrayList<Question> readExcelFile(Uri uri) {

        questionArrayList.clear();

        try{
            // Creating Input Stream
            File file = new File(uri.getPath());
            try (FileInputStream myInput = new FileInputStream(file)) {

                // Create a POIFSFileSystem object
                POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

                // Create a workbook using the File System
                HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

                // Get the first sheet from workbook
                HSSFSheet mySheet = myWorkBook.getSheetAt(0);

                /** We now need something to iterate through the cells.*/
                Iterator rowIter = mySheet.rowIterator();

                while (rowIter.hasNext()) {
                    HSSFRow myRow = (HSSFRow) rowIter.next();
                    Iterator cellIter = myRow.cellIterator();
                    int i = 1;
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if (i == 1) {
                            quesSt = myCell.toString();
                            i++;
                        } else if (i == 2) {
                            op1 = myCell.toString();
                            i++;
                        } else if (i == 3) {
                            op2 = myCell.toString();
                            i++;
                        } else if (i == 4) {
                            op3 = myCell.toString();
                            i++;
                        } else if (i == 5) {
                            op4 = myCell.toString();
                            i++;
                        } else if (i == 6) {
                            ca = (int) Double.parseDouble(String.valueOf(myCell));
                            i++;
                        }
                        Log.d(TAG, "Cell Value: " + myCell.toString());
                        //Toast.makeText(context, "Cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();
                    }
                    questionArrayList.add(new Question(quesSt, op1, op2, op3, op4, ca));
                }
                myInput.close();
            }
        }catch (Exception e){e.printStackTrace(); }
        return questionArrayList;
    }
}

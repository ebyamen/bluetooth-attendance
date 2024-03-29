package com.distresx.bluetoothyoklama.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.distresx.bluetoothyoklama.R;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.distresx.bluetoothyoklama.others.Excel_sheet_access;
import com.distresx.bluetoothyoklama.realm.Register;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class Add_class extends AppCompatActivity
{
    private static String TAG = "Add_class";

    int card_padding;
    DisplayMetrics metrics;
    int height;
    EditText batch_edit,subject_edit,subjectcode_edit,semester_edit,stream_edit,section_edit,group_edit;
    ImageView imageView;
    String batch,subject,batchid,subjectcode,semester,stream,section,group;
    int semesterI,batchI;
    CheckBox groupCheck;
    boolean groupB = false;
    int year;

    Excel_sheet_access excel_sheet;

    Realm realm;
    RealmResults<Register> checkBatch;
    RealmConfiguration realmConfig;


    private static final int FILE_SELECT_CODE = 0;

    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.main_blue));
        }
        initToolbar();

        realmConfig = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(realmConfig);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        height= metrics.heightPixels;
        card_padding=height/20;

        batch_edit = (EditText)findViewById(R.id.edit_batch);
        subject_edit = (EditText)findViewById(R.id.edit_subject);
        semester_edit = (EditText)findViewById(R.id.edit_semester);
        subjectcode_edit = (EditText)findViewById(R.id.edit_subjectcode);



        imageView=(ImageView)findViewById(R.id.button_import_excel);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (subject_edit.getText().toString().trim().equalsIgnoreCase(""))
                    subject_edit.setError("Bu satır boş kalamaz");
                else if (subjectcode_edit.getText().toString().trim().equalsIgnoreCase(""))
                    subjectcode_edit.setError("Bu satır boş kalamaz");
                else if (batch_edit.getText().toString().trim().equalsIgnoreCase(""))
                    batch_edit.setError("Bu satır boş kalamaz");
                else if (semester_edit.getText().toString().trim().equalsIgnoreCase(""))
                    semester_edit.setError("Bu satır boş kalamaz");
                else {

                    AlertDialog.Builder alert = new AlertDialog.Builder(Add_class.this);
                    alert.setTitle("Excel Sayfası İçe Aktarma");
                    alert.setMessage(" ÖNEMLİ !!-\n\n" +
                            "- İçe aktarılacak Excel dosyası XLS formatında olmalı.(XLSX olmadığına dikkat ediniz)\n\n" +
                            "- Excel sayfasında 5 sütun olmalı (Soldan Sağa)-\n \t* Öğrenci Numarası\n \t* Ad-Soyad\n \t* Telefon Numarası\n \t* MacID1\n \t* MacID2(Opsiyonel).\n\n" +
                            "- Excel Sayfası tercihen Microsoft Excel'de oluşturulmalı ve Android cihazda oluşturulmamalı.\n\n");
                    alert.setPositiveButton("Excel Dosyasını Seç", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            year = Integer.parseInt(semester_edit.getText().toString().trim());
                            Add_class.this.generateBatchID();
                            checkBatch = realm.where(Register.class).equalTo("BatchID",batchid).findAll();  //Checking if The same BatchID already exists
                            if(checkBatch.size() == 0) {
                                Intent intent = new Intent(Add_class.this, FilePickerActivity.class);
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                                intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                                intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

                                //intent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                                startActivityForResult(intent, FILE_SELECT_CODE);
                            }
                            else
                                Toast.makeText(Add_class.this,"Bu sınıf zaten mevcut",Toast.LENGTH_SHORT).show();
                        }
                    });
                    alert.setNegativeButton("İptal",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.show();

                }
            }
        });
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sınıf Ekle");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK)
        {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false))
            {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    ClipData clip = data.getClipData();
                    if (clip != null)
                    {
                        for (int i = 0; i < clip.getItemCount(); i++)
                        {
                            Uri uri = clip.getItemAt(i).getUri();
                            Toast.makeText(this,""+uri.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    // For Ice Cream Sandwich
                }
                else
                {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null)
                    {
                        for (String path: paths)
                        {
                            Uri uri = Uri.parse(path);
                            Toast.makeText(this,""+uri.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
            else
            {
                Uri uri = data.getData();
                Excel_sheet_access.readExcelFile(this,uri,batchid,subject,subjectcode,batchI,semesterI,stream,section,group);
                Toast.makeText(Add_class.this, "Sınıf Eklendi!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Add_class.this,Attendance.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void generateBatchID()    //Concatenate Batch Name and Subject to create final batch name
    {
        batch=batch_edit.getText().toString().trim();
        batchI = Integer.parseInt(batch);
        subject=subject_edit.getText().toString().trim();
        subjectcode = subjectcode_edit.getText().toString().trim();
        semester = semester_edit.getText().toString().trim();
        semesterI = Integer.parseInt(semester);
        if(groupB)
            group = group_edit.getText().toString().trim();
        else
            group = "-";

        batchid = subject;
        batchid = batchid.concat(subjectcode);
        batchid = batchid.concat(batch);
        batchid = batchid.concat(semester);
        if (groupB)
            batchid.concat(group);
        batchid = batchid.replaceAll("\\s+","");    //remove spaces from batch name
        batchid = batchid.toLowerCase();
    }



    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}

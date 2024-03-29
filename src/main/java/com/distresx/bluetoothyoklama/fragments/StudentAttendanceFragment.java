package com.distresx.bluetoothyoklama.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.distresx.bluetoothyoklama.R;
import com.distresx.bluetoothyoklama.adapters.StudentAttendanceListAdapter;
import com.distresx.bluetoothyoklama.realm.DateRegister;
import com.distresx.bluetoothyoklama.realm.Register;
import com.distresx.bluetoothyoklama.realm.Student;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;


public class StudentAttendanceFragment extends Fragment
{
    View view;
    android.content.Context context;

    String batchID;
    String rollNum;

    Realm realm;
    RealmConfiguration realmConfig;
    RealmList<DateRegister> registerRecords;
    RealmList<Student> studentsPresentList;
    Student selectedStudent;
    int totalNumRecords=0,totalDaysPresent=0;
    float attendancePercentage;
    ArrayList<Integer> presentDatesID;

    ListView recordListView;
    TextView percentageView;
    StudentAttendanceListAdapter studentAttendanceListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_student_attendance, container, false);

        realmConfig = new RealmConfiguration.Builder(context).build();
        realm = Realm.getInstance(realmConfig);

        recordListView = (ListView)view.findViewById(R.id.student_record_list);
        percentageView = (TextView)view.findViewById(R.id.attendance_percentage);

        presentDatesID = new ArrayList<Integer>();
        registerRecords = realm.where(Register.class).equalTo("BatchID",batchID).findFirst().getRecord();
        registerRecords.sort("dateToday");
        for(int i=0 ; i<registerRecords.size() ; i++)
            totalNumRecords+=registerRecords.get(i).getValue();
        selectedStudent = realm.where(Student.class).equalTo("Roll_number",rollNum).findFirst();

        calculateAttendance();

        studentAttendanceListAdapter = new StudentAttendanceListAdapter(context,registerRecords,presentDatesID,totalNumRecords,totalDaysPresent,StudentAttendanceFragment.this);
        recordListView.setAdapter(studentAttendanceListAdapter);
        totalDaysPresent = 0;
        return view;
    }

    private void calculateAttendance() {
        int i;
        for (i = 0; i < registerRecords.size(); i++)
        {
            studentsPresentList = registerRecords.get(i).getStudentPresent();
            if (studentsPresentList.contains(selectedStudent)) {
                presentDatesID.add(registerRecords.get(i).getDateID());
                totalDaysPresent+=registerRecords.get(i).getValue();
            }
        }
        attendancePercentage = ((float)totalDaysPresent*100)/(float)totalNumRecords;
        percentageView.setText(""+attendancePercentage + " %");
        totalNumRecords=totalDaysPresent=0;
    }

    public void markStudent(int dateRegisterId)
    {
        DateRegister record;
        Student student;
        record = realm.where(DateRegister.class).equalTo("dateID",dateRegisterId).findFirst();
        student = realm.where(Student.class).equalTo("Roll_number",rollNum).findFirst();
        realm.beginTransaction();
        if(!record.getStudentPresent().contains(student))
            record.getStudentPresent().add(student);
        realm.commitTransaction();

        registerRecords = realm.where(Register.class).equalTo("BatchID",batchID).findFirst().getRecord();

        for(int i=0 ; i<registerRecords.size() ; i++)
            totalNumRecords+=registerRecords.get(i).getValue();

        int i;
        for (i = 0; i < registerRecords.size(); i++)
        {
            studentsPresentList = registerRecords.get(i).getStudentPresent();
            if (studentsPresentList.contains(selectedStudent)) {
                totalDaysPresent+=registerRecords.get(i).getValue();
            }
        }
        attendancePercentage = ((float)totalDaysPresent*100)/(float)totalNumRecords;
        percentageView.setText(""+attendancePercentage + " %");
        totalNumRecords=totalDaysPresent=0;
    }

    public void unmarkStudent(int dateRegisterId)
    {
        DateRegister record;
        Student student;
        realm.beginTransaction();
        record = realm.where(DateRegister.class).equalTo("dateID",dateRegisterId).findFirst();
        student = realm.where(Student.class).equalTo("Roll_number",rollNum).findFirst();
        if( record.getStudentPresent().contains(student))
            record.getStudentPresent().remove(student);
        realm.commitTransaction();

        registerRecords = realm.where(Register.class).equalTo("BatchID",batchID).findFirst().getRecord();
        for(int i=0 ; i<registerRecords.size() ; i++)
            totalNumRecords+=registerRecords.get(i).getValue();

        int i;
        for (i = 0; i < registerRecords.size(); i++)
        {
            studentsPresentList = registerRecords.get(i).getStudentPresent();
            if (studentsPresentList.contains(selectedStudent)) {
                totalDaysPresent+=registerRecords.get(i).getValue();
            }
        }
        attendancePercentage = ((float)totalDaysPresent*100)/(float)totalNumRecords;
        percentageView.setText(""+attendancePercentage + " %");
        totalNumRecords=totalDaysPresent=0;
    }


    public void getBatchID(String batchID)
    {
        this.batchID = batchID;
    }
    public void getStudentRoll(String rollNum) { this.rollNum = rollNum; }
    public void getActivityContext(android.content.Context context) { this.context = context; }
}

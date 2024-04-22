package com.social_network.pnu_app.firebase;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.social_network.pnu_app.entity.Student;
import com.social_network.pnu_app.registration.Registration;

import java.util.HashMap;
import java.util.Map;

public class QueriesFirebase {

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("students");

    static int counter = 10;
    public void addStudent() {


        String id = reference.push().getKey();

        Student newStudent = new Student("BA61582106", "ПНУ", "Life", counter++,
                 "","", "", "", true, "",  "математики та інформатики", "",
                "01-07-2016", "Денна" , "");

        Map<String, Object> studentValues = newStudent.toMap();

        Map<String, Object> student = new HashMap<>();
        student.put(id, studentValues);

        reference.updateChildren(student);

    }



}
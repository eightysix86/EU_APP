package com.social_network.pnu_app.entity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.PhoneAuthCredential;
import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.social_network.pnu_app.localdatabase.StudentSqlLite;
import com.social_network.pnu_app.pages.MainStudentPage;
import com.social_network.pnu_app.registration.Registration;

import java.util.HashMap;
import java.util.Map;

public class Student {

    public StudentSqlLite studentLocalSet = new StudentSqlLite();


    public static HashMap<Object, Object> student = new HashMap(){{
        put("faculty", "faculty");
        put("group", "group");
        put("dateOfEntry", "dateOfEntry");
        put("formStudying", "formStudying");
    }};
    public static String seriesIDcard;
    public String name;
    public String lastName;
    public int id;
    public String email;
    public String password;
    public String phone;
    public boolean verify;
    public String uid;
    public String linkFirebaseStorageMainPhoto;

    public String patronym;
    String faculty;
    String group;
    String dateOfEntry;
    String formStudying;
    String deviceToken;
    PhoneAuthCredential credentiall;

    AppDatabase StudentDatabase;

    public Student(){}

    public Student(String seriesIDcard){
        this.seriesIDcard = seriesIDcard;
    }


    public Student(String seriesIDcard, String name, String lastName, int id, String email,
                   String password,String phone, String uid, boolean verify, String patronym, String faculty,
                   String group, String dateOfEntry, String formStudying, String linkFirebaseStorageMainPhoto){

        this.seriesIDcard = seriesIDcard;
        this.name = name;
        this.lastName = lastName;
        this.id = id;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.verify = verify;
        this.uid = uid;
        this.patronym = patronym;
        this.faculty = faculty;
        this.group = group;
        this.dateOfEntry = dateOfEntry;
        this.formStudying = formStudying;
        this.linkFirebaseStorageMainPhoto = linkFirebaseStorageMainPhoto;

    }

    public Student(PhoneAuthCredential credential, boolean verify, String password,String phone,String uid, String deviceToken){
        this.credentiall = credential;
        this.verify = verify;
        this.password = password;
        this.phone = phone;
        this.uid = uid;
        this.deviceToken = deviceToken;

    }


    public Student(String linkFirebaseStorageMainPhoto, String emptyParametr){
        this.linkFirebaseStorageMainPhoto = linkFirebaseStorageMainPhoto;
    }

    public Map<String, Object> toMapUpdateChild(){
        HashMap<String, Object> MapDatabase = new HashMap<>();
        MapDatabase.put("verify",verify);
        MapDatabase.put("password", password);
        MapDatabase.put("uid", uid);
        MapDatabase.put("phone", phone);
        MapDatabase.put("deviceToken",deviceToken);

        return MapDatabase;
    }

    public Map<String, Object> toMapUpdatelinkFirebaseStorageMainPhoto(){
        HashMap<String, Object> MapDatabase = new HashMap<>();
        MapDatabase.put("linkFirebaseStorageMainPhoto",linkFirebaseStorageMainPhoto);
        return MapDatabase;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> MapDatabase = new HashMap<>();
        MapDatabase.get(password);
        MapDatabase.put("seriesIDcard", seriesIDcard);
        MapDatabase.put("name", name);
        MapDatabase.put("lastName", lastName);
        MapDatabase.put("id", id);
        MapDatabase.put("email", email);
        MapDatabase.put("password", password);
        MapDatabase.put("phone", phone);
        MapDatabase.put("uid", uid);
        MapDatabase.put("verify",verify);
        MapDatabase.put("patronym",patronym);
        MapDatabase.put("faculty",faculty);
        MapDatabase.put("group",group);
        MapDatabase.put("dateOfEntry",dateOfEntry);
        MapDatabase.put("formStudying",formStudying);
        MapDatabase.put("linkFirebaseStorageMainPhoto",linkFirebaseStorageMainPhoto);

        return MapDatabase;
    }

    public void synchronizationSQLiteSignIn(final AppDatabase db){
        StudentDatabase = db;
        if (db.studentDao().getSeriesIDcard(seriesIDcard) != null){
            System.out.println("updateStudetnSQLite() getSeriesIDcard(seriesIDcard)= " + (db.studentDao().getSeriesIDcard(seriesIDcard)));
            System.out.println("studentUpdate() = " + student);
            updateStudetnSQLite();

        }
        else {
            System.out.println("setStudentSQLite() getSeriesIDcard(seriesIDcard)= " + (db.studentDao().getSeriesIDcard(seriesIDcard)));
            System.out.println("studentSet() = " + student);
            setStudentSQLite();

        }
    }

    public void updateStudetnSQLite(){
        StudentDatabase.studentDao().updateAllField(
                seriesIDcard,
                name,
                lastName,
                id,
                email,
                password,
                phone,
                uid,
                verify
                );
    }
    public void setStudentSQLite(){

        studentLocalSet.setSeriesIDcard(seriesIDcard);
        studentLocalSet.setFirstName(name);
        studentLocalSet.setLastName(lastName);
        studentLocalSet.setUid(id);
        studentLocalSet.setEmail(email);
        studentLocalSet.setPassword(password);
        studentLocalSet.setPhone(phone);
        studentLocalSet.setKeyFireBase(uid);
        studentLocalSet.setVerify(verify);

        studentLocalSet.setPatronym(patronym);
        studentLocalSet.setFaculty(faculty);
        studentLocalSet.setGroupStudent(group);
        studentLocalSet.setDateOfEntry(dateOfEntry);
        studentLocalSet.setFormStudying(formStudying);

        studentLocalSet.setCurrentStudent(student.get("id").toString());

        StudentDatabase.studentDao().insertStudent(studentLocalSet);
    }
}

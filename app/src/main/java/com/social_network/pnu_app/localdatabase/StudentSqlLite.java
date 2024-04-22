package com.social_network.pnu_app.localdatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.RoomWarnings;

import static java.sql.Types.DECIMAL;
import static java.sql.Types.INTEGER;
import static java.sql.Types.VARCHAR;

@Entity(tableName = "student" ) //, indices = {@Index(value = {"email", "phone", "seriesIDcard"}, unique = true)})
public class StudentSqlLite {



    //@NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "u_id")
    private int uid = 0;

   //@NonNull
    @ColumnInfo(name = "first_name", typeAffinity = VARCHAR)
    private String firstName;

   //@NonNull
    @ColumnInfo(name = "last_name", typeAffinity = VARCHAR)
    private String lastName;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "email", typeAffinity = VARCHAR)
    private String email;

   // @NonNull
    @ColumnInfo(name = "seriesIDcard")
    private String seriesIDcard;

    @ColumnInfo(name = "phone", typeAffinity = VARCHAR)
    private String phone;

    @ColumnInfo(name = "currentStudent", typeAffinity = VARCHAR)
    private String currentStudent;

    @ColumnInfo(name = "KeyFireBase" ,typeAffinity = VARCHAR)
    private String KeyFireBase;

    @ColumnInfo(name = "verify" ,typeAffinity = VARCHAR)
    private boolean verify;

    @ColumnInfo(name = "patronym", typeAffinity = VARCHAR)
    private String patronym;

    @ColumnInfo(name = "faculty", typeAffinity = VARCHAR)
    private String faculty;

    @ColumnInfo(name = "groupStudent", typeAffinity = VARCHAR)
    private String groupStudent;

    @ColumnInfo(name = "dateOfEntry", typeAffinity = VARCHAR)
    private String dateOfEntry;

    @ColumnInfo(name = "formStudying", typeAffinity = VARCHAR)
    private String formStudying;





    public boolean getVerify(){
        return verify;
    }





   /*@ColumnInfo(name = "dataPublishIDcar", typeAffinity = DATE)
    private String dataPublishIDcar;


    @ColumnInfo(name = "dataValidIDcar", typeAffinity = DATE)
    private String dataValidIDcar;*/


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



   @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {

        this.email = email;
    }


    @NonNull
    public String getSeriesIDcard() {
        return seriesIDcard;
    }

    public void setSeriesIDcard(@NonNull String series_id_card) {
        this.seriesIDcard = series_id_card;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Ignore
    public boolean isVerify() {
        return verify;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }

    public String getKeyFireBase() {
        return KeyFireBase;
    }

    public void setKeyFireBase(String KeyFireBase) {
        this.KeyFireBase = KeyFireBase;
    }

    public String getPatronym() {
        return patronym;
    }

    public void setPatronym(String patronym) {
        this.patronym = patronym;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getGroupStudent() {
        return groupStudent;
    }

    public void setGroupStudent(String groupStudent) {
        this.groupStudent = groupStudent;
    }


    public String getDateOfEntry() {
        return dateOfEntry;
    }

    public void setDateOfEntry(String dateOfEntry) {
        this.dateOfEntry = dateOfEntry;
    }

    public String getFormStudying() {
        return formStudying;
    }

    public void setFormStudying(String formStudying) {
        this.formStudying = formStudying;
    }


    public String getCurrentStudent() {
        return currentStudent;
    }

    public void setCurrentStudent(String currentStudent) {
        this.currentStudent = currentStudent;
    }
}

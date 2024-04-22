package com.social_network.pnu_app.localdatabase;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.social_network.pnu_app.entity.Student;

import java.util.HashMap;
import java.util.List;


@Dao
public interface StudentDao {

    // Simple

    @Query("SELECT * FROM student")
    List<StudentSqlLite> getAll();

    @Query("SELECT u_id FROM student WHERE u_id = :u_id ")
    int getId(int u_id);

    @Query("SELECT seriesIDcard FROM student WHERE seriesIDcard = :seriesIDcard ")
    String getSeriesIDcard(String seriesIDcard);

    @Query("SELECT first_name FROM student WHERE first_name = :name")
    String getFirstName(String name);

    @Query("SELECT last_name FROM student WHERE last_name = :Lastname")
    String getLastName(String Lastname);

    @Query("SELECT password FROM student WHERE password = :password")
    String getPassword(String password);

    @Query("SELECT email FROM student WHERE email = :email")
    String getEmail(String email);

    @Query("SELECT phone FROM student WHERE phone = :phone")
    String getPhone(String phone);

    @Query("SELECT verify FROM student WHERE verify = :verify")
    boolean getVerify(boolean verify);



    // By id

    @Query("SELECT first_name FROM student WHERE u_id = :uid")
    String getFirstNameById(int uid);

    @Query("SELECT last_name FROM student WHERE u_id = :uid")
    String getLastNameById(int uid);

    @Query("SELECT u_id  FROM student WHERE seriesIDcard = :seriesIDcard")
    int getIdstudentByIDcard(String seriesIDcard);


    @Query("SELECT u_id  FROM student WHERE password = :password AND u_id =:idSeriesIDCard")
    int getIdstudentByIDPassword(String password, int idSeriesIDCard);

    @Query("SELECT password FROM student WHERE password = :password AND u_id= :id")
    String getPasswordById(String password, int id);

    @Query("SELECT * FROM student WHERE first_name LIKE :firstName AND last_name LIKE :lastName")
    StudentSqlLite findByName(String firstName, String lastName);

    @Query("SELECT COUNT(*) FROM student")
    int countStudent();

    @Query("SELECT verify FROM student WHERE u_id = :uid")
    boolean getVerifyByID(int uid);

    @Query("SELECT password FROM student WHERE u_id = :uid")
    String getPasswordById(int uid);

    @Query("SELECT email FROM student WHERE u_id = :uid")
    String getEmailById(int uid);

    @Query("SELECT seriesIDcard FROM student WHERE u_id = :uid")
    String getSeriesBYId(int uid);

    @Query("SELECT patronym FROM student WHERE u_id = :uid")
    String getPatronymById(int uid);

    @Query("SELECT faculty FROM student WHERE u_id = :uid")
    String getFacultyById(int uid);

    @Query("SELECT groupStudent FROM student WHERE u_id = :uid")
    String getGroupById(int uid);

    @Query("SELECT dateOfEntry FROM student WHERE u_id = :uid")
    String getDateOfEntryById(int uid);

    @Query("SELECT formStudying FROM student WHERE u_id = :uid")
    String getFormStudyingById(int uid);

    @Query("SELECT KeyFireBase FROM student WHERE currentStudent=u_id")
    String getKeyStudent();

    @Query("SELECT currentStudent FROM student WHERE currentStudent=u_id")
    String getCurrentStudent();


    @Query("UPDATE student SET verify=:verify WHERE u_id= :id")
    void updateVerify(boolean verify, int id);

    @Query("UPDATE student SET password=:password WHERE u_id= :id")
    void setPassword(String password, int id);

    @Query("UPDATE student SET email=:email WHERE u_id= :id")
    void setEmail(String email, int id);

   // UpdateByid

    @Query("UPDATE student SET seriesIDcard=:seriesIDcard, first_name=:first_name," +
            " last_name=:lastName, u_id=:id,  verify=:verify, email=:email, password=:password," +
            " phone=:phone, KeyFireBase=:FireBaseUid, currentStudent=:id WHERE seriesIDcard=:seriesIDcard")
    void updateAllField(String seriesIDcard, String first_name, String lastName, int id, String email,
                        String password, String phone, String FireBaseUid, boolean verify);

    @Query("UPDATE student SET currentStudent=null WHERE currentStudent=:id")
    void updateCurrentStudent(int id);

/*    @Query("UPDATE student SET email=:email,  password=:password WHERE u_id= :id")
    void updateStudent(String email, int id, String password);*/

    @Update
    void updateStudent(StudentSqlLite... student);

    @Insert
    void insertStudent(StudentSqlLite student);



   // @Query("DELETE FROM student")
   // void DeleteAllTableStudent();





    @Insert
    void insertAll(StudentSqlLite... students);


    @Delete
    void delete(StudentSqlLite student);
}

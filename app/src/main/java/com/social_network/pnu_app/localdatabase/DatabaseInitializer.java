package com.social_network.pnu_app.localdatabase;


import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

public class DatabaseInitializer {

    private static final String TAG = DatabaseInitializer.class.getName();

    public static void populateAsync(@NonNull final AppDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
       task.execute();
    }
    public static void populateSync(@NonNull final AppDatabase db) {
        populateWithTestData(db);
    }

    private static StudentSqlLite addStudent(final AppDatabase db, StudentSqlLite student) {
        db.studentDao().insertAll(student);
        return student;
    }

    private static void populateWithTestData(AppDatabase db) {

        List<StudentSqlLite> userList = db.studentDao().getAll();
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + userList.size());


    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(mDb);
            return null;
        }

   }
}

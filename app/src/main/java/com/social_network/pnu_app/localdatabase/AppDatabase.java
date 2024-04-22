package com.social_network.pnu_app.localdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {StudentSqlLite.class}, version = 10, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static final String DBNAME = "studentDB.db";

    public abstract StudentDao studentDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DBNAME)
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                //    .studentDao().DeleteAllTableStudent();
        }
        return INSTANCE;
    }

   // public static void destroyInstance() {
     //   INSTANCE = null;
   // }
}

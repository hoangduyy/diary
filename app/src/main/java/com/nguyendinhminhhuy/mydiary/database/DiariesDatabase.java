package com.nguyendinhminhhuy.mydiary.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nguyendinhminhhuy.mydiary.dao.DiaryDao;
import com.nguyendinhminhhuy.mydiary.entities.Diary;

@Database(entities = Diary.class, version = 1, exportSchema = false)
public abstract class DiariesDatabase extends RoomDatabase {

    private static DiariesDatabase diariesDatabase;

    public static synchronized DiariesDatabase getDatabase(Context context){
        if(diariesDatabase == null){
            diariesDatabase = Room.databaseBuilder(
                    context,
                    DiariesDatabase.class,
                    "diaries_db"
            ).build();
        }
        return diariesDatabase;
    }

    public abstract DiaryDao diaryDao();
}

package com.nguyendinhminhhuy.mydiary.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.nguyendinhminhhuy.mydiary.entities.Diary;

import java.util.List;

@Dao
public interface DiaryDao {

    @Query("SELECT * FROM diaries ORDER BY id DESC")
    List<Diary> getAllDiaries();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDiary(Diary diary);

    @Delete
    void deleteDiary(Diary diary);
}

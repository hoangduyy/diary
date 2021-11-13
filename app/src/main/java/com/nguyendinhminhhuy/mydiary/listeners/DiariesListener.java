package com.nguyendinhminhhuy.mydiary.listeners;

import com.nguyendinhminhhuy.mydiary.entities.Diary;

public interface DiariesListener {
    void onDiaryClicked(Diary diary, int position);
}

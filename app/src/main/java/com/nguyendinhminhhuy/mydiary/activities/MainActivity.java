package com.nguyendinhminhhuy.mydiary.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.nguyendinhminhhuy.mydiary.R;
import com.nguyendinhminhhuy.mydiary.adapters.DiariesAdapter;
import com.nguyendinhminhhuy.mydiary.database.DiariesDatabase;
import com.nguyendinhminhhuy.mydiary.entities.Diary;
import com.nguyendinhminhhuy.mydiary.listeners.DiariesListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DiariesListener {

    public static final int REQUEST_CODE_ADD_DIARY = 1;
    public static final int REQUEST_CODE_UPDATE_DIARY = 2;
    public static final int REQUEST_CODE_SHOW_DIARIES = 3;

    private RecyclerView diariesRecyclerView;
    private List<Diary> diaryList;
    private DiariesAdapter diariesAdapter;

    private int diaryClickedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageAddDiaryMain = findViewById(R.id.imageAddDiaryMain);

        imageAddDiaryMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(),CreateDiaryActivity.class),
                        REQUEST_CODE_ADD_DIARY
                );
            }
        });

        diariesRecyclerView = findViewById(R.id.diaryRecyclerView);
        diariesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        );

        diaryList = new ArrayList<>();
        diariesAdapter = new DiariesAdapter(diaryList, this);
        diariesRecyclerView.setAdapter(diariesAdapter);

        getDiaries(REQUEST_CODE_SHOW_DIARIES, false);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                diariesAdapter.cancelTimer();

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(diaryList.size() != 0){
                    diariesAdapter.searchDiaries(s.toString());
                }
            }
        });
    }

    @Override
    public void onDiaryClicked(Diary diary, int position) {
        diaryClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(),CreateDiaryActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("diary", diary);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_DIARY);

    }

    private void getDiaries(final int requestCode, final boolean isDiaryDeleted){

        @SuppressLint("StaticFieldLeak")
        class GetDiariesTask extends AsyncTask<Void, Void, List<Diary>>{

            @Override
            protected List<Diary> doInBackground(Void... voids) {
                return DiariesDatabase
                        .getDatabase(getApplicationContext())
                        .diaryDao().getAllDiaries();
            }

            @Override
            protected void onPostExecute(List<Diary> diaries) {
                super.onPostExecute(diaries);
               // Log.d("MY_DIARIES", diaries.toString());
//                if(diaryList.size()==0){
//                    diaryList.addAll(diaries);
//                    diariesAdapter.notifyDataSetChanged();
//                } else {
//                    diaryList.add(0, diaries.get(0));
//                    diariesAdapter.notifyItemInserted(0);
//                }
//                diariesRecyclerView.smoothScrollToPosition(0);
                if(requestCode == REQUEST_CODE_SHOW_DIARIES){
                    diaryList.addAll(diaries);
                    diariesAdapter.notifyDataSetChanged();
                } else if(requestCode == REQUEST_CODE_ADD_DIARY){
                    diaryList.add(0, diaries.get(0));
                    diariesAdapter.notifyItemInserted(0);
                    diariesRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_DIARY) {
                    diaryList.remove(diaryClickedPosition);
                    if(isDiaryDeleted){
                        diariesAdapter.notifyItemChanged(diaryClickedPosition);
                    } else {
                        diaryList.add(diaryClickedPosition,diaries.get(diaryClickedPosition));
                        diariesAdapter.notifyItemChanged(diaryClickedPosition);
                    }
                }

            }
        }

        new GetDiariesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_DIARY && resultCode == RESULT_OK){
            getDiaries(REQUEST_CODE_ADD_DIARY,false);
        } else if(requestCode == REQUEST_CODE_UPDATE_DIARY && resultCode == RESULT_OK){
            if(data != null){
                getDiaries(REQUEST_CODE_UPDATE_DIARY, data.getBooleanExtra("isDiaryDeleted",false));
            }
        }
    }
}
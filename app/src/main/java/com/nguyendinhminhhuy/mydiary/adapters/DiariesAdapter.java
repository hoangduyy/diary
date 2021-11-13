package com.nguyendinhminhhuy.mydiary.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nguyendinhminhhuy.mydiary.R;
import com.nguyendinhminhhuy.mydiary.entities.Diary;
import com.nguyendinhminhhuy.mydiary.listeners.DiariesListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DiariesAdapter extends RecyclerView.Adapter<DiariesAdapter.DiaryViewHolder> {

    private List<Diary> diaries;
    private DiariesListener diariesListener;
    private Timer timer;
    private List<Diary> diariesSource;




    public DiariesAdapter(List<Diary> diaries, DiariesListener diariesListener) {
        this.diaries = diaries;
        this.diariesListener = diariesListener;
        diariesSource = diaries;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DiaryViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_diary,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.setDiary(diaries.get(position));
        holder.layoutDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diariesListener.onDiaryClicked(diaries.get(position),position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return diaries.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class DiaryViewHolder extends RecyclerView.ViewHolder{

        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutDiary;
        RoundedImageView imageDiary;

        DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutDiary = itemView.findViewById(R.id.layoutDiary);
            imageDiary = itemView.findViewById(R.id.imageDiary);
        }

        void setDiary(Diary diary){
            textTitle.setText(diary.getTitle());
            if(diary.getSubtitle().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            } else {
                textSubtitle.setText(diary.getSubtitle());
            }

            textDateTime.setText(diary.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutDiary.getBackground();
            if(diary.getColor() != null){
                gradientDrawable.setColor(Color.parseColor(diary.getColor()));
            } else {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }

            if(diary.getImage_path() != null){
                imageDiary.setImageBitmap(BitmapFactory.decodeFile(diary.getImage_path()));
                imageDiary.setVisibility(View.VISIBLE);
            } else {
                imageDiary.setVisibility(View.GONE);
            }

        }
    }

    public void searchDiaries(final String searchKeyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyword.trim().isEmpty()){
                    diaries = diariesSource;
                } else {
                    ArrayList<Diary> temp = new ArrayList<>();
                    for(Diary diary: diariesSource){
                        if(diary.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                        || diary.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                        || diary.getNote_text().toLowerCase().contains(searchKeyword.toLowerCase())
                        || diary.getDateTime().toLowerCase().contains(searchKeyword.toLowerCase())){
                            temp.add(diary);
                        }
                    }
                    diaries = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable(){
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer(){
        if(timer != null){
            timer.cancel();
        }
    }
}

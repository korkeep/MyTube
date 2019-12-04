package com.example.swonlinelectureapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentStore extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_store, container, false);
        //현재 DB에 저장된 정보(썸네일, 날짜, 제목) 출력
        //화면 클릭하면 영상 재생(비디오 ID를 Intent로, PlayActivity에서 재생)
        //Like 클릭하면 리스트 삭제, 위로 정렬기능
        return v;
    }
}

 /*
                    else {
                        //PlayList 삭제 구현

                        like.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_dark));
                        Toast.makeText(StartActivity.this, "보관함에서 삭제", Toast.LENGTH_SHORT).show();
                    }*/

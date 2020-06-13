package com.example.swonlinelectureapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class FragmentStore extends Fragment {

    private Context context;
    private ListView playlist;
    //검색 기능 구현에 필요한 Parameter
    static DrawableManager DM = new DrawableManager();
    ArrayList<SearchData> pdata = new ArrayList<SearchData>();
    AsyncTask<?, ?, ?> printTask;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_store, container, false);
        context = container.getContext();
        playlist = (ListView) v.findViewById(R.id.playlist);
        printTask = new printTask().execute();

        return v;
    }

    //Fragment 업데이트
    private void refresh(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.detach(this).attach(this).commit();
    }

    //출력 기능 구현
    @SuppressLint("StaticFieldLeak")
    private class printTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            pdata.clear();
            final DBHelper dbHelper = new DBHelper(getActivity(), "Video_Data.db", null, 1);

            //제목 순, 날짜 순, 추가 순 정렬
            String result = dbHelper.getResult_title();

            //DB 읽어오기
            if(!result.equals("")){
                String[] line = result.split("\n");
                String[] subStr;
                SearchData temp;

                for(int i=0; i<line.length;i++) {
                    subStr = line[i].split("\t");
                    temp = new SearchData(subStr[0], subStr[1], subStr[2], subStr[3]);
                    Log.v("View Temp", subStr[0] + ' ' + subStr[1] + ' ' + subStr[2] + ' ' + subStr[3]);
                    pdata.add(temp);
                }
            }
            return null;
        }

        //DB에서 읽어온 데이터를 이용해 리스트 만들기
        @Override
        protected void onPostExecute(Void result) {
            FragmentStore.StoreListAdapter mAdapter = new StoreListAdapter(context, R.layout.listview_play, pdata);
            playlist.setAdapter(mAdapter);
        }
    }

    //현재 DB에 저장된 정보(썸네일, 날짜, 제목) 출력
    //화면 클릭하면 영상 재생(비디오 ID를 Intent로, PlayActivity에서 재생)
    //Like 클릭하면 리스트 삭제, 위로 정렬기능
    public class StoreListAdapter extends ArrayAdapter<SearchData> {
        private ArrayList<SearchData> items;
        SearchData fInfo;

        //DB 관련
        final DBHelper dbHelper = new DBHelper(getActivity(), "Video_Data.db", null, 1);

        //ListView 세팅함수
        public StoreListAdapter(Context context, int textViewResourseId, ArrayList<SearchData> items) {
            super(context, textViewResourseId, items);
            this.items = items;
        }

        //ListView 출력함수
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            fInfo = items.get(position);
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listview_play, null);

            //썸네일 이미지
            final String url = fInfo.getUrl();
            ImageView img = (ImageView) v.findViewById(R.id.img);
            DM.fetchDrawableOnThread(url, img);

            //Video ID Intent 전달
            final String VideoID = fInfo.getVideoId();

            //제목 설정
            final String Title = fInfo.getTitle();
            ((TextView) v.findViewById(R.id.title)).setText(Title);

            //날짜 설정
            final String Date = fInfo.getPublishedAt();
            ((TextView) v.findViewById(R.id.date)).setText(Date);

            //like 버튼 클릭 시 like 버튼 비활성화
            final ToggleButton like = (ToggleButton) v.findViewById(R.id.like);
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // DB에 레코드가 존재한다면 like를 체크 상태로 전환
                    if(dbHelper.getResult_videoId(VideoID)) like.setChecked(true);
                    else like.setChecked(false);

                    //DB에서 삭제
                    if(like.isChecked()) {
                        //Like 클릭시 하트 비워짐, 토스트메시지
                        like.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_dark));
                        Toast.makeText(context, "보관함에서 삭제", Toast.LENGTH_SHORT).show();
                        dbHelper.delete(VideoID);

                        //해당 Fragment 다시 load
                        //TODO 자연스러운 애니메이션으로 삭제하는 방법 있으면 추가하자
                        refresh();
                    }
                }
            });

            //썸네일, 제목 클릭 시 동영상 실행
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 재생 횟수 증가
                    dbHelper.update_played(VideoID);

                    Intent intent = new Intent(getActivity(), PlayActivity.class);
                    intent.putExtra("id", VideoID);
                    startActivity(intent); //리스트 터치시 재생하는 엑티비티로 이동. 동영상 아이디를 넘겨줌.
                }
            });
            return v;
        }
    }
}
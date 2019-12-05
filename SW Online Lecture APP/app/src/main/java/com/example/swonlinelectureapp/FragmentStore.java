package com.example.swonlinelectureapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    //출력 기능 구현
    @SuppressLint("StaticFieldLeak")
    private class printTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //리스트 터치후, 재생에서 뒤로가기 하면 리스트 하나 더 중복해서 보이는 것 해결...(.txt 파일은 문제없음)
            pdata.clear();

            File file = new File(context.getFilesDir(), "myTubeData.txt");
            FileReader fr = null;
            BufferedReader buf = null;
            String line;
            String[] subStr;

            //myTubeData.txt 파일 읽어오기
            if(file.exists()){
                try{
                    line = null;
                    fr = new FileReader(file);
                    buf = new BufferedReader(fr);

                    while((line = buf.readLine()) != null){

                        subStr = null;
                        subStr = line.split("\t");

                        for(int i=0; i<subStr.length; i++){
                            subStr[i]=subStr[i].trim();
                        }

                        //썸네일 이미지 + 날짜 + 제목 + Video ID순으로 tab로 구분하여 저장된다
                        //pdata에 저장
                        SearchData temp = new SearchData(subStr[3], subStr[2], subStr[0], subStr[1]);
                        pdata.add(temp);
                    }
                    buf.close();
                    fr.close();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        //.txt에서 읽어온 유튜브 데이터를 이용해서 리스트를 만들어줍니다.
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

            //썸네일, 제목 클릭 시 동영상 실행
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PlayActivity.class);
                    intent.putExtra("id", VideoID);
                    startActivity(intent); //리스트 터치시 재생하는 엑티비티로 이동. 동영상 아이디를 넘겨줌.
                }
            });

            //제목 설정
            final String Title = fInfo.getTitle();
            ((TextView) v.findViewById(R.id.title)).setText(Title);

            //날짜 설정
            final String Date = fInfo.getPublishedAt();
            ((TextView) v.findViewById(R.id.date)).setText(Date);

            //Like 버튼
            final ToggleButton like = (ToggleButton) v.findViewById(R.id.like);
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // if 조건문에서 정확하게는 보관함 List Video ID와 비교해봐야
                    if(like.isChecked()) {

                        //썸네일 이미지 + 날짜 + 제목 + Video ID순으로 tab로 구분하여 저장된다
                        String temp = url + '\t' + Date + '\t' + Title + '\t' +  VideoID;

                        //myTubeData.txt 파일 삭제 기능 구현(읽기, 쓰기 모두)
                        /*try{
                            BufferedWriter buf = new BufferedWriter(new FileWriter(saveFile + "/myTubeData.txt", true));
                            buf.append(temp); //쓰고
                            buf.newLine(); //end line
                            buf.close(); //닫는다
                        }catch(FileNotFoundException e){
                            e.printStackTrace();
                        }catch(IOException e){
                            e.printStackTrace();
                        }*/

                        //Like 클릭시 하트 비워짐, 토스트메시지
                        like.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_dark));
                        Toast.makeText(context, "보관함에서 삭제", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return v;
        }
    }
}
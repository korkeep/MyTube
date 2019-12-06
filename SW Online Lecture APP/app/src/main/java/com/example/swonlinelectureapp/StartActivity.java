package com.example.swonlinelectureapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StartActivity extends AppCompatActivity {

    //썸네일에 필요한 Parameter
    static DrawableManager DM = new DrawableManager();
    // String은 공백을 끝으로 인식하기 때문에 다른 대안 필요하다
    String search_item;

    AsyncTask<?, ?, ?> searchTask;
    ArrayList<SearchData> sdata = new ArrayList<SearchData>();
    final String serverKey="AIzaSyBg-eEaLFpQN1scxt5HWA1vADzTKyKE6B0";
    //final String serverKey="AIzaSyBdARQznrjtHIflil6qtPdPdjMy2MdFWTU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //액션바 글자 없애고, 뒤로가기 생성
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        //Home에서 넘겨받은 String
        Intent intent = getIntent();
        search_item = intent.getExtras().getString("search_item");
        searchTask = new searchTask().execute();
    }

    //상단 부분
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar, menu);
        //검색기능 구현
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //검색어 입력시
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            //검색어 완료시
            @Override
            public boolean onQueryTextChange(String s) {
                search_item = s;
                searchTask = new searchTask().execute();
                return false;
            }
        });
        return true;
    }

    //뒤로가기 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                //뒤로가기 동작
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //검색 기능 구현
    @SuppressLint("StaticFieldLeak")
    private class searchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                paringJsonData(getUtube());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ListView searchlist = (ListView) findViewById(R.id.searchlist);
            StoreListAdapter mAdapter = new StoreListAdapter(
                    StartActivity.this, R.layout.listview_start, sdata); //Json파싱해서 가져온 유튜브 데이터를 이용해서 리스트를 만들어줍니다.
            searchlist.setAdapter(mAdapter);
        }
    }

    //Http 통신 프로토콜 이용 파싱 함수
    public JSONObject getUtube() {
        //최대 10개까지 검색해옴(너무 많으면 API Key 사용한도 초과함)
        HttpGet httpGet = new HttpGet(
                "https://www.googleapis.com/youtube/v3/search?"
                        + "part=snippet&maxResults=10&q=" + search_item
                        + "&key="+ serverKey);
        // part(snippet),  q(검색값) , key(서버키)
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            //예외처리
        } catch (IOException e) {
            //예외처리
        }

        //JSon 형식의 Youtube API 파일 읽어오기
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    //파싱 결과(Video ID, 제목, 썸네일, 날짜)
    private void paringJsonData(JSONObject jsonObject) throws JSONException {
        sdata.clear();

        JSONArray contacts = jsonObject.getJSONArray("items");

        for (int i = 0; i < contacts.length(); i++) {
            JSONObject c = contacts.getJSONObject(i);
            String VideoID = c.getJSONObject("id").getString("videoId");
            String title = c.getJSONObject("snippet").getString("title");
            String changeString = "";
            try {
                changeString = new String(title.getBytes("8859_1"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String imgUrl = c.getJSONObject("snippet").getJSONObject("thumbnails")
                    .getJSONObject("default").getString("url");
            String date = c.getJSONObject("snippet").getString("publishedAt")
                    .substring(0, 10);

            sdata.add(new SearchData(VideoID, changeString, imgUrl, date));
        }
    }


    //만약 검색 결과에서, 보관함에 동일한 Video ID가 존재한다면?? 요때 Like 색깔 맞춰주기


    //파싱 결과로 LIstView 구성
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

            //convertView가 맞나??
            View v = convertView;
            fInfo = items.get(position);
            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listview_start, null);

            //썸네일 이미지
            ImageView img = (ImageView) v.findViewById(R.id.img);
            String url = fInfo.getUrl();
            String sUrl = "";
            String eUrl = "";
            sUrl = url.substring(0, url.lastIndexOf("/") + 1);
            eUrl = url.substring(url.lastIndexOf("/") + 1, url.length());
            try {
                eUrl = URLEncoder.encode(eUrl, "EUC-KR").replace("+", "%20");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            final String new_url = sUrl + eUrl;
            DM.fetchDrawableOnThread(new_url, img);

            //Video ID 설정
            v.setTag(position);
            final String VideoID = items.get((Integer) v.getTag()).getVideoId();

            //썸네일, 제목 클릭 시 동영상 실행
            /*v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StartActivity.this, PlayActivity.class);
                    intent.putExtra("id", VideoID);
                    startActivity(intent); //리스트 터치시 재생하는 엑티비티로 이동. 동영상 아이디를 넘겨줌.
                }
            });*/

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

                        //썸네일 이미지 + 날짜 + 제목 + Video ID순으로 \t으로 구분하여 저장된다
                        String temp = new_url + "\t" + Date + "\t" + Title + "\t" +  VideoID;

                        File file = new File(getFilesDir(), "myTubeData.txt");
                        FileWriter fw = null;
                        BufferedWriter buf = null;

                        //myTubeData.txt 에 동영상 정보 추가
                        try{
                            fw = new FileWriter(file, true); //기존 Data 추가쓰기
                            //fw = new FileWriter(file); //기존 Data 덮어쓰기
                            buf = new BufferedWriter(fw);
                            buf.append(temp); //쓰고
                            buf.newLine(); //end line
                            buf.flush(); //비운다
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        try {
                            if (buf != null) {
                                buf.close();
                            }
                            if (fw != null) {
                                fw.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //Like 클릭시 하트 채워짐, 토스트메시지
                        like.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_gray));
                        Toast.makeText(StartActivity.this, "보관함에 추가", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //썸네일, 제목 클릭 시 Like 버튼 활성화
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // '!' 이 조건절에 붙었음
                    if(!like.isChecked()) {

                        //썸네일 이미지 + 날짜 + 제목 + Video ID순으로 \t으로 구분하여 저장된다
                        String temp = new_url + "\t" + Date + "\t" + Title + "\t" +  VideoID;

                        File file = new File(getFilesDir(), "myTubeData.txt");
                        FileWriter fw = null;
                        BufferedWriter buf = null;

                        //myTubeData.txt 에 동영상 정보 추가
                        try{
                            //fw = new FileWriter(file, true); //기존 Data 추가쓰기
                            fw = new FileWriter(file); //기존 Data 덮어쓰기
                            buf = new BufferedWriter(fw);
                            buf.append(temp); //쓰고
                            buf.newLine(); //end line
                            buf.flush(); //비운다
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        try {
                            if (buf != null) {
                                buf.close();
                            }
                            if (fw != null) {
                                fw.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //Like 클릭시 하트 채워짐, 토스트메시지
                        like.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_gray));
                        Toast.makeText(StartActivity.this, "보관함에 추가", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return v;
        }
    }
}

package com.example.swonlinelectureapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class FragmentRecommend extends Fragment {
    private Context context;
    private EditText searchOption;
    private ListView playlist;
    private String select = "";
    private String keyword = "";
    private Spinner spinner;
    //private int index;

    //검색 기능 구현에 필요한 Parameter
    static DrawableManager DM = new DrawableManager();
    ArrayList<SearchData> pdata = new ArrayList<SearchData>();
    AsyncTask<?, ?, ?> printTask;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recommend, container, false);
        context = container.getContext();
        playlist = (ListView)v.findViewById(R.id.playlist);
        spinner = (Spinner) v.findViewById(R.id.recommendSpinner);

        //Spinner 관련
        final String[] data = getResources().getStringArray(R.array.recommendList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, data);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("[index]", String.valueOf(i));
                select = (String) adapterView.getItemAtPosition(i);
                printTask = new FragmentRecommend.printTask().execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                refresh();
            }
        });

        //검색 기능
        searchOption = v.findViewById(R.id.search_option);
        searchOption.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchOption.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        return v;
    }

    //검색 기능 구현
    public void performSearch() {
        searchOption.clearFocus();
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(searchOption.getWindowToken(), 0);

        //EditText내에서 검색한 string 결과 검색
        keyword = searchOption.getText().toString();
        printTask = new FragmentRecommend.printTask().execute();
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

            //DB 관련
            String getString  = "";
            final DBHelper dbHelper = new DBHelper(getActivity(), "Video_Data.db", null, 1);

            //키워드 검색, 그룹 검색 정렬
            if (select.equals("그룹 검색"))
                getString = dbHelper.getResult_groupName(keyword);
            else {
                getString = dbHelper.getResult_search(keyword);
            }

            //DB 읽어오기
            if(!getString.equals("")){
                String[] line = getString.split("\n");
                String[] subStr;
                SearchData temp;

                for(int i=0; i<line.length;i++) {
                    subStr = line[i].split("\t");
                    temp = new SearchData(subStr[0], subStr[1], subStr[2], subStr[3]);
                    pdata.add(temp);
                }
            }
            return null;
        }

        //DB에서 읽어온 데이터를 이용해 리스트 만들기
        @Override
        protected void onPostExecute(Void result) {
            FragmentRecommend.StoreListAdapter mAdapter = new FragmentRecommend.StoreListAdapter(context, R.layout.listview_play, pdata);
            playlist.setAdapter(mAdapter);
        }
    }

    //현재 DB에 저장된 정보(썸네일, 날짜, 제목) 출력
    //화면 클릭하면 영상 재생(비디오 ID를 Intent로, PlayActivity에서 재생)
    //Check 클릭하면 그룹 추가 + 삭제
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

            //Check 버튼 클릭 시 check 버튼 비활성화
            final ToggleButton check = (ToggleButton) v.findViewById(R.id.like);

            // DB에 GROUP_NAME이 NULL이 아니라면 check를 채워진 체크 모양으로 전환
            if(dbHelper.getResult_groupNotNULL(VideoID, keyword)){
                check.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_gray));
            } else{
                check.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_dark));
            }

            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 그룹명이 NULL이 아니라면 check를 체크 상태로 전환
                    if(dbHelper.getResult_groupNotNULL(VideoID, keyword)) check.setChecked(true);
                    else check.setChecked(false);

                    //GROUP_NAME 추가
                    if(!check.isChecked()){
                        //그룹명 추가 팝업 띄우기
                        final EditText txtEdit = new EditText(context);

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                        dialogBuilder.setTitle( "그룹 이름을 설정해주세요." );
                        dialogBuilder.setView( txtEdit );
                        dialogBuilder.setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick( DialogInterface dialog, int which) {
                                        String input = txtEdit.getText().toString();
                                        // 입력된 문자열 그룹 명으로 삽입
                                        dbHelper.update_groupName(VideoID, input);
                                    }
                                });
                        dialogBuilder.setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //체크박스 원상태로 복귀
                                        check.setChecked(false);
                                    }
                                });
                        dialogBuilder.show();
                    }
                    //GROUP_NAME 삭제
                    else {
                        //check 클릭시 체크 비워짐, 토스트메시지
                        check.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_dark));
                        Toast.makeText(context, "그룹에서 삭제", Toast.LENGTH_SHORT).show();
                        dbHelper.delete_groupName(VideoID);
                    }
                    //해당 Fragment 다시 load
                    //TODO 자연스러운 애니메이션으로 삭제하는 방법 있으면 추가하자
                    refresh();
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
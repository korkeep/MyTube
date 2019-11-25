package com.example.swonlinelectureapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //액션바 글자 없애고, 뒤로가기 생성
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        //Home에서 넘겨받은 String
        Intent intent = getIntent();
        String search_item = intent.getExtras().getString("search_item");

        //테스트 부분(나중에 지워야)
        TextView t1 = findViewById(R.id.result);
        t1.setText(search_item);
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
                //테스트 부분(나중에 지워야)
                TextView t1 = findViewById(R.id.result);
                t1.setText(s);

                return false;
            }
        });
        return true;
    }

    //뒤로가기 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                //뒤로가기 동작
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.swonlinelectureapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //액션바 글자 없애고, 뒤로가기 버튼 생성
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    //검색버튼 클릭했을때 동작
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_item, menu);
        /*
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.edittext_search_video));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //검색어 입력시
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            //검색어 완료시
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });*/
        return true;
    }

    //뒤로가기 클릭했을때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Toast.makeText(this, "뒤로가기 클릭", Toast.LENGTH_SHORT).show();
            //뒤로가기 구현해야댕
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.example.swonlinelectureapp;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //중간 Frame 구성
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentRecommend fragmentRecommend = new FragmentRecommend();
    private FragmentStore fragmentStore = new FragmentStore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //액션바 글자 없애고, 뒤로가기 생성
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        //중간 내용
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();

        //하단 메뉴 구성 바
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    }

    //상단 부분
    //로고 삽입
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_bar, menu);
        /* 검색기능 구현
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

    //뒤로가기메뉴, 홈으로 이동 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                //뒤로가기 동작
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.go_home:
                //홈으로 이동 동작
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //하단 메뉴 부분
    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.homeItem:
                    transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
                    break;
                case R.id.recommendItem:
                    transaction.replace(R.id.frameLayout, fragmentRecommend).commitAllowingStateLoss();
                    break;
                case R.id.storeItem:
                    transaction.replace(R.id.frameLayout, fragmentStore).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
}
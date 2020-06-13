package com.example.swonlinelectureapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {
    //DBHelper 생성자 (관리할 DB 이름, 버전 정보)
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //새로운 테이블 생성 (VIDEO_ID, TITLE, URL, PUBLISHED_AT, LIKE_AT, GROUP_NAME, PLAYED)
        sqLiteDatabase.execSQL("CREATE TABLE VIDEO_DATA (VIDEO_ID TEXT PRIMARY KEY, TITLE TEXT, URL TEXT, PUBLISHED_AT DATE, LIKE_AT DATE, GROUP_NAME TEXT, PLAYED INTEGER);");
    }
    //DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // 레코드 추가 (StartActivity)
    public void insert(String videoId, String title, String url, Date publishedAt, Date likeAt) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // 레코드 추가, Primary Key를 VIDEO_ID로 두면서 중복 처리
        db.execSQL("INSERT INTO VIDEO_DATA VALUES('" +videoId+"', '"+title+"', '"+url+"', '"+publishedAt+"', '"+likeAt+"', null, 0);");
        Log.v("Insert", "레코드가 추가되었습니다.");
        Log.v("Result", getResult_title());
        db.close();
    }

    // 레코드 저장여부 반환 함수 (StartActivity)
    public boolean getResult_videoId(String videoId) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // 레코드 가져오기 (VIDEO_ID, TITLE, URL, PUBLISHED_AT, LIKE_AT, GROUP_NAME, PLAYED)
        Cursor cursor = db.rawQuery("SELECT VIDEO_ID FROM VIDEO_DATA WHERE VIDEO_ID=?", new String[]{videoId});
        while (cursor.moveToNext()) {
            result += cursor.getString(0);   //VIDEO_ID
            Log.v("Result", result);
        }

        if(videoId.equals(result)){
            Log.v("Stored Item Detected", "이미 저장된 비디오입니다.");
            return true;
        } else {
            Log.v("No Stored Item", "이미 저장된 비디오가 없습니다.");
            return false;
        }
    }

    // 레코드 재생 횟수 추가 (FragmentStore)
    public void update_p(String videoId) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        String temp ="";
        // 현재 재생 횟수 가져오기
        Cursor cursor = db.rawQuery("SELECT PLAYED FROM VIDEO_DATA WHERE VIDEO_ID=?", new String[] {videoId});
        while (cursor.moveToNext()) {
            temp = cursor.getString(0);
        }
        int result = Integer.parseInt(temp) + 1;

        // 레코드 재생 횟수 추가
        db.execSQL("UPDATE VIDEO_DATA SET PLAYED=" + result + " WHERE VIDEO_ID='" + videoId + "';");
        Log.v("Update Played", "재생 횟수가 증가되었습니다.");
        db.close();
    }

    // 레코드 그룹 명 추가 (FragmentStore)
    public void update_g(String videoId, String groupName) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        String result = groupName;

        // 레코드 그룹명 추가
        db.execSQL("UPDATE VIDEO_DATA SET GROUP_NAME=" + result + " WHERE VIDEO_ID='" + videoId + "';");
        Log.v("Update Group", "그룹 이름이 추가되었습니다.");
        db.close();
    }

    // 레코드 삭제 (FragmentStore)
    public void delete(String videoId) {
        SQLiteDatabase db = getWritableDatabase();
        // 레코드 삭제
        db.execSQL("DELETE FROM VIDEO_DATA WHERE VIDEO_ID='" + videoId + "';");
        Log.v("Delete", "레코드가 삭제되었습니다.");
        db.close();
    }

    // 제목 순으로 레코드 출력 (FragmentStore)
    public String getResult_title() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // 레코드 가져오기 (VIDEO_ID, TITLE, URL, PUBLISHED_AT, LIKE_AT, GROUP_NAME, PLAYED)
        Cursor cursor = db.rawQuery("SELECT * FROM VIDEO_DATA ORDER BY TITLE", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)   //VIDEO_ID
                    + '\t'
                    + cursor.getString(1)   //TITLE
                    + '\t'
                    + cursor.getString(2)   //URL
                    + '\t'
                    + cursor.getString(3)   //PUBLISHED_AT
                    + '\t'
                    + cursor.getString(4)   //LIKE_AT
                    + '\t'
                    + cursor.getString(5)   //GROUP_NAME
                    + '\t'
                    + cursor.getInt(6)      //PLAYED
                    + '\t';
        }
        Log.v("Title", "제목 순서로 출력되었습니다.");
        return result;
    }

    // 동영상 날짜 순으로 레코드 출력 (FragmentStore)
    public String getResult_publishedAt() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // 레코드 가져오기 (VIDEO_ID, TITLE, URL, PUBLISHED_AT, LIKE_AT, GROUP_NAME, PLAYED)
        Cursor cursor = db.rawQuery("SELECT * FROM VIDEO_DATA ORDER BY PUBLISHED_AT", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)   //VIDEO_ID
                    + '\t'
                    + cursor.getString(1)   //TITLE
                    + '\t'
                    + cursor.getString(2)   //URL
                    + '\t'
                    + cursor.getString(3)   //PUBLISHED_AT
                    + '\t'
                    + cursor.getString(4)   //LIKE_AT
                    + '\t'
                    + cursor.getString(5)   //GROUP_NAME
                    + '\t'
                    + cursor.getInt(6)      //PLAYED
                    + '\t';
        }
        Log.v("PublishedAt", "동영상 날짜 순서로 출력되었습니다.");
        return result;
    }

    // 좋아요 날짜 순으로 레코드 출력 (FragmentStore)
    public String getResult_likeAt() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // 레코드 가져오기 (VIDEO_ID, TITLE, URL, PUBLISHED_AT, LIKE_AT, GROUP_NAME, PLAYED)
        Cursor cursor = db.rawQuery("SELECT * FROM VIDEO_DATA ORDER BY LIKE_AT", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)   //VIDEO_ID
                    + '\t'
                    + cursor.getString(1)   //TITLE
                    + '\t'
                    + cursor.getString(2)   //URL
                    + '\t'
                    + cursor.getString(3)   //PUBLISHED_AT
                    + '\t'
                    + cursor.getString(4)   //LIKE_AT
                    + '\t'
                    + cursor.getString(5)   //GROUP_NAME
                    + '\t'
                    + cursor.getInt(6)      //PLAYED
                    + '\t';
        }
        Log.v("LikeAt", "좋아요 날짜 순서로 출력되었습니다.");
        return result;
    }

    // 플레이 횟수 Top 'N' 레코드 출력 (FragmentStore)
    public String getResult_played(int top) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        int n = 0;

        // 레코드 가져오기 (VIDEO_ID, TITLE, URL, PUBLISHED_AT, LIKE_AT, GROUP_NAME, PLAYED)
        Cursor cursor = db.rawQuery("SELECT * FROM VIDEO_DATA ORDER BY PLAYED ASC", null);
        while (cursor.moveToNext() && n++<top) {
            result += cursor.getString(0)   //VIDEO_ID
                    + '\t'
                    + cursor.getString(1)   //TITLE
                    + '\t'
                    + cursor.getString(2)   //URL
                    + '\t'
                    + cursor.getString(3)   //PUBLISHED_AT
                    + '\t'
                    + cursor.getString(4)   //LIKE_AT
                    + '\t'
                    + cursor.getString(5)   //GROUP_NAME
                    + '\t'
                    + cursor.getInt(6)      //PLAYED
                    + '\t';
        }
        Log.v("Played", "Top N만큼 재생된 순서로 출력되었습니다.");
        return result;
    }

    // 키워드 검색 레코드 출력 (FragmentStore)
    public String getResult_search(String search) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // 레코드 가져오기 (VIDEO_ID, TITLE, URL, PUBLISHED_AT, LIKE_AT, GROUP_NAME, PLAYED)
        Cursor cursor = db.rawQuery("SELECT * FROM VIDEO_DATA WHERE TITLE='*'+?+'*'", new String[] {search});
        while (cursor.moveToNext()) {
            result += cursor.getString(0)   //VIDEO_ID
                    + '\t'
                    + cursor.getString(1)   //TITLE
                    + '\t'
                    + cursor.getString(2)   //URL
                    + '\t'
                    + cursor.getString(3)   //PUBLISHED_AT
                    + '\t'
                    + cursor.getString(4)   //LIKE_AT
                    + '\t'
                    + cursor.getString(5)   //GROUP_NAME
                    + '\t'
                    + cursor.getInt(6)      //PLAYED
                    + '\t';
        }
        Log.v("Search", "키워드 검색 결과가 출력되었습니다.");
        return result;
    }

    // 그룹 레코드 출력 (FragmentStore)
    public String getResult_groupName(String groupName) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // 레코드 가져오기 (VIDEO_ID, TITLE, URL, PUBLISHED_AT, LIKE_AT, GROUP_NAME, PLAYED)
        Cursor cursor = db.rawQuery("SELECT * FROM VIDEO_DATA WHERE GROUP_NAME=?", new String[] {groupName});
        while (cursor.moveToNext()) {
            result += cursor.getString(0)   //VIDEO_ID
                    + '\t'
                    + cursor.getString(1)   //TITLE
                    + '\t'
                    + cursor.getString(2)   //URL
                    + '\t'
                    + cursor.getString(3)   //PUBLISHED_AT
                    + '\t'
                    + cursor.getString(4)   //LIKE_AT
                    + '\t'
                    + cursor.getString(5)   //GROUP_NAME
                    + '\t'
                    + cursor.getInt(6)      //PLAYED
                    + '\t';
        }
        Log.v("Group", "그룹 결과가 출력되었습니다.");
        return result;
    }
}

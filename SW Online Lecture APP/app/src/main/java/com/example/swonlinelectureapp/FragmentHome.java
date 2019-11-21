package com.example.swonlinelectureapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class FragmentHome extends Fragment {

    private EditText searchBar;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //검색 기능
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        searchBar = v.findViewById(R.id.search_bar);
        searchBar.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        searchBar.clearFocus();
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);

        //EditText내에서 검색한 string 결과 Intent로 전달
        String temp = searchBar.getText().toString();
        Intent intent = new Intent(getActivity().getApplicationContext(), StartActivity.class);
        intent.putExtra("search_item", temp);
        searchBar.setText("");
        startActivity(intent);
    }
}

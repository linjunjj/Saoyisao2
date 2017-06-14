package com.example.linjun.saoyisao;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Viewdata extends AppCompatActivity {

    @BindView(R.id.data)
    TextView data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewdata);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        data.setText(intent.getStringExtra("date"));

    }




}

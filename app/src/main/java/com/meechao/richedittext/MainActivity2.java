package com.meechao.richedittext;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

  private TEditText et;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main2);
    et = findViewById(R.id.et_content);
    findViewById(R.id.tv_0).setOnClickListener(this);
    findViewById(R.id.tv_1).setOnClickListener(this);
    findViewById(R.id.tv_2).setOnClickListener(this);
    findViewById(R.id.tv_3).setOnClickListener(this);

  }

  @Override public void onClick(View v) {

    switch (v.getId()) {
      case R.id.tv_0:
        et.insertTopic(" #程序猿[1|话题]# ");
        break;
      case R.id.tv_1:
        et.insertTopic(" #设计喵[1|话题]# ");
        break;
      case R.id.tv_2:
        et.insertTopic(" #攻城狮[1|话题]# ");
        break;
      case R.id.tv_3:
        et.insertTopic(" #单身汪[1|话题]# ");
        break;
    }
  }

}
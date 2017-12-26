package com.meechao.richedittext;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.meechao.richedittext.Redit.RActivity;
import com.meechao.richedittext.richText.RichActivity;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2017-12-25 17:55
 * Mail：jihaifeng@meechao.com
 */
public class MainActivity extends AppCompatActivity{

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void toRedit(View view) {
    startActivity(new Intent(this, RActivity.class));
  }

  public void toRichEdit(View view) {
    startActivity(new Intent(this, RichActivity.class));
  }
}

package com.meechao.richedittext.richText;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.meechao.richedittext.KeyboardUtils;
import com.meechao.richedittext.R;
import java.util.List;

public class RichActivity extends Activity {

  private RichEditText mREditText;
  private TextView mResult;
  private Button btnTopic, btnSet;
  private RadioGroup radioGroup;
  int id = 0;
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_rich);
    initViews();
  }

  public void initViews() {
    mREditText = (RichEditText) findViewById(R.id.rich_edit);
    mResult = (TextView) findViewById(R.id.result);
    btnTopic = (Button) findViewById(R.id.btn_topic);
    btnSet = (Button) findViewById(R.id.btn_set);
    radioGroup = (RadioGroup)  findViewById(R.id.rg_keyboard);
    Log.i("aaa", "initViews: " + radioGroup);
    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
          case R.id.rb_keybord_left:
            KeyboardUtils.hideSoftInput(RichActivity.this);
            radioGroup.getChildAt(1).setVisibility(View.GONE);
            radioGroup.getChildAt(2).setVisibility(View.VISIBLE);
            break;
          case R.id.rb_keybord_right:
            KeyboardUtils.showSoftInput(RichActivity.this);
            break;
        }
      }
    });



    btnSet.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        getTopicsData();
      }
    });

    btnTopic.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        // 设置话题
        //if (a > 5) {
        //  a = 0;
        //}
        //a++;
        switch (id % 2) {
          case 0:
            setTopic("#天气[1|话题]#");
            break;
          case 1:
            setTopic("#萨达[1|话题]#");
            break;
        }
        id++;

      }
    });
  }

  private String getEmojiStringByUnicode(int unicode){
    return new String(Character.toChars(unicode));
  }

  /**
   * 添加设置话题
   *
   * @param s 标签名
   */
  private void setTopic(String s) {

    RichObject topic = new RichObject();

    topic.setTopicContent(s);// 必须设置

    mREditText.setObject(topic);// 设置话题
  }

  /**
   * 获取话题列表数据
   *
   * @author Ruffian
   */
  private void getTopicsData() {

    /**
     * 获取话题对象集合,遍历
     */
    List<RichObject> list = mREditText.getObjects();// 获取话题对象集合
    if (list == null || list.size() == 0) {
      mResult.setText("no data");
      return;
    }

    RichObject myTopic;
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < list.size(); i++) {
      myTopic = list.get(i);// 强制转化为开发者topic类型

      mResult.setText(myTopic.getShowContent());
    }
  }
}

package com.meechao.richedittext.Redit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.meechao.richedittext.R;
import com.meechao.richedittext.richText.RichObject;
import java.util.List;

public class RActivity extends Activity implements OnClickListener {

  private CopyOfREditText mREditText;
  private TextView mResult;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_r);
    initViews();

  }

  public void initViews() {
    mREditText = (CopyOfREditText) findViewById(R.id.edittext);
    mResult = (TextView) findViewById(R.id.result);
    findViewById(R.id.topic_iv).setOnClickListener(this);
    findViewById(R.id.send).setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.topic_iv:
        // 设置话题
        setTopic();
        break;
      case R.id.send:
        // 展示话题对象内容
        getTopicsData();
        break;
    }

  }

  /**
   * 添加设置话题
   *
   * @author Ruffian
   */
  private void setTopic() {

    RichObject topic = new RichObject();
    int id = (int) (Math.random() * 100);
    topic.setObjectText("#天气[1|话题]#");// 必须设置

    //switch (id % 3) {
    //  case 0:
    //    topic.setObjectRule("*");// 开发者设置,默认#
    //    break;
    //  case 1:
    //    topic.setObjectRule("$");// 开发者设置,默认#
    //    break;
    //  case 2:
    //    topic.setObjectRule("#");// 开发者设置,默认#
    //    break;
    //}

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
    List<RObject> list = mREditText.getObjects();// 获取话题对象集合
    if (list == null || list.size() == 0) {
      mResult.setText("no data");
      return;
    }

    MyTopic myTopic;
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < list.size(); i++) {
      myTopic = (MyTopic) list.get(i);// 强制转化为开发者topic类型
      stringBuffer.append("id= " + myTopic.getId() + "    text= "
          + myTopic.getObjectText() + "\n");
    }
    mResult.setText(stringBuffer.toString());

  }

  /**
   * 测试使用开发者话题实体,必须继承RObject
   */
  class MyTopic extends RObject {
    private String id;

    // 其他属性...

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }

}

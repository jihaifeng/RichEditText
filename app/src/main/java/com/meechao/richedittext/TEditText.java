package com.meechao.richedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2018-01-04 19:37
 * Mail：jihaifeng@meechao.com
 */
public class TEditText extends AppCompatEditText {

  private static final String TAG = TEditText.class.getSimpleName().trim();

  private ArrayList<String> mTopicList = new ArrayList<>();

  private ArrayList<ForegroundColorSpan> mColorSpans = new ArrayList<>();
  private ArrayList<AbsoluteSizeSpan> mSizeSpans = new ArrayList<>();

  private TInputConnection inputConnection;

  // 默认的正则表达式
  private final String DEFAULT_REGEX = "\\s#[^#]*?\\[.*?\\|话题]#\\s";// 获取 标签内容 ,如 #天气[1|话题]#

  // 话题标签的文字大小
  private int textSizeTopic = 18;
  // 话题文字颜色 未选中
  private int textColorNormalTopic = Color.parseColor("#FFAA31");
  // 话题文字颜色 选中
  private int textColorSelectTopic = Color.parseColor("#50ffaa31");

  // 标签匹配的正则表达式
  private String topicRegex;

  // 是否是删除操作
  private boolean isDel = false;

  public TEditText(Context context) {
    this(context, null);
  }

  public TEditText(Context context, AttributeSet attrs) {
    super(context, attrs);

    // 获取自定义属性
    TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.TEditText);
    textSizeTopic = t.getDimensionPixelSize(R.styleable.TEditText_text_size_topic, textSizeTopic);
    textColorNormalTopic = t.getColor(R.styleable.TEditText_text_color_normal_topic, textColorNormalTopic);
    textColorSelectTopic = t.getColor(R.styleable.TEditText_text_color_select_topic, textColorSelectTopic);
    topicRegex = TextUtils.isEmpty(t.getString(R.styleable.TEditText_topic_regex)) ? DEFAULT_REGEX
        : t.getString(R.styleable.TEditText_topic_regex);

    // 用完后回收
    t.recycle();

    //设置选中颜色
    this.setHighlightColor(textColorSelectTopic);

    // 初始化退格键的监听
    inputConnection = new TInputConnection(null, true);

    initEvent();
  }

  private void initEvent() {

    /**
     * 监听删除键 <br/>
     * 1.光标在话题后面,将整个话题内容删除 <br/>
     * 2.光标在普通文字后面,删除一个字符
     */
    this.setBackSpaceListener(new TInputConnection.BackspaceListener() {
      @Override public boolean onBackspace() {
        return operateDelText();
      }
    });

    /**
     * 监听点击事件 <br/>
     * 注意：如果在页面中实现了该方法，则需要手动调用 {@link TEditText#changeSelectionPos()}方法
     */
    this.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        changeSelectionPos();
      }
    });

    /**
     * 监听输入框内容改变 <br/>
     * 注意：如果在页面中实现了该方法，则需要在{@link TextWatcher#onTextChanged(CharSequence, int, int, int)}方法中手动调用
     * {@link TEditText#refreshUI(String)}方法
     */
    this.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        refreshUI(charSequence.toString());
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
  }

  /**
   * 光标改变时的回调监听，处理输入框长按拖拽光标的问题
   *
   * @param selStart start
   * @param selEnd end
   */
  @Override protected void onSelectionChanged(int selStart, int selEnd) {
    super.onSelectionChanged(selStart, selEnd);
    if (!isDel) {
      changeSelectionPos();
    }
  }

  /**
   * 改变输入框内的文字样式，在 {@link TextWatcher#onTextChanged(CharSequence, int, int, int)} 方法中调用
   *
   * @param content 传入CharSequence.toString()
   */
  private void refreshUI(String content) {
    if (TextUtils.isEmpty(content)) {
      return;
    }
    // 查找话题
    mTopicList.clear();
    mTopicList.addAll(findTopic(content));

    // 查找到变色
    Editable editable = getText();

    // 刷新UI前先移除所有的样式
    for (int i = 0; i < mColorSpans.size(); i++) {
      editable.removeSpan(mColorSpans.get(i));
    }
    mColorSpans.clear();

    for (int i = 0; i < mSizeSpans.size(); i++) {
      editable.removeSpan(mSizeSpans.get(i));
    }
    mSizeSpans.clear();

    //为editable,中的话题加入colorSpan
    int findPos = 0;
    int size = mTopicList.size();
    for (int i = 0; i < size; i++) {
      //遍历话题
      String topic = mTopicList.get(i);
      findPos = content.indexOf(topic, findPos);
      if (findPos != -1) {
        // 改变话题标签文字颜色
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(textColorNormalTopic);
        editable.setSpan(colorSpan, findPos, findPos + topic.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        mColorSpans.add(colorSpan);

        // 改变话题标签文字大小
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(textSizeTopic, true);
        editable.setSpan(sizeSpan, findPos, findPos + topic.indexOf("["), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        mSizeSpans.add(sizeSpan);

        // 隐藏话题标签文字中的占位文字
        if (topic.contains("[") && topic.contains("]")) {
          AbsoluteSizeSpan _sizeSpan = new AbsoluteSizeSpan(0, true);
          editable.setSpan(_sizeSpan, findPos + topic.indexOf("["), findPos + topic.indexOf("]") + 1,
              Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

          mSizeSpans.add(_sizeSpan);

          // 修改占位文字后面的文字大小
          if (topic.length() >= topic.indexOf("]")) {
            AbsoluteSizeSpan _sizeSpan1 = new AbsoluteSizeSpan(textSizeTopic, true);
            editable.setSpan(_sizeSpan1, findPos + topic.indexOf("]") + 1, findPos + topic.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            mSizeSpans.add(_sizeSpan1);
          }
        }
        findPos = findPos + topic.length();
      }
    }
  }

  /**********************改变光标位置，不可选中话题中间******************************************/
  private void changeSelectionPos() {
    if (null != mTopicList && mTopicList.size() > 0) {
      int selectionStart = getSelectionStart();
      int lastPos = 0;
      int size = mTopicList.size();
      for (int i = 0; i < size; i++) {
        String topic = mTopicList.get(i);
        lastPos = getText().toString().indexOf(topic, lastPos);
        if (lastPos != -1) {
          if (selectionStart > lastPos && selectionStart <= (lastPos + topic.length())) {
            //在这position 区间就移动光标
            setSelection(lastPos + topic.length());
          }
        }
        lastPos = lastPos + topic.length();
      }
    }
  }

  /**
   * 删除操作
   *
   * @return true or false
   */
  private boolean operateDelText() {
    int selectionStart = getSelectionStart();
    int selectionEnd = getSelectionEnd();
    if (selectionStart != selectionEnd) {
      isDel = false;
      return false;
    }

    Editable editable = getText();
    String content = editable.toString();
    int lastPos = 0;
    int size = mTopicList.size();
    for (int i = 0; i < size; i++) {
      String topic = mTopicList.get(i);
      lastPos = content.indexOf(topic, lastPos);
      if (lastPos != -1) {
        if (selectionStart != 0 && selectionStart > lastPos && selectionStart <= (lastPos + topic.length())) {
          isDel = true;
          //选中话题
          setSelection(lastPos, lastPos + topic.length());
          //// 改变选中话题标签文字颜色
          //BackgroundColorSpan colorSpan = new BackgroundColorSpan(textColorSelectTopic);
          //editable.setSpan(colorSpan, lastPos, lastPos + topic.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

          return true;
        }
      }
      lastPos += topic.length();
    }
    return false;
  }

  /**
   * 当输入法和EditText建立连接的时候会通过这个方法返回一个InputConnection。
   * 我们需要代理这个方法的父类方法生成的InputConnection并返回我们自己的代理类。
   */
  @Override public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
    inputConnection.setTarget(super.onCreateInputConnection(outAttrs));
    return inputConnection;
  }

  /**
   * 匹配文本方法
   *
   * @param s 带匹配内容
   *
   * @return 匹配到的话题标签列表
   */
  public ArrayList<String> findTopic(String s) {
    if (TextUtils.isEmpty(topicRegex)) {
      Log.w(TAG, "you have not set the topicRegex.so we use the default regex. ");
      topicRegex = DEFAULT_REGEX;
      return new ArrayList<>();
    }
    Pattern p = Pattern.compile(topicRegex);
    Matcher m = p.matcher(s);
    ArrayList<String> list = new ArrayList<>();
    while (m.find()) {
      list.add(m.group());
    }
    return list;
  }

  /**
   * 插入话题内容
   *
   * @param topic 插入话题
   */
  public void insertTopic(String topic) {
    int selectStart = getSelectionStart();
    String con = getText().toString();
    String firstStr = con.substring(0, selectStart);
    String secondStr = con.substring(selectStart, con.length());
    setText(firstStr + topic + secondStr);
    setSelection(selectStart + topic.length());
  }

  /**
   * 设置软键盘删除键事件
   *
   * @param backSpaceListener 删除监听
   */
  public void setBackSpaceListener(TInputConnection.BackspaceListener backSpaceListener) {
    inputConnection.setBackspaceListener(backSpaceListener);
  }
}

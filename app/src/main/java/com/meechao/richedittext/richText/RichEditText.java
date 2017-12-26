package com.meechao.richedittext.richText;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import com.alibaba.fastjson.JSON;
import com.meechao.richedittext.R;
import java.util.ArrayList;
import java.util.List;

/**
 * 仿微博话题输入控件
 *
 * @author Ruffian
 */
public class RichEditText extends android.support.v7.widget.AppCompatEditText {

  // 默认,话题文本高亮颜色
  private static final int FOREGROUND_COLOR = Color.parseColor("#FF8C00");
  // 默认,话题背景高亮颜色
  private static final int BACKGROUND_COLOR = Color.parseColor("#FFDEAD");
  //
  private float textSize;

  /**
   * 开发者可设置内容
   */
  private int mForegroundColor = FOREGROUND_COLOR;// 话题文本高亮颜色
  private int mBackgroundColor = BACKGROUND_COLOR;// 话题背景高亮颜色
  private List<RichObject> mRObjectsList = new ArrayList<RichObject>();// object集合
  private List<String> changedList = new ArrayList<>();

  public RichEditText(Context context) {
    this(context, null);
  }

  public RichEditText(Context context, AttributeSet attrs) {
    super(context, attrs);

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.REditText);
    mBackgroundColor = a.getColor(R.styleable.REditText_object_background_color, BACKGROUND_COLOR);
    mForegroundColor = a.getColor(R.styleable.REditText_object_foreground_color, FOREGROUND_COLOR);
    a.recycle();
    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
    // 初始化设置
    initView();
  }

  /**
   * 监听光标的位置,若光标处于话题内容中间则移动光标到话题结束位置
   */
  @Override protected void onSelectionChanged(int selStart, int selEnd) {
    super.onSelectionChanged(selStart, selEnd);
    if (mRObjectsList == null || mRObjectsList.size() == 0) {
      return;
    }

    try {
      for (int i = 0; i < mRObjectsList.size(); i++) {
        RichObject richObject = mRObjectsList.get(i);// 文本
        int startPosition = richObject.getStartIndex();// 获取文本开始下标
        int endPosition = richObject.getEndIndex();
        if (startPosition != -1 && selStart > startPosition && selStart <= endPosition) {// 若光标处于话题内容中间则移动光标到话题结束位置
          setSelection(endPosition);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 初始化控件,一些监听
   */
  private void initView() {
    textSize = getTextSize();
    /**
     * 输入框内容变化监听<br/>
     * 1.当文字内容产生变化的时候实时更新UI
     */
    this.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override public void afterTextChanged(final Editable s) {
        // 文字改变刷新UI
        refreshEditTextUI(s.toString());
      }
    });

    /**
     * 监听删除键 <br/>
     * 1.光标在话题后面,将整个话题内容删除 <br/>
     * 2.光标在普通文字后面,删除一个字符
     */
    this.setOnKeyListener(new OnKeyListener() {
      @Override public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {

          int selectionStart = getSelectionStart();
          int selectionEnd = getSelectionEnd();

          /**
           * 如果光标起始和结束不在同一位置,删除文本
           */

          if (selectionStart != selectionEnd) {
            // 查询文本是否属于目标对象,若是移除列表数据
            Log.i("mRObjectsList", "mRObjectsList obj: " + JSON.toJSONString(mRObjectsList));
            String targetText = getText().toString().substring(selectionStart, selectionEnd);
            for (int i = 0; i < mRObjectsList.size(); i++) {
              RichObject object = mRObjectsList.get(i);
              if (selectionStart == object.getStartIndex() && selectionEnd == object.getEndIndex()) {
                Log.i("mRObjectsList", "mRObjectsList obj i: " + i);
                mRObjectsList.remove(object);
              }
            }
            return false;
          }

          //int lastPos = 0;
          Editable editable = getText();
          // 遍历判断光标的位置
          for (int i = 0; i < mRObjectsList.size(); i++) {
            RichObject object = mRObjectsList.get(i);
            String objectText = object.getTopicContent();

            Log.i("setOnKeyListener", String.format(
                "i： %s  \nlastPos: %s  \nselectionStart: %s  \nselectionStart >= object.getStartIndex():  %s  "
                    + "\nobjectText.length ()： "
                    + "%s "
                    + "\nelectionStart <= (object.getStartIndex() "
                    + "+ objectText.length()： %s  ", i, object.getStartIndex(), selectionStart,
                selectionStart >= object.getStartIndex(), objectText.length(),
                selectionStart <= (object.getStartIndex() + objectText.length())));

            try {
              if (selectionStart != 0
                  && selectionStart >= object.getStartIndex()
                  && selectionStart <= (object.getStartIndex() + objectText.length())) {
                // 选中话题
                setSelection(object.getStartIndex(), object.getStartIndex() + objectText.length());
                // 设置背景色
                editable.setSpan(new BackgroundColorSpan(mBackgroundColor), object.getStartIndex(),
                    object.getStartIndex() + objectText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return true;
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }

        return false;
      }
    });
  }

  /**
   * EditText内容修改之后刷新UI
   *
   * @param content 输入框内容
   */
  private void refreshEditTextUI(String content) {

    /**
     * 内容变化时操作<br/>
     * 1.查找匹配所有话题内容 <br/>
     * 2.设置话题内容特殊颜色
     */

    if (mRObjectsList.size() == 0) {
      return;
    }

    if (TextUtils.isEmpty(content)) {
      mRObjectsList.clear();
      return;
    }
    int lastObjIndex = 0;
    /**
     * 重新设置span
     */
    Editable editable = getText();

    int startPosition = 0;
    for (int i = 0; i < mRObjectsList.size(); i++) {
      final RichObject object = mRObjectsList.get(i);
      String objectText = object.getTopicContent();// 文本
      startPosition = content.indexOf(objectText, lastObjIndex);// 获取文本开始下标
      if (startPosition != -1) {// 设置话题内容前景色高亮
        lastObjIndex = startPosition + objectText.length();
        object.setStartIndex(startPosition);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(mForegroundColor);
        editable.setSpan(colorSpan, startPosition, lastObjIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      }
    }
    editable.setSpan(new AbsoluteSizeSpan(16, true), lastObjIndex, content.length(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    Log.i("mRObjectsList", "mRObjectsList obj: " + JSON.toJSONString(mRObjectsList));
  }

  /**
   * 插入/设置话题
   *
   * @param object 话题对象
   */
  public void setObject(RichObject object) {

    if (object == null) {
      return;
    }

    int selectionStart = getSelectionStart();// 光标位置

    Editable editable = getText();// 原先内容
    if (selectionStart >= 0) {

      for (int i = 0; i < mRObjectsList.size(); i++) {
        int objPreEnd = -1;

        if (i == 0) {
          objPreEnd = 0;
        } else {
          objPreEnd = mRObjectsList.get(i - 1).getEndIndex();
        }

        int objNextStart = mRObjectsList.get(i).getStartIndex();
        if (selectionStart == objPreEnd) {
          editable.insert(selectionStart, " ");
          setSelection(selectionStart + 1);
        }

        if (selectionStart == objNextStart) {
          editable.insert(selectionStart, " ");
          setSelection(selectionStart - 1);
        }
      }
      //1.设置object起始位置
      object.setStartIndex(getSelectionStart());
      //2.添加话题内容到数据集合
      mRObjectsList.add(object);
      //3.将话题内容添加到EditText中展示
      editable.insert(getSelectionStart(), object.getShowContent());// 在光标位置插入内容
      editable.insert(getSelectionStart(), " ");// 话题后面插入空格,至关重要
      setSelection(getSelectionStart());// 移动光标到添加的内容后面
    }
  }

  /**
   * 获取object列表数据
   */
  public List<RichObject> getObjects() {
    return mRObjectsList;
  }
}

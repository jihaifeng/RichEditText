package com.meechao.richedittext.richText;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import com.meechao.richedittext.Redit.RObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RichObject extends RObject {

  // s = #天气[1|话题]#
  public static final String REG_ID = "(?<=\\[)(\\d+)(?=|)"; // 获取标签id ，如 1
  public static final String REG_TEXT = "(?<=\\|)(.+)(?=\\|)"; // 获取 标签类别 ，如 话题
  public static final String REG_TYPE = "(?<=#)(.+)(?=\\[)"; // 获取 标签内容，不包括前后# ,如 天气

  private String topicContent;// 话题文本  #天气[1|话题]#
  private int topicId;
  private String topicType;
  private String topicText;
  private SpannableString showContent;
  private int startIndex;
  private int endIndex;

  public String getTopicContent() {
    return topicContent;
  }

  public void setTopicContent(String topicContent) {
    this.topicContent = topicContent;
    setShowContent();
    setTopicId(null == getMatcherStr(REG_ID) ? -1 : Integer.parseInt(getMatcherStr(REG_ID)));
    setTopicText(getMatcherStr(REG_TEXT));
    setTopicType(getMatcherStr(REG_TYPE));
  }

  public int getTopicId() {
    return topicId;
  }

  private void setTopicId(int topicId) {
    this.topicId = topicId;
  }

  public String getTopicType() {
    return topicType;
  }

  private void setTopicType(String topicType) {
    this.topicType = topicType;
  }

  public String getTopicText() {
    return topicText;
  }

  private void setTopicText(String topicText) {
    this.topicText = topicText;
  }

  public int getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
    setEndIndex(startIndex + topicContent.length());
  }

  public int getEndIndex() {
    return endIndex;
  }

  private void setEndIndex(int endIndex) {
    this.endIndex = endIndex;
  }

  public SpannableString getShowContent() {
    return showContent;
  }

  private void setShowContent() {
    SpannableString spannableString = new SpannableString(topicContent);
    spannableString.setSpan(new AbsoluteSizeSpan(0, true), topicContent.indexOf("["), topicContent.indexOf("]") + 1,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    this.showContent = spannableString;
  }

  /**
   * 正则提取内容
   *
   * @param reg 正则表达式
   */
  private String getMatcherStr(String reg) {
    if (TextUtils.isEmpty(topicContent)) {
      return null;
    }
    String result = null;
    Pattern pattern = Pattern.compile(reg);
    Matcher matcher = pattern.matcher(topicContent);
    if (matcher.find()) {
      result = matcher.group();
    }
    Log.i("aaa", "getMatcherStr: " + result);
    return result;
  }
}

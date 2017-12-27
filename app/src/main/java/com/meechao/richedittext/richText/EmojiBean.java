package com.meechao.richedittext.richText;

import java.util.ArrayList;
import java.util.List;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2017-12-26 16:55
 * Mail：jihaifeng@meechao.com
 */
public class EmojiBean {

  private List<String> emoji_list;

  public List<String> getEmoji_list() {
    List<String> strList = new ArrayList<>();
    for (String s : emoji_list) {
      int id = Integer.parseInt(s,16);
      strList.add(new String(Character.toChars(id)));
    }

    return strList;
  }

  public void setEmoji_list(List<String> emoji_list) {
    this.emoji_list = emoji_list;
  }
}

package com.meechao.richedittext;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Date：2017-11-08-0008 16:00
 * Mail：jihaifeng@meechao.com
 */
public final class KeyboardUtils {

  private KeyboardUtils() {
    throw new UnsupportedOperationException("u can't instantiate me...");
  }

    /*
      避免输入法面板遮挡
      <p>在manifest.xml中activity中设置</p>
      <p>android:windowSoftInputMode="adjustPan"</p>
     */

  /**
   * 动态显示软键盘
   *
   * @param activity activity
   */
  public static void showSoftInput(final Activity activity) {
    View view = activity.getCurrentFocus();
    if (view == null) {
      view = new View(activity);
    }
    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    if (imm == null) {
      return;
    }
    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
  }

  /**
   * 动态显示软键盘
   *
   * @param view 视图
   */
  public static void showSoftInputfinal(View view) {
    view.setFocusable(true);
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm == null) {
      return;
    }
    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
  }

  /**
   * 动态隐藏软键盘
   *
   * @param activity activity
   */
  public static void hideSoftInput(final Activity activity) {
    View view = activity.getCurrentFocus();
    if (view == null) {
      view = new View(activity);
    }
    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    if (imm == null) {
      return;
    }
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  /**
   * 动态隐藏软键盘
   *
   * @param view 视图
   */
  public static void hideSoftInput(final View view) {
    InputMethodManager imm =
        (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm == null) {
      return;
    }
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  /**
   * 切换键盘显示与否状态
   */
}
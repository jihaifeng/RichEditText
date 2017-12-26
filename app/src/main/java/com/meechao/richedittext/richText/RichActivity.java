package com.meechao.richedittext.richText;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.meechao.richedittext.KeyboardUtils;
import com.meechao.richedittext.MyAdapter;
import com.meechao.richedittext.R;
import com.meechao.richedittext.rv.HorizontalPageLayoutManager;
import com.meechao.richedittext.rv.PagingScrollHelper;
import com.meechao.richedittext.utils.DisplayUtils;
import com.meechao.richedittext.utils.InputMethodUtils;

import java.util.List;

public class RichActivity extends Activity {

    private RichEditText mREditText;
    private TextView mResult;
    private Button btnTopic, btnSet;
    private RadioGroup radioGroup;
    private RecyclerView rcv;
    private PagingScrollHelper scrollHelper = new PagingScrollHelper();
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich);
        DisplayUtils.init(this);
        initViews();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initViews() {
        mREditText = (RichEditText) findViewById(R.id.rich_edit);
        mResult = (TextView) findViewById(R.id.result);
        btnTopic = (Button) findViewById(R.id.btn_topic);
        btnSet = (Button) findViewById(R.id.btn_set);
        radioGroup = (RadioGroup) findViewById(R.id.rg_keyboard);
        Log.i("aaa", "initViews: " + radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_keybord_left:
//                        KeyboardUtils.hideSoftInput(RichActivity.this);
//                        rcv.setVisibility(View.VISIBLE);
//                        if (isEmotionPanelShowing()) {
                        showEmotionPanel();

//                        } else {
//                            showEmotionPanel();
//                        }
                        break;
                    case R.id.rb_keybord_right:
                        InputMethodUtils.toggleSoftInput(getCurrentFocus());
                        mEmotionPanel.postDelayed(mHideEmotionPanelTask, 500);
                        break;
                }
            }
        });
        mREditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    InputMethodUtils.setKeyboardShowing(true);
                    if (isEmotionPanelShowing()) {
                        mEmotionPanel.postDelayed(mHideEmotionPanelTask, 500);
                    }
                }
                return false;
            }
        });

//        InputMethodUtils.detectKeyboard(this);
//        InputMethodUtils.enableCloseKeyboardOnTouchOutside(this);

        btnSet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getTopicsData();
            }
        });

        btnTopic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
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

        rcv = findViewById(R.id.rcv);
        mEmotionPanel = rcv;
        HorizontalPageLayoutManager layoutManager = new HorizontalPageLayoutManager(3, 4);

        rcv.setLayoutManager(layoutManager);
        MyAdapter myAdapter = new MyAdapter();
        rcv.setAdapter(myAdapter);
        scrollHelper.setUpRecycleView(rcv);
        scrollHelper.setOnPageChangeListener(new PagingScrollHelper.onPageChangeListener() {
            @Override
            public void onPageChange(int index) {
                mResult.setText("第" + (index + 1) + "页");
            }
        });

    }

    public void translateRcv(View view, int fromY, int toY) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, fromY, toY);
        translateAnimation.setDuration(2000);
        view.setAnimation(translateAnimation);
        view.startAnimation(translateAnimation);
    }

    private String getEmojiStringByUnicode(int unicode) {
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


    private View mEmotionPanel;

    public void showEmotionPanel() {
        mEmotionPanel.removeCallbacks(mHideEmotionPanelTask);
        InputMethodUtils.updateSoftInputMethod(this, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        mEmotionPanel.setVisibility(View.VISIBLE);
        InputMethodUtils.hideKeyboard(getCurrentFocus());
    }

    private Runnable mHideEmotionPanelTask = new Runnable() {
        @Override
        public void run() {
            hideEmotionPanel();
        }
    };

    public void hideEmotionPanel() {
        if (mEmotionPanel.getVisibility() != View.GONE) {
            mEmotionPanel.setVisibility(View.GONE);
            InputMethodUtils.updateSoftInputMethod(this, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    public boolean isEmotionPanelShowing() {
        return mEmotionPanel.getVisibility() == View.VISIBLE;
    }

    public void updateEmotionPanelHeight(int keyboardHeight) {
        Log.i("aaa", "updateEmotionPanelHeight: " + keyboardHeight);
        ViewGroup.LayoutParams params = mEmotionPanel.getLayoutParams();
        if (params != null && params.height != keyboardHeight) {
            params.height = keyboardHeight;
            mEmotionPanel.setLayoutParams(params);
        }
    }

}

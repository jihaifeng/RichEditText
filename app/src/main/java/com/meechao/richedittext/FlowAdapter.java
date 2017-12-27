package com.meechao.richedittext;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

/**
 * Created by xiangcheng on 17/12/3.
 */

public class FlowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private List<String> list;
  private Context context;

  public FlowAdapter(Context context, List<String> list) {
    this.list = list;
    this.context = context;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new MyHolder(View.inflate(context, R.layout.flow_item, null));
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
    TextView textView = ((MyHolder) holder).text;
    textView.setText(list.get(position));
  }

  @Override public int getItemCount() {
    return null == list ? 0 : list.size();
  }

  class MyHolder extends RecyclerView.ViewHolder {

    private TextView text;

    public MyHolder(View itemView) {
      super(itemView);
      text = (TextView) itemView.findViewById(R.id.flow_text);
    }
  }
}

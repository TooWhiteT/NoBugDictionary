package com.tzg.nobugdictionary.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.tts.client.SpeechSynthesizer;
import com.tzg.nobugdictionary.Bean.ZiBean;
import com.tzg.nobugdictionary.R;

import java.util.List;

public class ZiAdapter extends RecyclerView.Adapter {
    public SpeechSynthesizer mSpeechSynthesizer;
    private List<ZiBean.ResultBean> Madapterlist;
    private OnItem MonItem;

    public void setOnItem(OnItem onItem) {
        MonItem = onItem;
    }

    public interface OnItem{
        void onItemClick(int position);
    }
    public void setMadapterlist(List<ZiBean.ResultBean> list){
        Madapterlist = list;
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_layout, viewGroup, false);
        return new Holder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder,final int i) {
        ((Holder)viewHolder).item_text.setText(Madapterlist.get(i).getZi());
        ((Holder)viewHolder).item_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //播放音频
                Log.i("PLAYADAPTER","play点击");
                mSpeechSynthesizer = SpeechSynthesizer.getInstance();
                mSpeechSynthesizer.speak(Madapterlist.get(i).getZi());

            }
        });
        ((Holder)viewHolder).layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MonItem != null){
                    MonItem.onItemClick(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (Madapterlist != null){
            return Madapterlist.size();
        }
        return 0;
    }
    public class Holder extends RecyclerView.ViewHolder{
        private RelativeLayout layout;
        private TextView item_text;
        private ImageView item_image;
        public Holder(View itemView) {
            super(itemView);
            item_text = (TextView)itemView.findViewById(R.id.item_text);
            item_image = (ImageView)itemView.findViewById(R.id.item_image);
            layout = (RelativeLayout)itemView.findViewById(R.id.item_layout);
        }
    }

}

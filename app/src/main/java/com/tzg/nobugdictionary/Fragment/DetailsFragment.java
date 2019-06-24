package com.tzg.nobugdictionary.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tzg.nobugdictionary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {
    private TextView zi,py,bs,wb,bh,jianjie,xiangjie;
    private Bundle StrSet;
    public DetailsFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrSet = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        initView(view);
        zi.setText(StrSet.getString("zi"));
        py.setText(StrSet.getString("py"));
        bs.setText(StrSet.getString("bs"));
        wb.setText(StrSet.getString("wb"));
        bh.setText(StrSet.getString("bh"));
        jianjie.setText(StrSet.getString("jj"));
        xiangjie.setText(StrSet.getString("xj"));
        return view;
    }

    public void initView(View view){
        zi = (TextView)view.findViewById(R.id.xj_zi);
        py = (TextView)view.findViewById(R.id.xj_pinyin);
        bs = (TextView)view.findViewById(R.id.xj_bushou);
        wb = (TextView)view.findViewById(R.id.xj_wubi);
        bh = (TextView)view.findViewById(R.id.xj_bihua);
        jianjie = (TextView)view.findViewById(R.id.xj_jianjie);
        xiangjie = (TextView)view.findViewById(R.id.xj_xiangjie);

        jianjie.setMovementMethod(ScrollingMovementMethod.getInstance());
        xiangjie.setMovementMethod(ScrollingMovementMethod.getInstance());
    }
}

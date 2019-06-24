package com.tzg.nobugdictionary.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tzg.nobugdictionary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CyDetailsFragment extends Fragment {
    private TextView text,pinyin,from,yufa,cyjs,tongyi,fanyi,exp;
    private Bundle StrSet;

    public CyDetailsFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrSet = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cy_details, container, false);
        initView(view);
        text.setText(StrSet.getString("text"));
        pinyin.setText(StrSet.getString("py"));
        from.setText(StrSet.getString("from"));
        yufa.setText(StrSet.getString("yf"));
        cyjs.setText(StrSet.getString("js"));
        tongyi.setText(StrSet.getString("ty"));
        fanyi.setText(StrSet.getString("fy"));
        exp.setText(StrSet.getString("exp"));
        return view;
    }

    public void initView(View view){
        text = (TextView)view.findViewById(R.id.cy_text);
        pinyin = (TextView)view.findViewById(R.id.cy_py);
        from = (TextView)view.findViewById(R.id.cy_from);
        //from.setMovementMethod(ScrollingMovementMethod.getInstance());

        yufa = (TextView)view.findViewById(R.id.cy_yufa);
        cyjs = (TextView)view.findViewById(R.id.cy_js);
        //cyjs.setMovementMethod(ScrollingMovementMethod.getInstance());

        tongyi = (TextView)view.findViewById(R.id.cy_tongyi);
        fanyi = (TextView)view.findViewById(R.id.cy_fanyi);
        exp = (TextView)view.findViewById(R.id.cy_exp);
        //exp.setMovementMethod(ScrollingMovementMethod.getInstance());
    }
}

package com.tzg.nobugdictionary.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tzg.nobugdictionary.Adapter.CyAdapter;
import com.tzg.nobugdictionary.Bean.CyBean;
import com.tzg.nobugdictionary.Events.CyEvent;
import com.tzg.nobugdictionary.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CyFragment extends Fragment {

    private List<CyBean.ResultBean> cyBeanList = new ArrayList<>();
    private RecyclerView cyRecyclerView;
    private CyAdapter cyAdapter;
    private String cyString;
    private FragmentManager manager;
    private CyDetailsFragment cyDetailsFragment;
    public CyFragment() {
        super();
        this.cyDetailsFragment = new CyDetailsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_cy, container, false);
        EventBus.getDefault().register(this);
        cyRecyclerView = (RecyclerView)inflate.findViewById(R.id.cy_recyclerview);
        cyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        cyAdapter = new CyAdapter();
        cyAdapter.setOnItem(new CyAdapter.OnItem() {
            @Override
            public void onItemClick(int position) {
                //进详情页
                replaceFragment(cyDetailsFragment);
                setBundleValue(cyBeanList,position,cyDetailsFragment);
            }
        });
        return inflate;
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onCyEvent(CyEvent event){
        cyBeanList = event.getList();
        cyAdapter.setMadapterlist(cyBeanList);
        cyRecyclerView.setAdapter(cyAdapter);
        cyString = event.getCystr();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
    public void setBundleValue(List<CyBean.ResultBean> list,int position,Fragment fragment){
        Bundle bundle = new Bundle();
        bundle.putString("text",cyString);
        bundle.putString("py",list.get(position).getPinyin());
        bundle.putString("from",list.get(position).getFrom_()+"\n"+list.get(position).getYinzhengjs());
        bundle.putString("yf",list.get(position).getYufa());
        bundle.putString("js",list.get(position).getCiyujs()+"\n"+list.get(position).getChengyujs());
        bundle.putString("fy",changString(list.get(position).getFanyi()));
        bundle.putString("ty",changString(list.get(position).getTongyi()));
        bundle.putString("exp",list.get(position).getExample());

        fragment.setArguments(bundle);
    }
    //拼接字符串
    public String changString(List<String> list){
        StringBuilder csvBuilder = new StringBuilder();
        for(String zi : list){
            csvBuilder.append(zi);
            csvBuilder.append(",");
        }
        return csvBuilder.toString();
    }
    public void replaceFragment(Fragment fragment){
        manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.layout_fragment_cy,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

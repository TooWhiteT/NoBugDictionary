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

import com.tzg.nobugdictionary.Adapter.ZiAdapter;
import com.tzg.nobugdictionary.Bean.ZiBean;
import com.tzg.nobugdictionary.Events.ZiEvent;
import com.tzg.nobugdictionary.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZiFragment extends Fragment {

    private List<ZiBean.ResultBean> ziBeanList = new ArrayList<>();
    private RecyclerView ziRecyclerView;
    private ZiAdapter ziAdapter;
    private FragmentManager manager;
    private DetailsFragment detailsFragment;

    public ZiFragment() {
        super();
        this.detailsFragment = new DetailsFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View inflate = inflater.inflate(R.layout.fragment_zi, container, false);

        EventBus.getDefault().register(this);
        ziRecyclerView = (RecyclerView)inflate.findViewById(R.id.zi_recyclerview);
        ziRecyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        ziAdapter = new ZiAdapter();
        ziAdapter.setOnItem(new ZiAdapter.OnItem() {
            @Override
            public void onItemClick(int position) {
                replaceFragment(detailsFragment);
                setBundleValue(ziBeanList,position,detailsFragment);
            }
        });
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onZiEvent(ZiEvent event){
        ziBeanList = event.getList();
        ziAdapter.setMadapterlist(ziBeanList);
        ziRecyclerView.setAdapter(ziAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void setBundleValue(List<ZiBean.ResultBean> list,int position,Fragment fragment){
        Bundle bundle = new Bundle();
        bundle.putString("zi",list.get(position).getZi());
        bundle.putString("py",list.get(position).getPinyin());
        bundle.putString("bs",list.get(position).getBushou());
        bundle.putString("bh",list.get(position).getBihua());
        bundle.putString("wb",list.get(position).getWubi());
        bundle.putString("jj",changString(list.get(position).getJijie()));
        bundle.putString("xj",changString(list.get(position).getXiangjie()));
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
        transaction.replace(R.id.layout_fragment_zi,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

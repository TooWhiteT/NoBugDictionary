package com.tzg.nobugdictionary.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tzg.nobugdictionary.Adapter.PyAdapter;
import com.tzg.nobugdictionary.Adapter.ZiAdapter;
import com.tzg.nobugdictionary.Bean.PyBean;
import com.tzg.nobugdictionary.Events.PyEvent;
import com.tzg.nobugdictionary.MainActivity;
import com.tzg.nobugdictionary.NetWork;
import com.tzg.nobugdictionary.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

/**
 * A simple {@link Fragment} subclass.
 */
public class PyFragment extends Fragment {

    private List<PyBean.ResultBean.ListBean> pylistbean = new ArrayList<>();
    private RecyclerView pyRecyclerView;
    private PyAdapter pyAdapter;
    private int page = 1;
    public Retrofit retrofit;
    public NetWork netWork;
    private FragmentManager manager;
    private DetailsFragment detailsFragment;

    public PyFragment() {
        super();
        this.detailsFragment = new DetailsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_py, container, false);
        EventBus.getDefault().register(this);
        initRetorfit("http://v.juhe.cn/xhzd/");

        pyRecyclerView = (RecyclerView)inflate.findViewById(R.id.py_recyclerview);
        pyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        pyAdapter = new PyAdapter();
        pyAdapter.setOnItem(new ZiAdapter.OnItem() {
            @Override
            public void onItemClick(int position) {
                replaceFragment(detailsFragment);
                setBundleValue(pylistbean,position,detailsFragment);
            }
        });
        pyRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState){
                    case SCROLL_STATE_IDLE:
                        break;
                    case SCROLL_STATE_DRAGGING:
                        page = page + 1;
                        Bundle bundle = getArguments();
                        addListBean(bundle.getString("serStr"),page);
                        break;
                    case SCROLL_STATE_SETTLING:
                        pyAdapter.setMadapterlist(pylistbean);
                        break;
                }
            }
        });
        return inflate;
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onPyEvent(PyEvent event){
        pylistbean = event.getList();
        pyAdapter.setMadapterlist(pylistbean);
        pyRecyclerView.setAdapter(pyAdapter);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
    public void replaceFragment(Fragment fragment){
        manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.layout_fragment_py,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void initRetorfit(String url) {
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(initOkhttp())
                .build();
        netWork = retrofit.create(NetWork.class);
    }

    public OkHttpClient initOkhttp() {
        return new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .connectTimeout(5,TimeUnit.SECONDS)
                .build();
    }

    public void addListBean(String str,int page){
        netWork.getPy("bf1f9352094074c284aa3a2caad67b73",str,page,1,1)
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .map(new Func1<PyBean, List<PyBean.ResultBean.ListBean>>() {
                    @Override
                    public List<PyBean.ResultBean.ListBean> call(PyBean pyBean) {
                        return pyBean.getResult().getList();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<PyBean.ResultBean.ListBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<PyBean.ResultBean.ListBean> listBeans) {
                        pylistbean.addAll(listBeans);
                    }
                });
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
    public void setBundleValue(List<PyBean.ResultBean.ListBean> list,int position,Fragment fragment){
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
}

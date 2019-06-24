package com.tzg.nobugdictionary.Events;

import com.tzg.nobugdictionary.Bean.BsBean;

import java.util.List;

public class BsEvent {
    private List<BsBean.ResultBean.ListBean> list;
    public BsEvent(List<BsBean.ResultBean.ListBean> mList){
        this.list = mList;
    }

    public List<BsBean.ResultBean.ListBean> getList() {
        return list;
    }
}

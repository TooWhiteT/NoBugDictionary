package com.tzg.nobugdictionary.Events;

import com.tzg.nobugdictionary.Bean.PyBean;

import java.util.List;

public class PyEvent {
    private List<PyBean.ResultBean.ListBean> list;
    public PyEvent(List<PyBean.ResultBean.ListBean> mList){
        this.list = mList;
    }

    public List<PyBean.ResultBean.ListBean> getList() {
        return list;
    }
}

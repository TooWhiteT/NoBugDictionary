package com.tzg.nobugdictionary.Events;

import com.tzg.nobugdictionary.Bean.ZiBean;

import java.util.List;

public class ZiEvent {
    private List<ZiBean.ResultBean> list;
    public ZiEvent(List<ZiBean.ResultBean> mList){
        this.list = mList;
    }

    public List<ZiBean.ResultBean> getList() {
        return list;
    }
}

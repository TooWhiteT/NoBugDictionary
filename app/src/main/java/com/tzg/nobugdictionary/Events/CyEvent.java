package com.tzg.nobugdictionary.Events;

import com.tzg.nobugdictionary.Bean.CyBean;

import java.util.List;

public class CyEvent {
    private List<CyBean.ResultBean> list;
    private String cystr;
    public CyEvent(List<CyBean.ResultBean> mList,String mCystr){
        this.list = mList;
        this.cystr = mCystr;
    }
    public List<CyBean.ResultBean> getList() {
        return list;
    }

    public String getCystr() {
        return cystr;
    }
}

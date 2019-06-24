package com.tzg.nobugdictionary;


import com.tzg.nobugdictionary.Bean.BsBean;
import com.tzg.nobugdictionary.Bean.CyBean;
import com.tzg.nobugdictionary.Bean.PyBean;
import com.tzg.nobugdictionary.Bean.ZiBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface NetWork {
    @GET("query")
    public Observable<CyBean> getCy(@Query("key") String key, @Query("word") String word);//获取成语字典

    @GET("query")
    public Observable<ZiBean> getZi(@Query("key") String key, @Query("word") String zi);//根据汉字查找
    @GET("querybs")
    public Observable<BsBean> getBs(@Query("key") String key, @Query("word") String zi, @Query("page") int page, @Query("isjijie") int jijie, @Query("isxiangjie") int xiangjie);//根据部首查找
    @GET("querypy")
    public Observable<PyBean> getPy(@Query("key") String key, @Query("word") String zi, @Query("page") int page, @Query("isjijie") int jijie, @Query("isxiangjie") int xiangjie);//根据拼音查找
}

package com.ai.basecommon.core.param;

public class PageIn {

    private int page = 1;
    private int limit = 10;

    //分页查询起始位置，根据page limit 自动计算，无需入参
    private int begin = 0;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
        if(0!=page && 0!= limit){
            this.begin =(page-1)*limit;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(0 == limit){
            limit = 10;
        }
        this.limit = limit;
    }

    public int getBegin() {
        return begin;
    }

}

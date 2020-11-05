package com.zhihuta.xiaota.bean.response;
import com.alibaba.fastjson.annotation.JSONField;

public class PageOffsetResponse extends BaseResponse {

    public int offset;

    @JSONField(name="request_count")
    public int requestCount;

    @JSONField(name="total_count")
    public long totalCount;

    /*
     * 第一种：在对象响应字段前加注解，这样生成的json也不包含该字段。
     * @JSONField(serialize=false)
     *  private String name;
     */
    /*
     * 第二种：在对象对应字段前面加transient，表示该字段不用序列化，即在生成json的时候就不会包含该字段了。
     * private transient  String name;
     */

}

package com.github.wxiaoqi.security.jinmao.vo.decoreteapply;

import lombok.Data;

import java.io.Serializable;

@Data
public class DecoreteApplyListVo implements Serializable {

    private static final long serialVersionUID = 860585034535921248L;
    private String id;

    private String subId;

    private String name;

    private String phone;

    private String addr;

    private String stage;

    private String area;

    private String status;
    //支付状态(0-待支付,1-待接单,2-监理中,3-已完成,4-已取消,5-已评价)
    private String statusStr;

    private String price;

    private String createTime;

    public String getStatusStr() {
        String statusStr = "";
        if ("0".equals(status)) {
            statusStr = "待支付";
        }else if ("1".equals(status)) {
            statusStr = "待接单";
        }else if ("2".equals(status)) {
            statusStr = "监理中";
        }else if ("3".equals(status)) {
            statusStr = "已完成";
        }else if ("4".equals(status)) {
            statusStr = "已取消";
        }else if ("5".equals(status)) {
            statusStr = "已评价";
        }
        return statusStr;
    }
}

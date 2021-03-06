package com.github.wxiaoqi.security.app.mapper;

import com.github.wxiaoqi.security.app.entity.BizOrderRemark;
import com.github.wxiaoqi.security.common.mapper.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author guohao
 * @Date 2020-04-22 18:48:14
 */
public interface BizOrderRemarkMapper extends CommonMapper<BizOrderRemark> {

    List<BizOrderRemark> selectByOrderId(@Param("orderId") String orderId);


    int updateBySplitOrder(@Param("sourceOrderId") String sourceOrderId,@Param("tenantId") String tenantId,
                           @Param("targetOrderId") String targetOrderId);
}

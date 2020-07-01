package com.github.wxiaoqi.security.admin.mapper;

import com.github.wxiaoqi.security.admin.entity.Menu;
import com.github.wxiaoqi.security.common.mapper.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MenuMapper extends CommonMapper<Menu> {
    /**
     * 根据资源类型查询菜单
     *
     * @param authorityId
     * @param authorityType
     * @param type
     * @return
     */
    public List<Menu> selectMenuByAuthorityId(@Param("authorityId") String authorityId, @Param("authorityType") String authorityType, @Param("type") String type);

    /**
     * 根据用户和组的权限关系查找用户可访问菜单
     *
     * @param userId
     * @return
     */
    public List<Menu> selectAuthorityMenuByUserId(@Param("userId") String userId, @Param("type") String type);

    /**
     * 根据用户和组的权限关系查找用户可访问的系统
     *
     * @param userId
     * @return
     */
    public List<Menu> selectAuthoritySystemByUserId(@Param("userId") String userId, @Param("type") String type);

    /**
     * 查询所有的菜单
     *
     * @return
     */
    public List<Menu> selectListAllMenu();
}

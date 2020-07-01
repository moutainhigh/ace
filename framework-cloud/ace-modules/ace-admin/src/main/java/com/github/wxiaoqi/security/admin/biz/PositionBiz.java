package com.github.wxiaoqi.security.admin.biz;

import com.ace.cache.annotation.CacheClear;
import com.github.ag.core.context.BaseContextHandler;
import com.github.wxiaoqi.security.admin.entity.Depart;
import com.github.wxiaoqi.security.admin.entity.Group;
import com.github.wxiaoqi.security.admin.entity.Position;
import com.github.wxiaoqi.security.admin.entity.User;
import com.github.wxiaoqi.security.admin.mapper.PositionMapper;
import com.github.wxiaoqi.security.admin.vo.DepartTree;
import com.github.wxiaoqi.security.admin.vo.GroupTree;
import com.github.wxiaoqi.security.common.biz.BusinessBiz;
import com.github.wxiaoqi.security.common.util.UUIDUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.AG
 * @version 2018-02-04 19:06:43
 * @email 463540703@qq.com
 */
@Service
public class PositionBiz extends BusinessBiz<PositionMapper, Position> {
    /**
     * 更改岗位用户
     *
     * @param positionId
     * @param users
     */
    @CacheClear(pre = "permission")
    public void modifyPositionUsers(String positionId, String users) {
        mapper.deletePositionUsers(positionId);
        if (StringUtils.isNotBlank(users)) {
            for (String uId : users.split(",")) {
                mapper.insertPositionUser(UUIDUtils.generateUuid(), positionId, uId, BaseContextHandler.getTenantID());
            }
        }
    }

    /**
     * 获取用户流程关联岗位
     *
     * @param userId
     * @return
     */
    public List<Position> getUserFlowPosition(String userId) {
        return mapper.selectUserFlowPosition(userId);
    }

    /**
     * 获取岗位用户
     *
     * @param positionId
     * @return
     */
    public List<User> getPositionUsers(String positionId) {
        return mapper.selectPositionUsers(positionId);
    }

    public void modifyPositionGroups(String positionId, String groups) {
        mapper.deletePositionGroups(positionId);
        if (StringUtils.isNotBlank(groups)) {
            for (String groupId : groups.split(",")) {
                mapper.insertPositionGroup(UUIDUtils.generateUuid(), positionId, groupId, BaseContextHandler.getTenantID());
            }
        }
    }

    public List<GroupTree> getPositionGroups(String positionId) {
        List<Group> groups = mapper.selectPositionGroups(positionId);
        List<GroupTree> trees = new ArrayList<GroupTree>();
        GroupTree node = null;
        for (Group group : groups) {
            node = new GroupTree();
            node.setLabel(group.getName());
            BeanUtils.copyProperties(group, node);
            trees.add(node);
        }
        return trees;
    }

    public void modifyPositionDeparts(String positionId, String departs) {
        mapper.deletePositionDeparts(positionId);
        if (StringUtils.isNotBlank(departs)) {
            for (String groupId : departs.split(",")) {
                mapper.insertPositionDepart(UUIDUtils.generateUuid(), positionId, groupId, BaseContextHandler.getTenantID());
            }
        }
    }

    public List<DepartTree> getPositionDeparts(String positionId) {
        List<Depart> departs = mapper.selectPositionDeparts(positionId);
        List<DepartTree> trees = new ArrayList<>();
        departs.forEach(depart -> {
            trees.add(new DepartTree(depart.getId(), depart.getParentId(), depart.getName(), depart.getCode()));
        });
        return trees;
    }

    @Override
    public void insertSelective(Position entity) {
        String departId = entity.getDepartId();
        entity.setId(UUIDUtils.generateUuid());
        super.insertSelective(entity);
        entity.setDepartId(departId);
        updateSelectiveById(entity);
    }
}
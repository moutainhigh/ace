package com.github.wxiaoqi.security.admin.vo;

import com.github.wxiaoqi.security.common.vo.TreeNodeVO;

/**
 * @author ace
 * @create 2018/2/4.
 */
public class DepartTree extends TreeNodeVO<DepartTree> {
    String label;
    String code;

    public DepartTree() {

    }

    public DepartTree(Object id, Object parentId, String label, String code) {
        this.label = label;
        this.code = code;
        this.setId(id);
        this.setParentId(parentId);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

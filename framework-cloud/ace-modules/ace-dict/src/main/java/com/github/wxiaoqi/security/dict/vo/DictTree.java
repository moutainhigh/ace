package com.github.wxiaoqi.security.dict.vo;

import com.github.wxiaoqi.security.common.vo.TreeNodeVO;

/**
 * @author ace
 * @create 2018/1/30.
 */
public class DictTree extends TreeNodeVO<DictTree> {
    String label;
    String code;

    public DictTree() {

    }

    public DictTree(Object id, Object parentId, String label, String code) {
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

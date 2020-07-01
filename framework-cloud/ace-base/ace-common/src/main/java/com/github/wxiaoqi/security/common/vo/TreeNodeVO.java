package com.github.wxiaoqi.security.common.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ace on 2017/6/12.
 */
public class TreeNodeVO<T> {
    protected Object id;
    protected Object parentId;
    List<T> children = new ArrayList<T>();

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getParentId() {
        return parentId;
    }

    public void setParentId(Object parentId) {
        this.parentId = parentId;
    }

    public void add(T node) {
        children.add(node);
    }

}

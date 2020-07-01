package com.github.wxiaoqi.security.common.util;

import com.github.wxiaoqi.security.common.vo.TreeNodeVO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Ace on 2017/6/12.
 */
public class TreeUtil<T extends TreeNodeVO> {
    /**
     * 两层循环实现建树
     *
     * @param treeNodes  传入的树节点列表
     * @param comparator
     * @return
     */
    public static <T extends TreeNodeVO> List<T> bulid(List<T> treeNodes, Object root, Comparator comparator) {

        List<T> trees = new ArrayList<T>();

        for (T treeNode : treeNodes) {

            if (root.equals(treeNode.getParentId())) {
                trees.add(treeNode);
            }

            for (T it : treeNodes) {
                if (it.getParentId().equals(treeNode.getId())) {
                    if (treeNode.getChildren() == null) {
                        treeNode.setChildren(new ArrayList<>());
                    }
                    treeNode.add(it);
                }
            }
            if (comparator != null) {
                treeNode.getChildren().sort(comparator);
            }
        }
        if(trees!=null && comparator != null){
            trees.sort(comparator);
        }
        return trees;
    }

    /**
     * 使用递归方法建树
     *
     * @param treeNodes
     * @return
     */
    public static <T extends TreeNodeVO> List<T> buildByRecursive(List<T> treeNodes, Object root) {
        List<T> trees = new ArrayList<T>();
        for (T treeNode : treeNodes) {
            if (root.equals(treeNode.getParentId())) {
                trees.add(findChildren(treeNode, treeNodes));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     *
     * @param treeNodes
     * @return
     */
    public static <T extends TreeNodeVO> T findChildren(T treeNode, List<T> treeNodes) {
        for (T it : treeNodes) {
            if (treeNode.getId().equals(it.getParentId())) {
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<TreeNodeVO>());
                }
                treeNode.add(findChildren(it, treeNodes));
            }
        }
        return treeNode;
    }

}
